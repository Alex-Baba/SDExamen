package com.sd.laborator.controllers

import com.sd.laborator.business.interfaces.IBeerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BeerDAOController {
    @Autowired
    private lateinit var _beerService: IBeerService

    @GetMapping("/beer/get-all")
    fun getBeers(): ResponseEntity<String>
    {
        println("all")
        return ResponseEntity(_beerService.getBeers(), HttpStatus.OK)
    }

}