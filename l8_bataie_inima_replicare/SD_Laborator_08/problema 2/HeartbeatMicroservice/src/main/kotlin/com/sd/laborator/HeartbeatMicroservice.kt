package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class HeartbeatService {
    private val subscribers: HashMap<Process, Socket>
    private lateinit var heartbeatSocket: ServerSocket
    private lateinit var replicatorSocket: Socket

    companion object Constants {
        const val HEARTBEAT_PORT = 2000
        const val REPLICATOR_PORT = 2001
        val REPLICATOR_HOST = System.getenv("REPLICATOR_HOST") ?: "localhost"
    }

    init {
        subscribers = hashMapOf()
    }

    private fun restartService(proc: com.sd.laborator.Process) {
        synchronized(subscribers)
        {
            subscribers.remove(proc)
        }
        replicatorSocket.getOutputStream().write("${proc.img}~${proc.name}~${proc.network}\n".toByteArray())
    }

    private fun subscribeToReplicator() {
        try {
            replicatorSocket = Socket(REPLICATOR_HOST, REPLICATOR_PORT)
        } catch (e: Exception) {
            exitProcess(1)
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        heartbeatSocket = ServerSocket(HEARTBEAT_PORT)
        subscribeToReplicator()
        println("Heartbeat se executa pe portul: ${heartbeatSocket.localPort}")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = heartbeatSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            thread {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")
                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                val connectData = bufferReader.readLine() // name~host~port~img
                println(connectData)
                val (name,port,img,network) = connectData.split("~",limit=4)
                val thisProc = com.sd.laborator.Process(name,port.toInt(),img, network)
                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    subscribers[thisProc] = clientConnection
                }

                var retries = 0 // 2 retries ca n-am chef
                while (clientConnection.isConnected) {
                    clientConnection.soTimeout = 1500
                    try {
                        clientConnection.getOutputStream().write("heartbeat\n".toByteArray())
                        val resp = bufferReader.readLine() ?: throw SocketException()
                        println(resp)
                        retries = 0
                        sleep(1000)
                    }
                    catch (ex: Exception){
                        retries++
                        println("Failed")
                        if(retries > 2) {
                            println("Restartam proc: ${thisProc.name}")
                            clientConnection.close()
                            restartService(thisProc)
                            break
                        }
                    }

                }
            }
        }
    }
}

fun main() {
    val heartbeatService = HeartbeatService()
    heartbeatService.run()
}
