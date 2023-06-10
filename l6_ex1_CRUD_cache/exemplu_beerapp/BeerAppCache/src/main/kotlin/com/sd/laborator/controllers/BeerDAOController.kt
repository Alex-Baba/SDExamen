package com.sd.laborator.controllers

import com.sd.laborator.business.interfaces.IBeerCacheService
import com.sd.laborator.models.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class BeerDAOController {
    @Autowired
    private lateinit var _beerCache: IBeerCacheService

    @PostMapping("/cache/add")
    fun addToCache(@RequestBody query: Query) : ResponseEntity<String>
    {
        println("add")
        _beerCache.addToCache(query)
        return ResponseEntity("added", HttpStatus.OK)
    }

    @PostMapping("/cache/get")
    fun getFromCache(@RequestBody query: String): ResponseEntity<String> {
        println(query)
        return if(_beerCache.getFromCache(query) != null) {
            ResponseEntity(_beerCache.getFromCache(query).toString(), HttpStatus.OK)
        } else {
            ResponseEntity("", HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/cache/delete")
    fun deleteFromCache(): ResponseEntity<String> {
        println("delete")
        _beerCache.removeFromCache()
        return ResponseEntity("deleted", HttpStatus.OK)
    }

}