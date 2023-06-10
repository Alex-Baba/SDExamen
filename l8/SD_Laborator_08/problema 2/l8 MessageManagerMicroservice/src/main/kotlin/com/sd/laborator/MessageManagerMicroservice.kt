package com.sd.laborator

import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class MessageManagerMicroservice {
    private val studentsList: MutableList<Int>
    private val teachersList: MutableList<Int>
    private val subList: HashMap<Int,Socket>
    private var catalogSocket: Socket? = null
    private lateinit var messageManagerSocket: ServerSocket
    private lateinit var heartbeatSocket: Socket
    private var nrIntrebari = AtomicInteger(0)

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
        val HEARTBEAT_HOST = System.getenv("HEARTBEAT_HOST") ?: "localhost"
        const val HEARTBEAT_PORT = 1700
    }

    init {
        studentsList = mutableListOf()
        teachersList = mutableListOf()
        subList = HashMap()
    }

    private  fun broadcastMessage(message: String) {
        studentsList.forEach {
            subList[it]?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    private  fun sendTo(destination: Socket, message: String) {
        destination.getOutputStream()?.write((message + "\n").toByteArray())
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

    fun run() {
        subscribeToHeartbeat()
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        messageManagerSocket = ServerSocket(MESSAGE_MANAGER_PORT, 100)
        println("MessageManagerMicroservice se executa pe portul: ${messageManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

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

//        thread {
            while (true) {
                // se asteapta conexiuni din partea clientilor subscriberi
                val clientConnection = messageManagerSocket.accept()

                // se porneste un thread separat pentru tratarea conexiunii cu clientul
                thread {

                    //acceptSubscribers
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subList) {
                    subList[clientConnection.port] = clientConnection
                }

                    val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                    while (true) {
                        // se citeste raspunsul de pe socketul TCP
                        val receivedMessage = bufferReader.readLine()

                        // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                        if (receivedMessage == null) {
                            // deci subscriber-ul respectiv a fost deconectat
                            println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                            synchronized(subList) {
                                subList.remove(clientConnection.port)
                            }
                            bufferReader.close()
                            clientConnection.close()
                            break
                        }
                        println("Primit mesaj: $receivedMessage")
                        val (messageType, messageDestination, messageBody) = receivedMessage.split(" ", limit = 3)

                        when (messageType) {
                            "intrebare" -> {//v
                                // tipul mesajului de tip intrebare este de forma:
                                // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                                nrIntrebari.set(nrIntrebari.get() + 1)
                                broadcastMessage(
                                    "intrebare ${clientConnection.port} $messageBody"
                                    //except = clientConnection.port
                                )
                            }
                            "raspuns" -> {//v
                                // tipul mesajului de tip raspuns este de forma:
                                // raspuns <CONTINUT_RASPUNS>~<student_id>
                                subList[messageDestination.toInt()]?.let { sendTo(it, messageBody.split("~")[0]) }
                                //nota <student_id> <intrebare>
                                catalogSocket?.getOutputStream()?.write("nota ${messageBody.split("~")[1]} ${nrIntrebari.get()}\n".toByteArray())
                            }
                            "prezenta" -> {//v
                                //prezenta <CINE A CERUT>
                                catalogSocket?.let { sendTo(it, "$messageType $messageDestination da") }
                                broadcastMessage("$messageType ${clientConnection.port} da")
                            }
                            "medii_vreau"-> {//v
                                //administrativ <CINE A CERUT>
                                catalogSocket?.let { sendTo(it, "$messageType $messageDestination $messageBody") }
                            }
                            "prezent" -> {//v
                                //prezent <dest> <student_id>
                                subList[messageDestination.toInt()]?.let { sendTo(it, "prezent ${clientConnection.port}:$messageBody")}
                            }
                            "impostor" -> {//v
                                //impostor <CINE A CERUT> student_port
                                subList[messageBody.toInt()]?.let { sendTo(it, "esti-impostor!") }
                                subList.remove(messageBody.toInt())

                            }
                            "student" -> {//v
                                //student
                                synchronized(studentsList) {
                                    studentsList.add(messageDestination.toInt())
                                }
                            }
                            "teacher" -> {//v
                                synchronized(studentsList) {
                                    teachersList.add(messageDestination.toInt())
                                }
                            }
                            "catalog" -> {//v
//                                catalogSocket?.let {
//                                    synchronized(it) {
//                                        catalogSocket = clientConnection
//                                    }
//                                }
                                catalogSocket = clientConnection
                            }
                            "lista" -> {//v
                                //lista <msgDest> <id1,id2,i3>
                                subList[messageDestination.toInt()]?.let { sendTo(it,"lista $messageBody") }
                            }
                            "medii" -> {//v
                                subList[messageDestination.toInt()]?.let { sendTo(it, "medii $messageBody") }
                            }
                        }
                    }
                }
            }
        //}


    }
}

fun main() {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}
