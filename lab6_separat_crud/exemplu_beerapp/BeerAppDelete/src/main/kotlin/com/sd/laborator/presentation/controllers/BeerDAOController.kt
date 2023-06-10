package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IBeerDeleteService
import com.sd.laborator.models.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BeerDAOController {

    @Autowired
    private lateinit var _beerDeleteService: IBeerDeleteService

    @RequestMapping(value = ["/delete_beer"], method = [RequestMethod.POST])
    fun editBeer(
        @RequestParam(required = true, name = "beer_name", defaultValue = "") beerName: String
    ): ResponseEntity<Beer?> {

        println("Deletting bear with name " + beerName)
        _beerDeleteService.deleteBeer(beerName)

        return ResponseEntity(HttpStatus.OK)
    }

}