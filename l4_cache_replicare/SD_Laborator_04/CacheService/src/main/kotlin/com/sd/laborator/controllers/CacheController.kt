package com.sd.laborator.controllers

import com.sd.laborator.interfaces.ICacheService
import com.sd.laborator.pojo.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class CacheController {
    @Autowired
    private lateinit var cacheService: ICacheService

    @PostMapping("/cache/post")
    fun addToCache(@RequestBody query: Query){
        print(query.toString())
        cacheService.addToCache(query)
    }

    @PostMapping("/cache/get")
    fun getFromCache(@RequestBody query: String) : Query?
    {
        val queryExtracted = query.split(":")[1].drop(1).dropLast(2)
        println(queryExtracted)
        return cacheService.getFromCache(queryExtracted)
    }

}