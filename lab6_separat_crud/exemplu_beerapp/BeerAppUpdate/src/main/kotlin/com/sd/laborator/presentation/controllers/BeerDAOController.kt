package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IBeerUpdateService
import com.sd.laborator.models.Beer
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BeerDAOController {

    @Autowired
    private lateinit var _beerUpdateService: IBeerUpdateService

    @RequestMapping(value = ["/edit_beer"], method = [RequestMethod.POST])
    fun editBeer(
        @RequestParam(required = true, name = "beer_id", defaultValue = "") beerID: Int,
        @RequestParam(required = true, name = "beer_name", defaultValue = "") beerName: String,
        @RequestParam(required = true, name = "beer_price", defaultValue = "") beerPrice: Float
    ): ResponseEntity<Beer?> {

        println("Editing bear with ID " + beerID + " with values " + beerName + " " + beerPrice)

        var beer = Beer(beerID,beerName, beerPrice)
        _beerUpdateService.updateBeer(beer)

        return ResponseEntity(HttpStatus.OK)
    }

}