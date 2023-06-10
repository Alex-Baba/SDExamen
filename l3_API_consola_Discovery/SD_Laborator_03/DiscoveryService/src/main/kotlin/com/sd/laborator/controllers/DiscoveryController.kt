package com.sd.laborator.controllers

import com.sd.laborator.interfaces.IDiscoveryService
import com.sd.laborator.pojo.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

//Discovery-ul asta e pe REST....la altele se poate face prin cozi gen kafka, rabbitmq sau direct tcp

@Controller
class DiscoveryController {
    @Autowired
    private lateinit var discoveryService: IDiscoveryService

    @PostMapping("/register")
    @ResponseBody
    fun registerNewService(@RequestBody body: Service) {
        println(body)
        discoveryService.registerService(body.name, "${body.host}:${body.port}")
    }

    @GetMapping("/register/{name}")
    @ResponseBody
    fun getService(@PathVariable name: String): String? {
        println(name)
        return discoveryService.getFromRegistrar(name)
    }

    @GetMapping("/unregister")
    fun unregister(@RequestParam("name") name: String)
    {
        discoveryService.unregister(name)
    }
}