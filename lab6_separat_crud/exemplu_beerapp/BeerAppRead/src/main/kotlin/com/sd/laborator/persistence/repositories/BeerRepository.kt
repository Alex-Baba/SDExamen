package com.sd.laborator.persistence.repositories

import com.sd.laborator.models.Beer
import com.sd.laborator.persistence.interfaces.IBeerRepository
import com.sd.laborator.persistence.mappers.BeerRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class BeerRepository: IBeerRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<Beer?> = BeerRowMapper()

    override fun getAll(): MutableList<Beer?> {
        return _jdbcTemplate.query("SELECT * FROM beers", _rowMapper)
    }

    override fun getByName(name: String): Beer? {
        return try {
            _jdbcTemplate.queryForObject("SELECT * FROM beers WHERE name = '$name'", _rowMapper)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    override fun getByPrice(price: Float): MutableList<Beer?> {
        return _jdbcTemplate.query("SELECT * FROM beers WHERE price <= $price", _rowMapper)
    }
}