package com.sd.laborator.controllers

import com.sd.laborator.claseNormale.MaxTemperatureCriteria
import com.sd.laborator.interfaces.FileRecordingInterface
import com.sd.laborator.interfaces.FilterInformationInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAppController {
    @Autowired
    private lateinit var locationSearchService: LocationSearchInterface

    @Autowired
    private lateinit var weatherForecastService: WeatherForecastInterface

    @Autowired
    private lateinit var fileRecordingService: FileRecordingInterface

    @Autowired
    private lateinit var filterInformationService: FilterInformationInterface

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        // se incearca preluarea WOEID-ului locaţiei primite in URL
        val locationId = locationSearchService.getLocationId(location)

        // dacă locaţia nu a fost găsită, răspunsul va fi corespunzător
        if (locationId == -1) {
            return "Nu s-au putut gasi date meteo pentru cuvintele cheie \"$location\"!"
        }

        // pe baza ID-ului de locaţie, se interoghează al doilea serviciu care returnează datele meteo
        // încapsulate într-un obiect POJO
        val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationId)

        fileRecordingService.recordForecast(rawForecastData)

        filterInformationService.setCriteria( MaxTemperatureCriteria() )
        var filteredForecasts = filterInformationService.filter(mutableListOf(rawForecastData))

        for (forecast in filteredForecasts) {
            fileRecordingService.recordField(forecast.currentTemp, "curentTemp.txt")
            fileRecordingService.recordField(forecast.humidity, "humitidy.txt")
        }

            // fiind obiect POJO, funcţia toString() este suprascrisă pentru o afişare mai prietenoasă
        return rawForecastData.toString()
    }
}