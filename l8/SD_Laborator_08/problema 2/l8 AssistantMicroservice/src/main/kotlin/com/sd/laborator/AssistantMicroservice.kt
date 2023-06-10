package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class AssistantMicroservice {
    private lateinit var messageManagerSocket: Socket
    private lateinit var heartbeatSocket: Socket
    private lateinit var assistantSocket: ServerSocket
    private val listaStudenti = mutableListOf<Int>()

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1602
        val HEARTBEAT_HOST = System.getenv("HEARTBEAT_HOST") ?: "localhost"
        const val HEARTBEAT_PORT = 1700
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 3000
            println("M-am conectat la MessageManager!")
            messageManagerSocket.getOutputStream().write("teacher ${messageManagerSocket.localPort} da\n".toByteArray())
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun subscribeToHeartbeat() {
        try {
            heartbeatSocket = Socket(HEARTBEAT_HOST, HEARTBEAT_PORT)
            heartbeatSocket.soTimeout = 3000
            println("M-am conectat la Heartbeat!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la Heartbeat!")
            exitProcess(1)
        }
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        subscribeToHeartbeat()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        assistantSocket = ServerSocket(TEACHER_PORT)

        println("AssistantMicroservice se executa pe portul: ${assistantSocket.localPort}")
        println("Asta face prezenta")

        thread {
            messageManagerSocket.getOutputStream().write("prezenta ${messageManagerSocket.localPort} da\n".toByteArray())
            while (true) {
                // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
                // (in acest caz, din partea aplicatiei client GUI)
                val clientConnection = assistantSocket.accept()

                // se foloseste un thread separat pentru tratarea fiecarei conexiuni client
                GlobalScope.launch {
                    println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                    // se citeste intrebarea dorita
                    val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                    val (messageType, receivedQuestion) = clientBufferReader.readLine().split(" ", limit = 2)

                    when (messageType) {
                        "raspuns" -> {
                            try {
                                val receivedResponse =
                                    BufferedReader(InputStreamReader(messageManagerSocket.getInputStream())).readLine()

                                // se trimite raspunsul inapoi clientului apelant
                                println("Am primit raspunsul: \"$receivedResponse\"")
                                clientConnection.getOutputStream().write((receivedResponse + "\n").toByteArray())
                            } catch (e: SocketTimeoutException) {
                                println("Nu a venit niciun raspuns in timp util.")
                                clientConnection.getOutputStream()
                                    .write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                            } finally {
                                // se inchide conexiunea cu clientul
                                clientConnection.close()
                            }
                        }
                    }
                }
            }
        }

        thread {
            while (true) {
                val managerBuffer = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
                val msg = managerBuffer.readLine()
                println(msg)
                if (msg == ""){
                    continue
                }
                val (messageType, receivedQuestion) = msg.split(" ", limit = 2)
                println(receivedQuestion)
                when (messageType) {
                    "lista" -> {
                        receivedQuestion.split(",").forEach {
                            listaStudenti.add(it.toInt())
                        }
                    }
                    "prezent" -> {
                        val (port, id) = receivedQuestion.split(":", limit = 2)
                        println(id)
                        if (!listaStudenti.contains(id.toInt())) {
                            println("impostor $id")
                            messageManagerSocket.getOutputStream()
                                .write("impostor ${messageManagerSocket.localPort} $port\n".toByteArray())
                        }
                    }
                }
            }
        }

        thread {
            val bufferReader = BufferedReader(InputStreamReader(heartbeatSocket.getInputStream()))

            while(true){
                if(heartbeatSocket.getInputStream().available() > 0) {
                    val msg = bufferReader.readLine()

                    if(msg == "heartbeat")
                    {
                        heartbeatSocket.getOutputStream().write(("alive" + '\n').toByteArray())
                        println("Heartbeat!")
                    }
                }
            }
        }
    }
}

fun main() {
    val assistantService = AssistantMicroservice()
    assistantService.run()
}