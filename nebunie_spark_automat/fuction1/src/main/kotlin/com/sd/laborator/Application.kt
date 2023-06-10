package com.sd.laborator

import com.google.gson.Gson
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.runtime.Micronaut
import java.net.HttpURLConnection
import java.net.URL


object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @KafkaListener(offsetReset = OffsetReset.EARLIEST , groupId = "group1")
    public class ProductListener {

        @Topic("topic_exemplu_kotlin")
        fun receive(name: String) {

            var request: AutomateInput = AutomateInput()

            request.setInput(name.toBooleanStrict())
            request.setCurrentState(getCurrentState())

            println( "We got ${request.getInput()} : ${request.getCurrentState()} ")

            var output = handler.apply(request)

            println("We got ${output.getCurrentState()}")

            if(output.getCurrentState() != -1) {
                setCurrentState(output.getCurrentState())
            }
        }

        private fun setCurrentState(state: Int) {
            val url = URL("http://localhost:8090/set_state?value=${state}")

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"  // optional default is GET

                println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            }

        }

        private fun getCurrentState(): Int {
            val url = URL("http://localhost:8090/get_state")

            var state:Int = -1
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"  // optional default is GET

                println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

                inputStream.bufferedReader().use {

                    var json:String = ""
                    it.lines().forEach { line ->
                        println(line)
                        json += line
                    }

                    json = json.replace("{", "");
                    json = json.replace("}", "");
                    val keyValue = json.split(":")
                    state = keyValue[1].toInt()
                }
            }
            return state
        }

        companion object {
            private val handler = State1Fuction()
        }
    }
}