package com.sd.laborator.services

import com.sd.laborator.interfaces.FileRecordingInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileRecordingService : FileRecordingInterface {

    override fun recordForecast(data: WeatherForecastData) {
        File("forecasts.txt").printWriter().use { out ->
            out.println("___FORECAST___\n")
            out.println(data.toString())
            out.println("\n\n")
        }
    }

    override fun recordField(data: Any, name: String) {
        File(name).printWriter().use { out ->
            out.println("___"+name+"___\n")
            out.println(data.toString())
            out.println("\n\n")
        }
    }
}