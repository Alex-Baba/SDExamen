package com.sd.laborator.presentation.controllers

import com.google.gson.Gson
import com.sd.laborator.models.Beer
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets


@Component
class BeerDAOController {

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    // citesc din queue1
    // scriu in queue
    @RabbitListener(queues = ["\${beerapp.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        val (operation, parameters) = msg.split('~')
        var beer: Beer? = null
        var price: Float? = null
        var name: String? = null

        // id=1;name=Corona;price=3.6
        if("id=" in parameters) {
            println(parameters)
            val params: List<String> = parameters.split(';')
            try {
                beer = Beer(
                    params[0].split('=')[1].toInt(),
                    params[1].split('=')[1],
                    params[2].split('=')[1].toFloat()
                )
            } catch (e: Exception) {
                print("Error parsing the parameters: ")
                println(params)
                return
            }
        } else if ("price=" in parameters) {
            price = parameters.split('=')[1].toFloat()
        } else if ("name=" in parameters) {
            name = parameters.split("=")[1]
        }
        println("Parameters: $parameters")
        println("Name: $name")
        println("Price: $price")
        println("Beer: $beer")
        val result: Any? = when(operation) {
          "createBeerTable" -> createBeerTable()
          "addBeer" -> addBeer(beer!!)
          "getBeers" -> getBeers()
          "getBeerByName" -> getBeerByName(name!!)
          "getBeerByPrice" -> getBeerByPrice(price!!)
          "updateBeer" -> updateBeer(beer!!)
          "deleteBeer" -> deleteBeer(name!!)
            else -> null
        }
        println("Result: $result")
        if (result != null) sendMessage(result.toString())
    }

    private fun createBeerTable(): Any? {
        val url = URL("http://localhost:8081/create_beer_table")
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.setRequestMethod("HEAD")

        val responseCode: Int = con.responseCode

        println("GET Response Code For Create Table:: $responseCode")
        con.disconnect()
        return null
    }

    private fun addBeer(beer: Beer): Any? {
        var stringURL = "http://localhost:8081/add_beer?beer_name="+ beer.beerName+  "&beer_price=" + beer.beerPrice

        print(stringURL)
        val url = URL(stringURL)
        val con = url.openConnection() as HttpURLConnection
        con.doOutput = true
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type","application/json")

        BufferedReader(InputStreamReader(con.inputStream)).readText()
        con.disconnect()
        return null
    }

    private fun getBeers(): Any? {
        var stringURL = "http://localhost:8082/get_all_beers"

        print(stringURL)
        val url = URL(stringURL)
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        val responseCode: Int = con.responseCode
        println("GET Response Code :: $responseCode")

        val stringResult = BufferedReader(InputStreamReader(con.inputStream)).readText()
        println(stringResult)
        con.disconnect()
        return stringResult
    }

    private fun getBeerByName(name: String): Any? {
        TODO("Not yet implemented")
    }

    private fun getBeerByPrice(price: Float): Any? {
        TODO("Not yet implemented")
    }

    private fun updateBeer(beer: Beer): Any? {
        TODO("Not yet implemented")
    }

    private fun deleteBeer(name: String): Any? {
        TODO("Not yet implemented")
    }


    private fun sendMessage(msg: String) {
        println("Message to send: $msg")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKey(), msg)
    }
}