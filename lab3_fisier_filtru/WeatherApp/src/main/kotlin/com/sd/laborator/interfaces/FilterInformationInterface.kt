package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface FilterInformationInterface {
    fun filter(data: List<WeatherForecastData>):List<WeatherForecastData>
    fun setCriteria(criteria: CriteriaInterface)
}