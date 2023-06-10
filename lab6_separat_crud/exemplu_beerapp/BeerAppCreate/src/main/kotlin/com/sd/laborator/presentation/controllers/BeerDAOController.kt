package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.IBeerCreateService
import com.sd.laborator.models.Beer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BeerDAOController {
    @Autowired
    private lateinit var _beerCreateService: IBeerCreateService

    @RequestMapping(value = ["/create_beer_table"], method = [RequestMethod.GET])
    fun createBeerTable(): ResponseEntity<Beer?> {

        var status =   HttpStatus.OK

        try{
            _beerCreateService.createBeerTable()
        }catch (e:Exception){
            println(e.message)
            status = HttpStatus.INTERNAL_SERVER_ERROR
        }

        //Only return a status
        return ResponseEntity(status)
    }

    @RequestMapping(value = ["/add_beer"], method = [RequestMethod.POST])
    fun createBeer(
        @RequestParam(required = true, name = "beer_name", defaultValue = "") beerName: String,
        @RequestParam(required = true, name = "beer_price", defaultValue = "") beerPrice: Float
    ): ResponseEntity<Beer?> {

        println("Created beer with name " + beerName + " and price " + beerPrice );


        var status =   HttpStatus.OK

        try{
            val beer: Beer = Beer(0,beerName ,beerPrice)
            _beerCreateService.addBeer(beer)
        }catch (e:Exception){
            println(e.message)
            status = HttpStatus.INTERNAL_SERVER_ERROR
        }

        //Only return a status
        return ResponseEntity( status)
    }

}