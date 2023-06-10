package com.sd.laborator

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread


class MessageManagerMicroservice {
    private var subscribers: MutableList<com.sd.laborator.Process> = mutableListOf()
    private lateinit var registerSocket: ServerSocket

    companion object Constants {
        const val REGISTER_PORT = 1789
    }

    private fun parseMessage(msg: String?, currentProcess: Socket) {
        println(msg)
        if (msg != null) {
            when(msg.split("~")[0]) {
                "sub" -> {
                    addSubscriber(msg.split("~")[1], currentProcess)
                }
                "unsub", "" -> {
                    removeSubByConn(currentProcess)
                    currentProcess.close()
                }
                "print" -> {println(msg.split("~")[1])}
            }
        }else {
            removeSubByConn(currentProcess)
            currentProcess.close()
        }
        //e cam monkey cum fac asta aici, nut whatever
    }

    private fun addSubscriber(json: String, conn: Socket): Process {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(com.sd.laborator.Process::class.java, ProcessAdapter())
        val gson = builder.create()
        val newProcess = gson.fromJson(json, com.sd.laborator.Process::class.java)
        newProcess.conn = conn
        println("S-a inregistrat cineva: ${newProcess.name}")
        synchronized(subscribers) {
            subscribers.add(newProcess)
        }
        return newProcess
    }

    private fun removeSubByConn(conn: Socket) {
        println("unsubbed")
        synchronized(subscribers) {
            subscribers = subscribers.filter { it.conn != conn }.toMutableList()
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        registerSocket = ServerSocket(REGISTER_PORT)
        println("Register se executa pe portul: ${registerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = registerSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            thread {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                while (true) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()
                    val proc = parseMessage(receivedMessage,clientConnection)
                    if(clientConnection.isClosed)
                        break
                }
            }
        }
    }
}

fun main() {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}
