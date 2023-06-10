package com.sd.laborator.services

import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import kotlin.math.roundToInt

@Service
class WeatherForecastService (private val timeService: TimeService) : WeatherForecastInterface {
    override fun getForecastData(locationId: Int): WeatherForecastData {
        // ID-ul locaţiei nu trebuie codificat, deoarece este numeric

        try {
            val forecastDataURL = URL("https://www.metaweather.com/api/location/$locationId/")

            // preluare conţinut răspuns HTTP la o cerere GET către URL-ul de mai sus
            val rawResponse: String = forecastDataURL.readText()

            // parsare obiect JSON primit
            val responseRootObject = JSONObject(rawResponse)
            val weatherDataObject = responseRootObject.getJSONArray("consolidated_weather").getJSONObject(0)
        }
        catch (e: Exception){
          // println( e.toString())
        }
        // construire şi returnare obiect POJO care încapsulează datele meteo
/*        return WeatherForecastData(
            location = responseRootObject.getString("title"),
            date = timeService.getCurrentTime(),
            weatherState = weatherDataObject.getString("weather_state_name"),
            weatherStateIconURL =
                "https://www.metaweather.com/static/img/weather/png/${weatherDataObject.getString("weather_state_abbr")}.png",
            windDirection = weatherDataObject.getString("wind_direction_compass"),
            windSpeed = weatherDataObject.getFloat("wind_speed").roundToInt(),
            minTemp = weatherDataObject.getFloat("min_temp").roundToInt(),
            maxTemp = weatherDataObject.getFloat("max_temp").roundToInt(),
            currentTemp = weatherDataObject.getFloat("the_temp").roundToInt(),
            humidity = weatherDataObject.getFloat("humidity").roundToInt()



        )*/

        return WeatherForecastData(
            location = "Viena",
            date = timeService.getCurrentTime(),
            weatherState = "Raining",
            weatherStateIconURL =
            "https://www.metaweather.com/static/img/weather/png/Raining.png",
            windDirection = "North",
            windSpeed = 43,
            minTemp = 5,
            maxTemp = 41,
            currentTemp = 41,
            humidity = 59
        )
    }
}