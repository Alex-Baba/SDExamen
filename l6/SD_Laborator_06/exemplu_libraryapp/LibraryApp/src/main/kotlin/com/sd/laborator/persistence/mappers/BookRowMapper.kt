package com.sd.laborator.persistence.mappers

import com.sd.laborator.persistence.entities.BookEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class BookRowMapper : RowMapper<BookEntity?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): BookEntity {
        return BookEntity(rs.getInt("id"), rs.getString("author"), rs.getString("text"), rs.getString("title"), rs.getString("publisher"))
    }
}