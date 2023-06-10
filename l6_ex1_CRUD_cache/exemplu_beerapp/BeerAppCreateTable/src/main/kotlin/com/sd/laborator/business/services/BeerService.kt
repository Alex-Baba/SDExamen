package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerService
import com.sd.laborator.persistence.interfaces.IBeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class BeerService: IBeerService {
    @Autowired
    private lateinit var _beerRepository: IBeerRepository
    private var _pattern: Pattern = Pattern.compile("\\W")

    override fun createBeerTable() {
        _beerRepository.createTable()
    }
}