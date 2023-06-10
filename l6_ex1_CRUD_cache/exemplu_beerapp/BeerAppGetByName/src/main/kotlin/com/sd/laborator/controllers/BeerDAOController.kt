package com.sd.laborator.controllers

import com.sd.laborator.business.interfaces.IBeerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BeerDAOController {
    @Autowired
    private lateinit var _beerService: IBeerService

    @GetMapping("/beer/get/{name}")
    fun addBeer(@PathVariable name: String) : ResponseEntity<String>
    {
        return if(_beerService.getBeerByName(name) != null) {
            ResponseEntity(_beerService.getBeerByName(name), HttpStatus.OK)
        } else {
            ResponseEntity("not found", HttpStatus.NOT_FOUND)
        }
    }

}