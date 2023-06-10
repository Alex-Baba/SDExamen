package com.sd.laborator.services

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets


//@Component
class CLIService {

    @Value("\${server.host}")
    private lateinit var host: String

    @Value("\${server.port}")
    private lateinit var port: Integer

    //ne inregistram la service discovery
    private fun registerToDiscovery()
    {
        val url = URL("http://localhost:8000/register")

        val obj = JSONObject()
        obj.put("name","cliservice")
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

    private fun unregister()
    {
        val url = URL("http://localhost:8000/unregister?name=cliservice")
        val con = url.openConnection() as HttpURLConnection
        con.disconnect()
    }

    //luam datele serviciului de API Gateway din registrar
    private fun getGatewayFromRegistrar(): String
    {
        val url = URL("http://localhost:8000/register/gateway")
        return url.readText()
    }

    fun runApp()
    {
        registerToDiscovery()
        val gatewayDomain = getGatewayFromRegistrar()

        println("Introdu orasul pentru care esti interesat sa vezi datele:")
        val oras = readLine()

        while(oras != "stop") {
            val url = URL("http://${gatewayDomain}/getforecast/${oras}")
            val conn = url.openConnection()
            val resp = BufferedReader(InputStreamReader(conn.getInputStream())).read()
            println(resp)
            println("Introdu orasul pentru care esti interesat sa vezi datele:")
            val oras = readLine()
        }
        unregister()
    }
}