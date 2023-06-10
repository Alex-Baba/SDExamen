package com.sd.tema.controller

import com.sd.tema.service.BlacklistService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
class BlacklistController {

    private val nextServiceURLString = "http://localhost:8083/getLocationId/"

    @Autowired
    private lateinit var blacklistService: BlacklistService

    @RequestMapping("/blacklist/{locationAccessed}/{locationFrom}/{city}", method = [RequestMethod.GET])
    @ResponseBody
    fun isBlacklisted(@PathVariable("locationAccessed") locationAccessed: String, @PathVariable("locationFrom") locationFrom: String, @PathVariable("city") city: String): String {
        val isBlacklisted = blacklistService.isBlacklisted(locationFrom, locationAccessed)
        if(isBlacklisted == "Blocked") {
            return "Not allowed to see this city from your country!"
        }

        val nextServiceURL = URL("$nextServiceURLString$city")
        return nextServiceURL.readText();
    }
}