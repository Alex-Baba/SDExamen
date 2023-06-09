package com.sd.laborator

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class MessageManagerMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private lateinit var messageManagerSocket: ServerSocket
    private lateinit var loggerSocket: Socket

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
    }

    init {
        subscribers = hashMapOf()
    }

    private fun broadcastMessage(message: String, except: Int) {
        subscribers.forEach {
            it.takeIf { it.key != except }
                ?.value?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    private fun respondTo(destination: Int, message: String) {
        subscribers[destination]?.getOutputStream()?.write((message + "\n").toByteArray())
    }

    @OptIn(DelicateCoroutinesApi::class)
    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        messageManagerSocket = ServerSocket(MESSAGE_MANAGER_PORT)
        println("MessageManagerMicroservice se executa pe portul: ${messageManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        //logger-ul trebuie sa se conecteze primul...poate fi imbunatatit sa poata fi conectat oricand
        loggerSocket = messageManagerSocket.accept()
        println("S-a conectat loggerul")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = messageManagerSocket.accept()

            // se porneste o corutina separat pentru tratarea conexiunii cu clientul
            GlobalScope.launch {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        synchronized(subscribers) {
                            subscribers.remove(clientConnection.port)
                        }
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("Primit mesaj: $receivedMessage")
                    val (messageType, messageDestination, messageBody) = receivedMessage.split(" ", limit = 3)
                    when (messageType) {
                        "intrebare" -> {
                            // tipul mesajului de tip intrebare este de forma:
                            // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                            broadcastMessage("intrebare ${clientConnection.port} $messageBody", except = clientConnection.port)
                            loggerSocket.getOutputStream().write("startConvo\n".toByteArray())
                            loggerSocket.getOutputStream().write("$receivedMessage\n".toByteArray())
                        }
                        "raspuns" -> {
                            // tipul mesajului de tip raspuns este de forma:
                            // raspuns <CONTINUT_RASPUNS>
                            respondTo(messageDestination.toInt(), messageBody)
                            loggerSocket.getOutputStream().write("$receivedMessage\n".toByteArray())
                            loggerSocket.getOutputStream().write("endConvo\n".toByteArray())
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}
