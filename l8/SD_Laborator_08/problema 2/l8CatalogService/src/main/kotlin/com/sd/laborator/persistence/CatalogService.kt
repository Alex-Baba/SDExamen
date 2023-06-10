package com.sd.laborator.persistence

import com.sd.laborator.persistence.entities.StudentNotat
import com.sd.laborator.persistence.repositories.StudentNotaRepository
import org.springframework.beans.factory.annotation.Autowired
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess

//@org.springframework.stereotype.Component
open class CatalogService {
    private lateinit var messageManagerSocket: Socket
    private lateinit var heartbeatSocket: Socket
    private lateinit var catalogServiceSocket: ServerSocket

    @Autowired
    private lateinit var repository: StudentNotaRepository

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1601
        val HEARTBEAT_HOST = System.getenv("HEARTBEAT_HOST") ?: "localhost"
        const val HEARTBEAT_PORT = 1700
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
            messageManagerSocket.getOutputStream().write("catalog ${messageManagerSocket.localPort} da\n".toByteArray())
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun subscribeToHeartbeat() {
        try {
            heartbeatSocket = Socket(HEARTBEAT_HOST, HEARTBEAT_PORT)
            heartbeatSocket.soTimeout = 3000
            println("M-am conectat la Heartbeat!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la Heartbeat!")
            exitProcess(1)
        }
    }

    private fun addNota(id: Int, intrebare: Long){
        val nota = StudentNotat(id, intrebare)
        repository.add(nota)
    }

    private fun getAverages(): HashMap<Int, Float> {
        val intrebari = repository.getInterbari()!!
        val mapa: HashMap<Int,Float> = HashMap()
        repository.getIds().forEach {
            mapa[it] = 0f
        }
        repository.getAll().forEach {
            if(!mapa.containsKey(it?.id))
            {
                mapa[it?.id!!] = 0f
            }
            mapa[it?.id!!] = mapa[it.id]!! + 1
        }

        mapa.map { it -> mapa[it.key] = it.value / intrebari }
        return mapa
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        subscribeToHeartbeat()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        catalogServiceSocket = ServerSocket(TEACHER_PORT)

        println("CatalogService se executa pe portul: ${catalogServiceSocket.localPort}")
        println("Se asteapta cereri...")
        repository.createTable()
        thread {
            while (true) {

                    val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

                    while (true) {
                        // se citeste raspunsul de pe socketul TCP
                        val receivedMessage = bufferReader.readLine()

                        println("Primit mesaj: $receivedMessage")
                        val (messageType, messageDestination, messageBody) = receivedMessage.split(" ", limit = 3)

                        when (messageType) {
                            "prezenta" -> {//v
                                //prezenta <CINE A CERUT>
                                var lista = ""
                                repository.getIds().forEach {
                                    lista += "$it,"
                                }
                                println(lista.dropLast(1))
                                messageManagerSocket.getOutputStream().write("lista $messageDestination ${lista.dropLast(1)}\n".toByteArray())
                            }
                            "medii_vreau"-> {//v
                                var lista = ""
                                getAverages().forEach {
                                    lista+= "${it.key}-${it.value},"
                                }
                                println(lista)
                                messageManagerSocket.getOutputStream().write("medii $messageDestination ${lista.dropLast(1)}\n".toByteArray())
                            }
                            "nota" -> {//v
                                addNota(messageDestination.toInt(), messageBody.toLong())
                            }
                        }
                    }
            }
        }

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
    }
}