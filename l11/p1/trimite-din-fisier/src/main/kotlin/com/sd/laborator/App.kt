package com.sd.laborator

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun main()
{
    val url = URL("http://localhost:8081/")
    val numbers = File("numbers.txt").readText()
    val postData = "{\"lista\": [$numbers]}"
    println(postData)
    
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Content-Length", postData.length.toString())
    conn.useCaches = false

    DataOutputStream(conn.outputStream).use { it.writeBytes(postData) }
    BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
        var line: String?
        while (br.readLine().also { line = it } != null) {
            println(line)
        }
    }
}