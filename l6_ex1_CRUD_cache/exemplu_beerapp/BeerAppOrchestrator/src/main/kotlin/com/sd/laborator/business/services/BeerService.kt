package com.sd.laborator.business.services

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sd.laborator.business.interfaces.IBeerService
import com.sd.laborator.models.Beer
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

@Service
class BeerService: IBeerService {
    private var _pattern: Pattern = Pattern.compile("\\W")
    private var newId = 0

    private fun getFromCache(queryString: String) : String
    {
        val json = queryString
        println(json)

        val url = URL("http://localhost:8002/cache/get")
        val conn = url.openConnection() as HttpURLConnection
        conn.doOutput = true
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type","application/json")
        val bytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        conn.outputStream.write(bytes)
        conn.outputStream.flush()

        val responseCode: Int = conn.responseCode
        var read = ""
        if(responseCode == 200) {
            read = BufferedReader(InputStreamReader(conn.inputStream)).readText()
        }

        conn.disconnect()
        return read
    }

    private fun addToCache(queryString: String, result: String)
    {
        val json = JsonObject()
        json.addProperty("query",queryString)
        json.addProperty("result",result)
        println(json)

        val url = URL("http://localhost:8002/cache/add")
        val conn = url.openConnection() as HttpURLConnection
        conn.doOutput = true
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type","application/json")
        val bytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        conn.outputStream.write(bytes)

        val read = BufferedReader(InputStreamReader(conn.inputStream)).readText()

        conn.disconnect()
    }

    private fun removeFromCache()
    {
        //val json = queryString

        val url = URL("http://localhost:8002/cache/delete")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val responseCode: Int = conn.responseCode
        println("GET Response Code :: $responseCode")

        conn.disconnect()
    }

    override fun createBeerTable() {
        val url = URL("http://localhost:8003/beer/table")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val responseCode: Int = conn.responseCode
        println("GET Response Code :: $responseCode")
        conn.disconnect()
    }

    override fun addBeer(beer: Beer) {
        if(_pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        removeFromCache()
        beer.beerID = newId++

        val gson = Gson()
        val json = gson.toJson(beer)
        println(json)

        val url = URL("http://localhost:8001/beer/add")
        val conn = url.openConnection() as HttpURLConnection
        conn.doOutput = true
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type","application/json")
        val bytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        conn.outputStream.write(bytes)

        val read = BufferedReader(InputStreamReader(conn.inputStream)).readText()

        conn.disconnect()
    }

    override fun getBeers(): String {
        val cache = getFromCache("/beer/get-all")
        if(cache != "") {
            return cache.split("result=")[1].dropLast(1)
        }
        else {
            val url = URL("http://localhost:8005/beer/get-all")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val responseCode: Int = conn.responseCode
            println("GET Response Code :: $responseCode")

            val stringResult = BufferedReader(InputStreamReader(conn.inputStream)).readText()
            println(stringResult)

            conn.disconnect()
            addToCache("/beer/get-all", stringResult)
            return stringResult
        }
    }

    override fun getBeerByName(name: String): String? {
        if(_pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return null
        }

        val cache = getFromCache("/beer/get/$name")
        if(cache != "") {
            return cache.split("result=")[1].dropLast(1)
        }
        else {
            val url = URL("http://localhost:8006/beer/get/$name")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val responseCode: Int = conn.responseCode
            println("GET Response Code :: $responseCode")

            val stringResult = BufferedReader(InputStreamReader(conn.inputStream)).readText()
            println(stringResult)

            conn.disconnect()
            addToCache("/beer/get/$name",stringResult)
            return stringResult
        }
    }

    override fun getBeerByPrice(price: Float): String {
        val cache = getFromCache("/beer/get/$price")
        if(cache != "") {
            return cache.split("result=")[1].dropLast(1)
        }
        else {
            val url = URL("http://localhost:8007/beer/get/$price")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val responseCode: Int = conn.responseCode
            println("GET Response Code :: $responseCode")

            val stringResult = BufferedReader(InputStreamReader(conn.inputStream)).readText()
            println(stringResult)

            conn.disconnect()
            addToCache("/beer/get/$price",stringResult)
            return stringResult
        }
    }

    override fun updateBeer(beer: Beer) {
        if(_pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        removeFromCache()
        val gson = Gson()
        val json = gson.toJson(beer)
        println(json)

        val url = URL("http://localhost:8008/beer/update")
        val conn = url.openConnection() as HttpURLConnection
        conn.doOutput = true
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type","application/json")
        val bytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        conn.outputStream.write(bytes)

        val read = BufferedReader(InputStreamReader(conn.inputStream)).readText()

        conn.disconnect()
    }

    override fun deleteBeer(name: String) {
        if(_pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return
        }
        removeFromCache()
        val url = URL("http://localhost:8004/beer/delete/$name")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val responseCode: Int = conn.responseCode
        println("GET Response Code :: $responseCode")
        val stringResult = BufferedReader(InputStreamReader(conn.inputStream)).readText()
        println(stringResult)
        conn.disconnect()
    }
}