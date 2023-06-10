package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerReadService
import com.sd.laborator.models.Beer
import com.sd.laborator.persistence.interfaces.IBeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class BeerReadService: IBeerReadService {
    @Autowired
    private lateinit var _beerRepository: IBeerRepository

    private var _pattern: Pattern = Pattern.compile("\\W")

    override fun getBeers(): MutableList<Beer?> {
        return _beerRepository.getAll()
    }

    override fun getBeerByName(name: String): Beer? {
        if (_pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return null
        }
        return _beerRepository.getByName(name)
    }

    override fun getBeerByPrice(price: Float):  MutableList<Beer?>  {
        return _beerRepository.getByPrice(price)
    }
}