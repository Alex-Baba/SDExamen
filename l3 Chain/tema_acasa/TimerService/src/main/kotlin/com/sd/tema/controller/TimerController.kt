package com.sd.tema.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sd.orchestration.pojo.WeatherForecastData
import com.sd.tema.service.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class TimerController {

    @Autowired
    private lateinit var timerService: TimeService

    @RequestMapping("/gettime", method = [RequestMethod.POST])
    @ResponseBody
    fun getTime(@RequestBody forecastData : String): String{
        val forecastPOJO : WeatherForecastData = jacksonObjectMapper().readValue(forecastData)
        forecastPOJO.date = timerService.getCurrentTime()
        return jacksonObjectMapper().writeValueAsString(forecastPOJO)
    }
}