package com.sd.laborator.persistence.mappers

import com.sd.laborator.persistence.entities.StudentNotat
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

open class StudentNotaRowMapper : RowMapper<StudentNotat?> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): StudentNotat {
        return StudentNotat(rs.getInt("id"), rs.getLong("intrebare"))
    }
}