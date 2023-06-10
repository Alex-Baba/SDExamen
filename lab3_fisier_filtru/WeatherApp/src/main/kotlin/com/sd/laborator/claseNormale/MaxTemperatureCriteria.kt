package com.sd.laborator.claseNormale

import com.sd.laborator.interfaces.CriteriaInterface
import com.sd.laborator.pojo.WeatherForecastData

class MaxTemperatureCriteria:CriteriaInterface {
    override fun meetCriteria(data: List<WeatherForecastData>): List<WeatherForecastData> {
        var list: MutableList<WeatherForecastData> = mutableListOf()

        for (forecast in data) {
            if(forecast.currentTemp < 40){
                list.add(forecast)
            }
        }
        return list
    }
}
