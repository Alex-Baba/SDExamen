package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerUpdateService
import com.sd.laborator.models.Beer
import com.sd.laborator.persistence.interfaces.IBeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class BeerUpdateService: IBeerUpdateService {
    @Autowired
    private lateinit var _beerRepository: IBeerRepository

    private var _pattern: Pattern = Pattern.compile("\\W")

    override fun updateBeer(beer: Beer) {
        if(_pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        _beerRepository.update(beer)
    }
}