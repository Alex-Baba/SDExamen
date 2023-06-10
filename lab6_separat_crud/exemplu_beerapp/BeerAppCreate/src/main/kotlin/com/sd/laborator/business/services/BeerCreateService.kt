package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerCreateService
import com.sd.laborator.models.Beer
import com.sd.laborator.persistence.interfaces.IBeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class BeerCreateService:IBeerCreateService {
    @Autowired
    private lateinit var _beerRepository: IBeerRepository

    private var _pattern: Pattern = Pattern.compile("\\W")

    override fun createBeerTable() {
        println("We created the table.")
        _beerRepository.createTable()
    }

    override fun addBeer(beer: Beer) {
        println("We created the bear." + beer.toString())
        if(_pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        _beerRepository.add(beer)
    }

}