package com.sd.tema.controller

import com.sd.tema.service.GetCountryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class GetCountryController {

    @Autowired
    private lateinit var getCountryService: GetCountryService

    @RequestMapping("/getcountry/{ip}", method = [RequestMethod.GET])
    @ResponseBody
    fun getCountryCdoe(@PathVariable("ip") ip: String): String {
        return getCountryService.getCountryCode(ip)
    }
}