package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherMicroservice {
    private lateinit var teacherManagerSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val TEACHER_MANAGER_HOST = System.getenv("TEACHER_MANAGER_HOST") ?: "localhost"
        const val TEACHER_MANAGER_PORT = 1700
        const val TEACHER_PORT = 1600
    }

    private fun subscribeToTeacherManager() {
        try {
            teacherManagerSocket = Socket(TEACHER_MANAGER_HOST, TEACHER_MANAGER_PORT)
            teacherManagerSocket.soTimeout = 3000
            println("M-am conectat la TeacherManager!")

            teacherManagerSocket.getOutputStream().write(("tag ${teacherManagerSocket.localPort} matematician none\n").toByteArray())
            println("Am trimis tag-ul!")

        } catch (e: Exception) {
            println("Nu ma pot conecta la TeacherManager!")
            exitProcess(1)
        }
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToTeacherManager()

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

                // intrebarea este redirectionata catre microserviciul MessageManager
                println("Trimit catre MessageManager: ${"intrebare ${teacherManagerSocket.localPort} $receivedQuestion\n"}")
                teacherManagerSocket.getOutputStream().write(("intrebare ${teacherManagerSocket.localPort} ${teacherManagerSocket.localPort} $receivedQuestion\n").toByteArray())

                // se asteapta raspuns de la MessageManager
                val messageManagerBufferReader = BufferedReader(InputStreamReader(teacherManagerSocket.inputStream))
                try {
                    val receivedResponse = messageManagerBufferReader.readLine()

                    // se trimite raspunsul inapoi clientului apelant
                    println("Am primit raspunsul: \"$receivedResponse\"")
                    clientConnection.getOutputStream().write((receivedResponse + "\n").toByteArray())
                } catch (e: SocketTimeoutException) {
                    println("Nu a venit niciun raspuns in timp util.")
                    clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                } finally {
                    // se inchide conexiunea cu clientul
                    clientConnection.close()
                }
            }
        }
    }
}

fun main() {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}