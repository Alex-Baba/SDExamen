package com.sd.tema.controller;

import com.sd.tema.service.LocationSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LocationSearchController {

    @Autowired
    private lateinit var locationSearchService: LocationSearchService

    private val map = hashMapOf<String,String>("Romania" to "RO", "Japan" to "JP", "Germany" to "DE", "Russia" to "RU", "Korea" to "KR", "Poland" to "PL", "United States" to "US")

    @RequestMapping("/getLocationCode/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getLocationCode(@PathVariable location: String): String {
        val woid = locationSearchService.getLocationId(location)
        return map[locationSearchService.getLocationCode(woid)] ?: "UKNOWN"
    }

    @RequestMapping("/getLocationId/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getLocationId(@PathVariable location: String): Int {
        return locationSearchService.getLocationId(location)
    }
}