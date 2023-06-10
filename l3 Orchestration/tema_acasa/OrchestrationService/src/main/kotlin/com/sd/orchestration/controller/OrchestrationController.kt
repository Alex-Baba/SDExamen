package com.sd.orchestration.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sd.orchestration.pojo.WeatherForecastData
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL
import javax.servlet.http.HttpServletRequest

@Controller
class OrchestrationController {

    @RequestMapping("/getfullforecast/{city}", method = [RequestMethod.GET])
    @ResponseBody
    fun getFullForecast(@PathVariable("city") city: String, request: HttpServletRequest): String {
        val ip = "81.180.210.90"

        var url = URL("http://localhost:8083/getLocationCode/$city")
        val cityLocation = url.readText()
        println(cityLocation)

        url = URL("http://localhost:8081/getcountry/$ip")
        //var responseRootObject =
        val ipLocation = url.readText()
        println(ip)
        println(ipLocation)

        url = URL("http://localhost:8082/blacklist/$cityLocation/$ipLocation")
        val isBlacklisted = url.readText()
        println(isBlacklisted)

        if(isBlacklisted == "Blocked") {
            return "Not allowed to see this city from your country!"
        }

        url = URL("http://localhost:8083/getLocationId/$city")
        val locationId = url.readText().toInt()
        println(locationId)

        url = URL("http://localhost:8085/getforecast/$locationId")
        val responseObject = url.readText()
        val forecast: WeatherForecastData = jacksonObjectMapper().readValue(responseObject)
        println(forecast)

        url = URL("http://localhost:8084/gettime")
        forecast.date = url.readText()

        return jacksonObjectMapper().writeValueAsString(forecast)
    }
}