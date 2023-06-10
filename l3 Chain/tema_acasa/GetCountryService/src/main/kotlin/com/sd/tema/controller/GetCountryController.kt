package com.sd.tema.controller

import com.sd.tema.service.GetCountryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
class GetCountryController {

    private val nextServiceURLString = "http://localhost:8082/blacklist/"

    @Autowired
    private lateinit var getCountryService: GetCountryService

    @RequestMapping("/getcountry/{ip}/{code}/{city}", method = [RequestMethod.GET])
    @ResponseBody
    fun getCountryCode(@PathVariable("ip") ip: String, @PathVariable("code") code : String, @PathVariable("city") city : String): String {
        val countryForIP = getCountryService.getCountryCode(ip)
        val nextServiceURL = URL("$nextServiceURLString$code/$countryForIP/$city")
        return nextServiceURL.readText()
    }
}