package com.sd.tema.`interface`

import com.sd.orchestration.pojo.WeatherForecastData

interface WeatherForecastInterface {
    fun getForecastData(locationId: Int): WeatherForecastData
}