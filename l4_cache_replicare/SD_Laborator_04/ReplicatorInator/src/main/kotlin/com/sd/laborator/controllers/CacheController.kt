package com.sd.laborator.controllers

import com.google.gson.Gson
import com.sd.laborator.pojo.Query
import org.json.JSONObject
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@RestController
class CacheController {

    private val cacheList : MutableList<String> = mutableListOf()

    init {
        //replicare
        println("init")
        (1..4).forEach {
            var port = 8010 + it
            println(port)
            //asta presupune sa dai build la dockerul din cacheservice cu tagul img_cache_repl:v1
            Runtime.getRuntime().exec("docker run -d -p ${port}:8003 img_cache_repl:v1")
            cacheList.add("http://localhost:${port}")
        }
    }

    @PostMapping("/cache/post")
    fun addToCache(@RequestBody query: Query){
        val url = URL("${cacheList.get(Random.nextInt(0, cacheList.size) % cacheList.size)}/cache/post")

        val jsonObj = JSONObject()
        jsonObj.put("query",query.query)
        jsonObj.put("result",query.result)
        jsonObj.put("timestamp",query.timestamp.toString())

        val bytes = jsonObj.toString().toByteArray(StandardCharsets.UTF_8)

        val http = url.openConnection() as HttpURLConnection
        http.doOutput = true
        http.setRequestProperty("Content-Type","application/json")
        //http.setRequestProperty("Content-Length",bytes.size.toString())
        http.outputStream.write(bytes)

        val resp = BufferedReader(InputStreamReader(http.inputStream)).readText()

        http.disconnect()
    }

    @PostMapping("/cache/get")
    fun getFromCache(@RequestBody query: String): Query? {
        val url = URL("${cacheList.get(Random.nextInt(0, cacheList.size) % cacheList.size)}/cache/get")

        val jsonObj = JSONObject()
        jsonObj.put("query",query)

        val bytes = jsonObj.toString().toByteArray(StandardCharsets.UTF_8)

        val http = url.openConnection() as HttpURLConnection
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")
        //http.setRequestProperty("Content-Length",bytes.size.toString())
        http.outputStream.write(bytes)

        val resp = BufferedReader(InputStreamReader(http.inputStream)).readText()

        http.disconnect()
        return Gson().fromJson(resp, Query::class.java)
    }

}