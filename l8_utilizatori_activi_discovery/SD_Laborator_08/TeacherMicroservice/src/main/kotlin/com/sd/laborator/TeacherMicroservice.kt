package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.util.UUID
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherMicroservice {
    //mi-e sila sa shimb numele la messageManagerSocket
    private lateinit var messageManagerSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket
    private val uuid = UUID.randomUUID()

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        val TEACHER_PORT = (System.getenv("TEACHER_PORT") ?: "1600").toInt()
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)

            messageManagerSocket.getOutputStream().write("sub~{\"name\":\"teacher${uuid}\",\"host\":\"localhost\",\"port\":\"${TEACHER_PORT}\"}\n".toByteArray())
            //messageManagerSocket.soTimeout = 3000
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun unsubFromMessageManager() {
        messageManagerSocket.getOutputStream().write("unsub~teacher${uuid}\n".toByteArray())
    }

    private fun askStudent(host:String, port:Int, intrebare:String) : String {
        val studentSocket = Socket(host,port)
        studentSocket.getOutputStream().write("intrebare~${intrebare}\n".toByteArray())
        println("wrote!")
        val resp = BufferedReader(InputStreamReader(studentSocket.getInputStream())).readLine()
        return resp
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        teacherMicroserviceServerSocket = ServerSocket(TEACHER_PORT)

        println("TeacherMicroservice se executa pe portul: ${teacherMicroserviceServerSocket.localPort}")
        println("Se asteapta cereri (intrebari)...")

        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = teacherMicroserviceServerSocket.accept()

            // se foloseste un thread separat pentru tratarea fiecarei conexiuni client
            thread {
                println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val receivedQuestion = clientBufferReader.readLine()
                val messageManagerBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

                //intreaba de studenti
                messageManagerSocket.getOutputStream().write("find~student.*\n".toByteArray())
                val resp = messageManagerBufferReader.readLine()
                println(resp)
                if (resp.contains("200")) {
                    //println("Trimit catre MessageManager: ${"intrebare ${messageManagerSocket.localPort} $receivedQuestion\n"}")
                    //messageManagerSocket.getOutputStream().write(("intrebare ${messageManagerSocket.localPort} $receivedQuestion\n").toByteArray())

                    //trimite intrebarea la studenti
                    //poate merge asincron facut, can't be bothered
                    resp.split("~")[1].split(",").forEach {
                        if(it != "") {
                            val (host, port) = it.split(":")
                            println(it)
                            val resp = askStudent(host, port.toInt(), receivedQuestion)
                            println(resp)
                            if (resp != "NU STIU!") {
                                println("Am primit raspunsul: \"$resp\"")
                                clientConnection.getOutputStream().write((resp + "\n").toByteArray())
                                clientConnection.close()
                                return@forEach
                            }
                        }
                    }

                    if(!clientConnection.isClosed) {
                        println("Nu a venit niciun raspuns in timp util.")
                        clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                        clientConnection.close()
                    }

                } else {
                    println("Nu a venit niciun raspuns in timp util.")
                    clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                }
            }
        }
        unsubFromMessageManager()
    }
}

fun main() {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}