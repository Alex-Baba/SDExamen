package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface CriteriaInterface {
    fun meetCriteria(data: List<WeatherForecastData>): List<WeatherForecastData>
}