package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherClient(public val socket:Socket){
    public var tag:String = ""
}


class TeacherManagerMicroservice {
    private val teacher_subscribers: HashMap<Int, TeacherClient>

    private lateinit var teacherManagerSocket: ServerSocket
    private lateinit var messageManagerSocket: Socket

    companion object Constants {
        const val TEACHER_MANAGER_PORT = 1700

        //Connect to message manager
        const val MESSAGE_MANAGER_PORT = 1500
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
    }

    init {
        teacher_subscribers = hashMapOf()
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            //messageManagerSocket.soTimeout = 3000
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }


    private fun broadcastMessage(message: String) {
        teacher_subscribers.forEach {
            it?.value?.socket?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    private fun multicastMessage(message: String, tag: String) {
        teacher_subscribers.forEach {
            it.takeIf { it.value.tag == tag }?.value?.socket?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    private fun respondTo(destination: Int, receivedMessage: String) {
        print("Trimitel la destinatia: " + destination)
        teacher_subscribers[destination]?.socket?.getOutputStream()?.write((receivedMessage + "\n").toByteArray())
    }


    public fun run() {
        subscribeToMessageManager()
        // se porneste un socket server TCP pe portul 1700 care asculta pentru conexiuni


        //Here we listen to the boss manager
        val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

        //Primim mesaje de la message manager
        thread {
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

                //Primim mesaj pe bune
                println("Primit mesaj: $response")
                val (messageType, messageDestination,sourceTeacher, messageBody) = response.split(" ", limit = 4)

                when (messageType) {
                    "raspuns" -> {
                       // respondTo(sourceTeacher.toInt(), messageBody)
                        multicastMessage(messageBody, "matematician")
                    }

                }
            }
        }


        //Here we listen to subcriber
        teacherManagerSocket = ServerSocket(TEACHER_MANAGER_PORT)

        println("Teacher Manager se executa pe portul: ${teacherManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            val clientConnection = teacherManagerSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            thread {
                println("Profesor conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(teacher_subscribers) {
                    teacher_subscribers[clientConnection.port] = TeacherClient(clientConnection)
                }

                val bufferReaderTeachers = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    val receivedMessage = bufferReaderTeachers.readLine()

                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        synchronized(teacher_subscribers) {
                            teacher_subscribers.remove(clientConnection.port)
                        }
                        bufferReaderTeachers.close()
                        clientConnection.close()
                        break
                    }

                    //Primim mesaj pe bune
                    println("Primit mesaj: $receivedMessage")
                    val (messageType, messageDestination, sourceTeacher ,messageBody) = receivedMessage.split(" ", limit = 4)

                    when (messageType) {
                        //De rescris

                        "intrebare" -> {
                            println("Am primit o intrebare de la $messageDestination: \"${messageBody}\"")

                            //Trebuie sa ii dam forward
                            messageManagerSocket.getOutputStream().write((receivedMessage + '\n').toByteArray())
                        }
                        "tag" -> {
                            teacher_subscribers[messageDestination.toInt()]?.tag = sourceTeacher
                            println("Am asignat tagul " + sourceTeacher + " to subcriber with port " + messageDestination.toInt())
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val teacherManagerMicroservice = TeacherManagerMicroservice()
    teacherManagerMicroservice.run()
}
