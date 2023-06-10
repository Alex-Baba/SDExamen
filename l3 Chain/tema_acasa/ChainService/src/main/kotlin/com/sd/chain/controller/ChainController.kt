package com.sd.chain.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL
import javax.servlet.http.HttpServletRequest

@Controller
class ChainController {

    val nextServiceURLString = "http://localhost:8083/getLocationCode/"

    @RequestMapping("/getfullforecast/{city}", method = [RequestMethod.GET])
    @ResponseBody
    fun getFullForecast(@PathVariable("city") city: String, request: HttpServletRequest): String {
        val nextServiceURL = URL(nextServiceURLString + city)
        return nextServiceURL.readText()
    }
}