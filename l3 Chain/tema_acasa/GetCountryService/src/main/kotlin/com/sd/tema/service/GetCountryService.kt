package com.sd.tema.service

import com.sd.tema.`interface`.GetCountryInterface
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL

@Service
class GetCountryService : GetCountryInterface{

    override fun getCountryCode(ip: String): String {
        val forecastDataURL = URL("https://api.ip2country.info/ip?$ip")

        // preluare conţinut răspuns HTTP la o cerere GET către URL-ul de mai sus
        val rawResponse: String = forecastDataURL.readText()

        val responseRootObject = JSONObject(rawResponse)
        val countryCode = responseRootObject["countryCode"]
        //println(countryCode.toString())
        return countryCode.toString()
    }


}