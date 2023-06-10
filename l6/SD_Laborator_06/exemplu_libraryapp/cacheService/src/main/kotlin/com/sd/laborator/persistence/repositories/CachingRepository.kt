package com.sd.laborator.persistence.repositories

import com.sd.laborator.persistence.entities.CacheEntity
import com.sd.laborator.persistence.interfaces.ICachingRepository
import com.sd.laborator.persistence.mappers.CacheRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class CachingRepository : ICachingRepository{

    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<CacheEntity?> = CacheRowMapper()

    override fun createTable() {
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS cache(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        timestamp INTEGER,
                                        query VARCHAR(50),
                                        result TEXT,
                                        UNIQUE(timestamp,query,result))""")
    }

    override fun deleteById(id: Int) {
        try {
            _jdbcTemplate.update("DELETE FROM cache WHERE id=?",id)
        } catch (e: UncategorizedSQLException){
            println("An error has occurred in${this.javaClass.name}.delete")
        }
    }

    override fun getByQuery(query: String): CacheEntity? {
        return try {
            _jdbcTemplate.queryForObject("SELECT * FROM cache WHERE query = '$query' ORDER BY timestamp ASC LIMIT 1", _rowMapper)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    override fun add(cache: CacheEntity) {
        try {
            _jdbcTemplate.update("INSERT INTO cache(timestamp, query, result) VALUES (?, ?, ?)", cache.timestamp, cache.query, cache.result)
        } catch (e: UncategorizedSQLException){
            println(e)
            println("An error has occurred in ${this.javaClass.name}.addBook")
        }
    }
}