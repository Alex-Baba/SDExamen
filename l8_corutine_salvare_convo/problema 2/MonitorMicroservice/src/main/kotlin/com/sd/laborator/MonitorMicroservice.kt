package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.system.exitProcess

class MonitorMicroservice {
    private lateinit var messageManagerSocket: Socket

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
    }

    private fun subscribeToMonitor() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)

            var responseToQuestion = "command monitor nope"
            messageManagerSocket.getOutputStream().write((responseToQuestion + "\n").toByteArray())

            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }


    public fun run() {

        subscribeToMonitor()
        val monitorBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

        while(true){
            val receivedMessage = monitorBufferReader.readLine()

            println(receivedMessage)

            if (receivedMessage == null) {
                // deci subscriber-ul respectiv a fost deconectat
                monitorBufferReader.close()
                messageManagerSocket.close()
                break
            }

            //No avut chef de ultima parte

        }
    }

}

fun main() {
    val messageManagerMicroservice = MonitorMicroservice()
    messageManagerMicroservice.run()
}
