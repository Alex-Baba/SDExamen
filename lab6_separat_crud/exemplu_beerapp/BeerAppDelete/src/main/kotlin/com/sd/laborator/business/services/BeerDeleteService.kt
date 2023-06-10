package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerDeleteService
import com.sd.laborator.persistence.interfaces.IBeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class BeerDeleteService : IBeerDeleteService{
    @Autowired
    private lateinit var _beerRepository: IBeerRepository

    private var _pattern: Pattern = Pattern.compile("\\W")

    override fun deleteBeer(name: String) {
        if(_pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return
        }
        _beerRepository.delete(name)
    }
}