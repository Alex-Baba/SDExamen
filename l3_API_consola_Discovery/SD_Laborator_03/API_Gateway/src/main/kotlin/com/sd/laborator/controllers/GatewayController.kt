package com.sd.laborator.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
class GatewayController {

    //luam datele serviciului de Forecast din registrar
    private fun getWeatherForecastServiceFromRegistrar(): String
    {
        val url = URL("http://localhost:8000/register/forecast")
        return url.readText()
    }

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        val domain = getWeatherForecastServiceFromRegistrar()
        return URL("http://$domain/getforecast/${location.toLowerCase()}").readText()
    }

}