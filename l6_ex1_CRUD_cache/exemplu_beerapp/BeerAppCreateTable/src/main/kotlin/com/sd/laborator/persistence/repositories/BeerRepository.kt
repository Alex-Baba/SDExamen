package com.sd.laborator.persistence.repositories

import com.sd.laborator.models.Beer
import com.sd.laborator.persistence.interfaces.IBeerRepository
import com.sd.laborator.persistence.mappers.BeerRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
open class BeerRepository: IBeerRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<Beer?> = BeerRowMapper()

    override fun createTable() {
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS beers(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        name VARCHAR(100) UNIQUE,
                                        price FLOAT)""")
    }
}