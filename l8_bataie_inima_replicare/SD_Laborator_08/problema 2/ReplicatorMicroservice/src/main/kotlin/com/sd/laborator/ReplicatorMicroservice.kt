package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class ReplicatorMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private lateinit var replicatorSocket: ServerSocket

    companion object Constants {
        const val REPLICATOR_SOCKET = 2001
    }

    init {
        subscribers = hashMapOf()
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        replicatorSocket = ServerSocket(REPLICATOR_SOCKET)
        println("Replicator se executa pe portul: ${replicatorSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = replicatorSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            thread {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                // se citeste raspunsul de pe socketul TCP
                while(true) {
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
                    }

                    println("Replic: $receivedMessage")
                    val (img, name, network) = receivedMessage.split("~", limit = 3)

                    startService(img, name, network)
                }
            }
        }
    }

    private fun startService(img: String, name: String, network: String) {
        println(BufferedReader(InputStreamReader(Runtime.getRuntime().exec("docker rm \$(docker ps -a -q)").inputStream)).readLines().joinToString("\n"))
        println(BufferedReader(InputStreamReader(Runtime.getRuntime().exec("docker run -d --name ${name} --network=${network} ${img}").inputStream)).readLines().joinToString("\n"))
    }
}

fun main() {
    val replicatorMicroservice = ReplicatorMicroservice()
    replicatorMicroservice.run()
}
