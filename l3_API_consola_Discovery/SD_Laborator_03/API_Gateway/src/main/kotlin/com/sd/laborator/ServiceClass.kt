package com.sd.laborator

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.annotation.PreDestroy

class ServiceClass {
    @Value("\${server.host}")
    private lateinit var host: String

    @Value("\${server.port}")
    private lateinit var port: Integer

    //ne inregistram la service discovery
    public fun registerToDiscovery()
    {
        val url = URL("http://localhost:8000/register")

        val obj = JSONObject()
        obj.put("name","gateway")
        obj.put("host",host)
        obj.put("port","$port")

        val http = url.openConnection() as HttpURLConnection
        http.setRequestProperty("accept", "application/json");
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")

        val out: ByteArray = obj.toString().toByteArray(StandardCharsets.UTF_8)

        val stream = http.outputStream
        stream.write(out)

        val bufferedReader =  BufferedReader(InputStreamReader(http.inputStream))
        //val rawResponse =  bufferedReader.lines().collect(Collectors.joining())
        http.disconnect()
    }

    @PreDestroy
    fun unregister()
    {
        println("unregister")
        val url = URL("http://localhost:8000/unregister?name=gateway")
        val con = url.openConnection() as HttpURLConnection
        con.disconnect()
    }
}