package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IBeerReadService
import com.sd.laborator.models.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BeerDAOController {

    @Autowired
    private lateinit var _beerReadService: IBeerReadService

    @RequestMapping(value = ["/get_all_beers"], method = [RequestMethod.GET])
    fun getAllBears(): ResponseEntity< MutableList<Beer?> > {

        println("Return all beers in the database.")

        val beers:  MutableList<Beer?> = _beerReadService.getBeers()
        val status = if (beers.isEmpty()) {
            HttpStatus.NOT_FOUND
        } else {
            HttpStatus.OK
        }
        return ResponseEntity(beers, status)
    }

    @RequestMapping(value = ["/get_bear_by_name"], method = [RequestMethod.GET])
    fun getBeerByName(@RequestParam(required = true, name = "beer_name", defaultValue = "") beerName: String): ResponseEntity<Beer?> {

        println("Recover beer with name " + beerName);

        var status = HttpStatus.OK

       var beer: Beer? =  _beerReadService.getBeerByName(beerName)

        if( beer == null){
            status = HttpStatus.NOT_FOUND
            return ResponseEntity( status)
        }

        return ResponseEntity(beer, status)
    }

    @RequestMapping(value = ["/get_bear_by_price"], method = [RequestMethod.GET])
    fun getBeerByPrice(@RequestParam(required = true, name = "beer_price", defaultValue = "") beerPrice: Float ): ResponseEntity<MutableList<Beer?>> {

        println("Recover beer with price " + beerPrice);

        var status = HttpStatus.OK

        var beer: MutableList<Beer?> =  _beerReadService.getBeerByPrice(beerPrice)

        if( beer.isEmpty()){
            status = HttpStatus.NOT_FOUND
            return ResponseEntity( status)
        }

        return ResponseEntity(beer, status)
    }


}