package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class HeartbeatMicroservice {
    private var servicesSocketList: MutableList<Socket> = mutableListOf()
    private lateinit var heartbeatSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        const val HEARTBEAT_PORT = 1700
        const val HEARTBEAT_DELAY: Long = 5000
        const val RESPONSE_TIMEOUT = 100000
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        heartbeatSocket = ServerSocket(HEARTBEAT_PORT, 100)

        println("Heartbeat se executa pe portul: ${heartbeatSocket.localPort}")
        println("Se asteapta microservicii pentru monitorizat...")

        thread {
            while (true) {
                // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
                // (in acest caz, din partea aplicatiei client GUI)
                val clientConnection = heartbeatSocket.accept()
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")
                synchronized(servicesSocketList){
                    servicesSocketList.add(clientConnection)
                }
            }
        }

        thread {
            while (true) {
                Thread.sleep(HEARTBEAT_DELAY)
                servicesSocketList.forEach(::println)
                val deadServices = mutableListOf<Socket>()
                servicesSocketList.forEach {
                    it.getOutputStream().write(("heartbeat" + '\n').toByteArray())
                    println("Sent to ${it.inetAddress.hostAddress}:${it.port} -> Heartbeat")
                    var responded = false
                    val bufferStream = BufferedInputStream(it.getInputStream())
                    val bufferReader = BufferedReader(InputStreamReader(it.getInputStream()))
                    try {
                        var timeout = RESPONSE_TIMEOUT
                        while(timeout > 0 && !responded)
                        {
                            responded = bufferStream.available() > 0
                            timeout--
                        }
                        if(responded) {
                            val response = bufferReader.readLine()
                            println(response)
                            if (response != "alive")
                                responded = false
                        }
                    } catch (exp: Exception) {
                        responded = false
                    }
                    if(!responded)
                    {
                        deadServices.add(it)
                    }
                }
                deadServices.forEach {
                    println("${it.inetAddress.hostAddress}:${it.port} a crapat!")
                    it.close()
                    servicesSocketList.remove(it)
                }
            }
        }
    }
}

fun main() {
    val heartbeatMicroservice = HeartbeatMicroservice()
    heartbeatMicroservice.run()
}
