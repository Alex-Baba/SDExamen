package com.sd.tema.controller

import com.sd.tema.service.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class TimerController {

    @Autowired
    private lateinit var timerService: TimeService

    @RequestMapping("/gettime", method = [RequestMethod.GET])
    @ResponseBody
    fun getTime(): String{
        return timerService.getCurrentTime()
    }
}