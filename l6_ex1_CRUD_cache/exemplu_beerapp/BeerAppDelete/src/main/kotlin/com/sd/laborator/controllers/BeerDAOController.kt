package com.sd.laborator.controllers

import com.sd.laborator.business.interfaces.IBeerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Controller
class BeerDAOController {
    @Autowired
    private lateinit var _beerService: IBeerService

    @GetMapping("/beer/delete/{name}")
    fun deleteBeer(@PathVariable name: String) : ResponseEntity<String>
    {
        _beerService.deleteBeer(name)
        println("deleted")
        return ResponseEntity("deleted", HttpStatus.OK)
    }

}