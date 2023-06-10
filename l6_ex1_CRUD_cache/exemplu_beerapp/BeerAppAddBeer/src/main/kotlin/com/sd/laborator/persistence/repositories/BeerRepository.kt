package com.sd.laborator.persistence.repositories

import com.sd.laborator.models.Beer
import com.sd.laborator.persistence.interfaces.IBeerRepository
import com.sd.laborator.persistence.mappers.BeerRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
open class BeerRepository: IBeerRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<Beer?> = BeerRowMapper()

    override fun add(beer: Beer) {
        try {
            _jdbcTemplate.update("INSERT INTO beers(name, price) VALUES (?, ?)", beer.beerName, beer.beerPrice)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }
}