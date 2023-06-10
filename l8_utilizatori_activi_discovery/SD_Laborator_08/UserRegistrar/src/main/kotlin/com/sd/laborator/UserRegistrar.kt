package com.sd.laborator

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class UserRegistrar {
    private var subscribers: MutableList<User> = mutableListOf()
    private lateinit var registrarSocket: ServerSocket

    companion object Constants {
        const val REGISTRAR_PORT = 1500
    }

    /*private fun broadcastMessage(message: String, except: Int) {
        subscribers.forEach {
            it.takeIf { it.key != except }
                ?.value?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    private fun respondTo(destination: Int, message: String) {
        subscribers[destination]?.getOutputStream()?.write((message + "\n").toByteArray())
    }*/

    private fun addSubscriber(msg: String, conn: Socket) {
        // adaugarea in lista de subscriberi trebuie sa fie atomica!
        val builder = GsonBuilder()
        builder.registerTypeAdapter(User::class.java, UserTypeAdapter())
        val gson = builder.create()

        val user = gson.fromJson(msg, User::class.java)
        user.conn = conn
        synchronized(subscribers) {
            subscribers.add(user)
        }
    }

    private fun removeSubscriberByName(msg: String?) {
        // stergerea din lista de subscriberi trebuie sa fie atomica!
        synchronized(subscribers) {
            subscribers = subscribers.filter { it.name == msg }.toMutableList()
        }
    }

    private fun removeSubscriberByConn(conn: Socket) {
        // stergerea din lista de subscriberi trebuie sa fie atomica!
        synchronized(subscribers) {
            subscribers = subscribers.filter { it.conn == conn }.toMutableList()
        }
    }

    //find~<NUMELE> ... NUMELE -> poate fi un regex, ex 'student*' ceea ce inseamna ca vrea toti studentii
    private fun finder(msg: String, conn: Socket) {
        val regexPattern = msg.toRegex()
        var usersToFind: MutableList<User> = mutableListOf()
        subscribers.forEach {
            if(regexPattern.matches(it.name)){
                usersToFind.add(it)
            }
        }
        if(usersToFind.size == 0) {
            conn.getOutputStream().write("404~NotFound\n".toByteArray())
        } else {
            println(usersToFind.joinToString { "${it.host}:${it.port}," })
            //200-<HOST>:<PORT>,<HOST>:<PORT>,...
            conn.getOutputStream().write("200~${usersToFind.joinToString { "${it.host}:${it.port}," }}\n".toByteArray())
        }
    }

    private fun messageParser(msg: String, conn: Socket) {
        println(msg)
        val (type, msgContent) = msg.split("~")
        when(type) {
            "sub" -> {
                addSubscriber(msgContent, conn)
            }
            "unsub" -> {
                removeSubscriberByConn(conn)
            }
            "find" -> {
                finder(msgContent, conn)
            }
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        registrarSocket = ServerSocket(REGISTRAR_PORT)
        println("Registrar se executa pe portul: ${registrarSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = registrarSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            thread {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")
                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        removeSubscriberByConn(clientConnection)
                        break
                    }

                    println("Primit mesaj: $receivedMessage")
                    messageParser(receivedMessage, clientConnection)
                }
            }
        }
    }
}

fun main() {
    val registrar = UserRegistrar()
    registrar.run()
}
