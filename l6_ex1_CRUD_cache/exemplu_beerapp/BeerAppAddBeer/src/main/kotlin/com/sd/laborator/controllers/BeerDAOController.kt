package com.sd.laborator.controllers

import com.sd.laborator.business.interfaces.IBeerService
import com.sd.laborator.models.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class BeerDAOController {
    @Autowired
    private lateinit var _beerService: IBeerService

    @PostMapping("/beer/add")
    fun addBeer(@RequestBody beer: Beer): ResponseEntity<String>
    {
        println("added")
        _beerService.addBeer(beer)
        return ResponseEntity("added", HttpStatus.OK)
    }

}