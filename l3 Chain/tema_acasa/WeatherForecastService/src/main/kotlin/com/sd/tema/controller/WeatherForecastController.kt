package com.sd.tema.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sd.orchestration.pojo.WeatherForecastData
import com.sd.tema.*
import com.sd.tema.`interface`.WeatherForecastInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors


@Controller
class WeatherForecastController {
    @Autowired
    private lateinit var weatherForecastService: WeatherForecastInterface

    private val nextServiceURLString = "http://localhost:8084/gettime"

    @RequestMapping("/getforecast/{locationId}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable locationId: Int): String {
        if (locationId == -1) {
            throw Exception("Nu s-au putut gasi date meteo pentru id-ul \"$locationId\"!")
        }
        val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationId)
        val rawForecastDataString = jacksonObjectMapper().writeValueAsString(rawForecastData)
        val nextServiceURL = URL(nextServiceURLString)

        val http: HttpURLConnection = nextServiceURL.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.setRequestProperty("accept", "application/json");
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")

        val out: ByteArray = rawForecastDataString.toByteArray(StandardCharsets.UTF_8)

        val stream = http.outputStream
        stream.write(out)

        val bufferedReader =  BufferedReader(InputStreamReader(http.inputStream))
        val rawResponse =  bufferedReader.lines().collect(Collectors.joining())
        http.disconnect()

        return rawResponse
    }
}
