package com.sd.laborator

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class StudentMicroservice {
    // intrebarile si raspunsurile sunt mentinute intr-o lista de perechi de forma:
    // [<INTREBARE 1, RASPUNS 1>, <INTREBARE 2, RASPUNS 2>, ... ]
    private lateinit var questionDatabase: MutableList<Pair<String, String>>
    private lateinit var messageManagerSocket: Socket
    private lateinit var studentMicroserviceServerSocket: ServerSocket
    private val uuid = UUID.randomUUID()

    init {
        val databaseLines: List<String> = File("/home/student/SD/susDE/sub_practice_rez/l8_utilizatori_activi_discovery/SD_Laborator_08/StudentMicroservice/questions_database.txt").readLines()
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
        }
    }

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        val STUDENT_PORT = (System.getenv("STUDENT_PORT") ?: "1700").toInt()
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)

            messageManagerSocket.getOutputStream().write("sub~{\"name\":\"student${uuid}\",\"host\":\"localhost\",\"port\":\"${STUDENT_PORT}\"}\n".toByteArray())
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

    private fun respondToQuestion(question: String): String? {
        questionDatabase.forEach {
            // daca se gaseste raspunsul la intrebare, acesta este returnat apelantului
            if (it.first == question) {
                return it.second
            }
        }
        return null
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        studentMicroserviceServerSocket = ServerSocket(STUDENT_PORT)

        println("StudentMicroservice se executa pe portul: ${studentMicroserviceServerSocket.localPort}")
        println("Se asteapta mesaje...")

        val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

        while (true) {

            val clientConnection = studentMicroserviceServerSocket.accept()
            println("connected")
            thread {
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val receivedQuestion = clientBufferReader.readLine()

                if (receivedQuestion == null) {
                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    clientBufferReader.close()
                    clientConnection.close()
                    return@thread
                }
                println(receivedQuestion)
                val (messageType,messageBody) = receivedQuestion.split("~")
                when(messageType) {
                    // tipul mesajului cunoscut de acest microserviciu este de forma:
                    // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                    "intrebare" -> {
                        println("Am primit o intrebare ${messageBody}\"")
                        var responseToQuestion = respondToQuestion(messageBody)
                        responseToQuestion?.let {
                            clientConnection.getOutputStream().write((it + "\n").toByteArray())
                        } ?: run {
                            clientConnection.getOutputStream().write(("NU STIU!\n").toByteArray())
                        }
                    }
                }
                clientConnection.close()
            }
        }
    }
}

fun main() {
    val studentMicroservice = StudentMicroservice()
    studentMicroservice.run()
}