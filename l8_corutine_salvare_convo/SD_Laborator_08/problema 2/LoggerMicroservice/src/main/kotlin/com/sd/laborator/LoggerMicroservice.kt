package com.sd.laborator

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/*
    Ce naiba inseamna "contin macar 50% din termenii dictionarului"? presupun ca jumate din conversatie e jumate din termeni
 */


class MessageManagerMicroservice {
    //private val subscribers: HashMap<Int, Socket>
    private var conversationDict : List<String>? = null
    private var relevantWords : MutableList<String> = mutableListOf()
    private var conversationWords : MutableList<String> = mutableListOf()
    private var conversation : MutableList<String> = mutableListOf()
    private lateinit var messageManagerSocket: Socket
    val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
    private val file = File("savedConversation.txt")

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }
    private fun saveConvo()
    {
        println("${conversationWords.size} - ${relevantWords.size}")
        if(conversationWords.size / 2 <= relevantWords.size) {
            println("Salvat")
            file.appendText(conversation.joinToString("\n") + "\n\n")
        }
    }

    public fun run() {
        conversationDict = File("dict.txt").readLines()[0].split(",")
        //reseteaza fisierul
        file.writeText("")
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        subscribeToMessageManager()

        while (true) {
            val messageManagerBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

            while (true) {
                // se citeste raspunsul de pe socketul TCP
                val receivedMessage = messageManagerBufferReader.readLine()

                println(receivedMessage)
                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    // deci subscriber-ul respectiv a fost deconectat
                    messageManagerBufferReader.close()
                    messageManagerSocket.close()
                    break
                }

                else if (receivedMessage == "startConvo") {
                    if(relevantWords.size > 0)
                    {
                        saveConvo()
                    }
                    relevantWords = mutableListOf()
                    conversation = mutableListOf()
                    conversationWords = mutableListOf()
                }

                else if (receivedMessage == "endConvo")  {
                    saveConvo()
                    relevantWords = mutableListOf()
                    conversation = mutableListOf()
                    conversationWords = mutableListOf()
                }

                else {
                    var mesaj = receivedMessage.split(" ")
                    mesaj = mesaj.drop(2)
                    conversation.add(mesaj.joinToString(" "))
                    mesaj.forEach {
                        println(it)
                        conversationWords.add(it)
                        var word: String = it.replace(",", "")
                        word = word.replace("?", "")
                        word = word.replace(".", "")
                        word = word.replace("!", "")
                        if(conversationDict!!.contains(word.lowercase(Locale.getDefault())))
                            relevantWords.add(word)
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
