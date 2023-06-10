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

    @GetMapping("/beer/get/{price}")
    fun getByPrice(@PathVariable price: Float) : ResponseEntity<String>
    {
        return if(_beerService.getBeerByPrice(price) != null){
            ResponseEntity(_beerService.getBeerByPrice(price),HttpStatus.OK)
        } else {
            ResponseEntity("not found",HttpStatus.NOT_FOUND)
        }

    }

}