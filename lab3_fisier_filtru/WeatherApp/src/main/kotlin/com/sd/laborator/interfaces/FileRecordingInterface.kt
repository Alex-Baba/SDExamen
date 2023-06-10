package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface FileRecordingInterface {
    fun recordForecast(data: WeatherForecastData)
    fun recordField(data: Any, name: String)
}