package com.sd.tema.controller

import com.sd.tema.service.BlacklistService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class BlacklistController {

    @Autowired
    private lateinit var blacklistService: BlacklistService

    @RequestMapping("/blacklist/{locationAccessed}/{locationFrom}", method = [RequestMethod.GET])
    @ResponseBody
    fun isBlacklisted(@PathVariable("locationAccessed") locationAccessed: String, @PathVariable("locationFrom") locationFrom: String): String {
        return blacklistService.isBlacklisted(locationFrom, locationAccessed)
    }
}