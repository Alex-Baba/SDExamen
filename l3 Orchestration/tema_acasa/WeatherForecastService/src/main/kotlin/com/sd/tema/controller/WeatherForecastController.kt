package com.sd.tema.controller

import com.sd.orchestration.pojo.WeatherForecastData
import com.sd.tema.`interface`.WeatherForecastInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherForecastController {
    @Autowired
    private lateinit var weatherForecastService: WeatherForecastInterface

    @RequestMapping("/getforecast/{locationId}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable locationId: Int): WeatherForecastData {
        // se incearca preluarea WOEID-ului locaţiei primite inURL
        if (locationId == -1) {
            throw Exception("Nu s-au putut gasi date meteo pentru id-ul \"$locationId\"!")
        }
        // pe baza ID-ului de locaţie, se interoghează al doileaserviciucare returnează datele meteo
        // încapsulate într-un obiect POJO
        val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationId)
        // fiind obiect POJO, funcţia toString() este suprascrisăpentruo afişare mai prietenoasă
        return rawForecastData;
    }
}
