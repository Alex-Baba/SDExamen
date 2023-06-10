package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.System.exit
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.system.exitProcess

class StudentMicroservice {
    // intrebarile si raspunsurile sunt mentinute intr-o lista de perechi de forma:
    // [<INTREBARE 1, RASPUNS 1>, <INTREBARE 2, RASPUNS 2>, ... ]
    private lateinit var questionDatabase: MutableList<Pair<String, String>>
    private lateinit var messageManagerSocket: Socket
    private lateinit var heartbeatSocket: Socket
    private val id: Int = Random.nextInt(6) + 1

    init {
        val databaseLines: List<String> = File("questions_database.txt").readLines()
        questionDatabase = mutableListOf()

        /*
         "baza de date" cu intrebari si raspunsuri este de forma:

         <INTREBARE_1>\n
         <RASPUNS_INTREBARE_1>\n
         <INTREBARE_2>\n
         <RASPUNS_INTREBARE_2>\n
         ...
         */
        for (i in 0..(databaseLines.size - 1) step 2) {
            questionDatabase.add(Pair(databaseLines[i], databaseLines[i + 1]))
            println(Pair(databaseLines[i], databaseLines[i + 1]))
        }
    }

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        val HEARTBEAT_HOST = System.getenv("HEARTBEAT_HOST") ?: "localhost"
        const val HEARTBEAT_PORT = 1700
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
            messageManagerSocket.getOutputStream().write("student ${messageManagerSocket.localPort} da\n".toByteArray())
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun subscribeToHeartbeat() {
        try {
            heartbeatSocket = Socket(HEARTBEAT_HOST, HEARTBEAT_PORT)
            println("M-am conectat la Heartbeat!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la Heartbeat!")
            exitProcess(1)
        }
    }

    private suspend fun respondToQuestion(question: String): String? {
        questionDatabase.forEach {
            // daca se gaseste raspunsul la intrebare, acesta este returnat apelantului
            if (it.first == question) {
                return it.second
            }
        }
        return null
    }

    public fun run() {
        subscribeToMessageManager()
        subscribeToHeartbeat()
        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiun

        println("StudentMicroservice se executa pe portul: ${messageManagerSocket.localPort} cu id-ul $id")
        println("Se asteapta mesaje...")

        thread {
            val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

            while (true) {
                // se asteapta intrebari trimise prin intermediarul "MessageManager"
                val response = bufferReader.readLine()

                if (response == null) {
                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    println("Microserviciul MessageService (${messageManagerSocket.port}) a fost oprit.")
                    bufferReader.close()
                    messageManagerSocket.close()
                    break
                }

                // se foloseste un thread separat pentru tratarea intrebarii primite
                GlobalScope.launch {
                    println(response)
                    val (messageType, messageDestination, messageBody) = response.split(" ", limit = 3)
                    println("Am primit $response")
                    when (messageType) {
                        // tipul mesajului cunoscut de acest microserviciu este de forma:
                        // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                        "intrebare" -> {
                            var responseToQuestion = respondToQuestion(messageBody)
                            responseToQuestion?.let {
                                responseToQuestion = "raspuns $messageDestination $it~$id"
                                println("Trimit raspunsul: \"${response}\"")
                                messageManagerSocket.getOutputStream().write((responseToQuestion + "\n").toByteArray())
                            }
                        }
                        "prezenta" -> {
                            messageManagerSocket.getOutputStream().write(("prezent $messageDestination $id\n").toByteArray())
                        }
                        "esti-impostor" -> {
                            println("Am fost prins facand evaziune fiscala!")
                            exitProcess(0)
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
    val studentMicroservice = StudentMicroservice()
    studentMicroservice.run()
}