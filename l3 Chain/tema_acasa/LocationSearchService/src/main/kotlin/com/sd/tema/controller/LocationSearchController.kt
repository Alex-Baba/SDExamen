package com.sd.tema.controller;

import com.sd.tema.service.LocationSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
class LocationSearchController {

    @Autowired
    private lateinit var locationSearchService: LocationSearchService

    private val map = hashMapOf<String,String>("Romania" to "RO", "Japan" to "JP", "Germany" to "DE", "Russia" to "RU", "Korea" to "KR", "Poland" to "PL", "United States" to "US")

    @RequestMapping("/getLocationCode/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getLocationCode(@PathVariable location: String): String {
        val nextServiceURLString = "http://localhost:8081/getcountry/"
        val ip = "81.180.210.90"

        val woid = locationSearchService.getLocationId(location)
        val code = map[locationSearchService.getLocationCode(woid)] ?: "UKNOWN"
        val nextServiceURL = URL("$nextServiceURLString$ip/$code/$location")
        return nextServiceURL.readText()
    }

    @RequestMapping("/getLocationId/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getLocationId(@PathVariable location: String): String {
        val nextServiceURLString = "http://localhost:8085/getforecast/"
        val locationID = locationSearchService.getLocationId(location)
        val nextServiceURL = URL(nextServiceURLString + locationID)
        return nextServiceURL.readText()
    }
}