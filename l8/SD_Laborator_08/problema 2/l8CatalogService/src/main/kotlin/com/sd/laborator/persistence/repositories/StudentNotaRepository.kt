package com.sd.laborator.persistence.repositories

import com.sd.laborator.persistence.entities.StudentNotat
import com.sd.laborator.persistence.interfaces.IStudentNotaRepository
import com.sd.laborator.persistence.mappers.StudentNotaRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
open class StudentNotaRepository : IStudentNotaRepository{

    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<StudentNotat?> = StudentNotaRowMapper()

    override fun createTable() {
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS StudentId(
                                            id INTEGER,
	                                        PRIMARY KEY(id)
                                            )""")

//        {1..4}?.forEach {
//            _jdbcTemplate.update("INSERT INTO StudentId (id) Values ($it)")
//        }

        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS StudentNota(
                                        id INTEGER,
                                        intrebare INTEGER,
                                        FOREIGN KEY(id) REFERENCES StudentId(id))""")
    }

    override fun deleteById(id: Long) {
        try {
            _jdbcTemplate.update("DELETE FROM StudentNota WHERE id=?", id)
        } catch (e: UncategorizedSQLException) {
            println("An error has occurred in${this.javaClass.name}.delete")
        }
    }

    override fun getInterbari(): Long? {
        return try{
            _jdbcTemplate.queryForObject("SELECT * FROM StudentNota ORDER BY intrebare DESC LIMIT 1",_rowMapper)?.intrebare
        } catch (e: UncategorizedSQLException) {
            println("An error has occurred in${this.javaClass.name}.delete")
            return null
        }
    }

    override fun getAll(): List<StudentNotat?> {
        return try{
            _jdbcTemplate.query("SELECT * FROM StudentNota", _rowMapper)
        } catch (e: UncategorizedSQLException) {
            println("An error has occurred in${this.javaClass.name}.delete")
            return listOf()
        }
    }

//    override fun getById(id: Long): List<StudentNotat?> {
//        return try {
//            _jdbcTemplate.query("SELECT * FROM StudentNota WHERE id = '$id'", _rowMapper)
//        } catch (e: EmptyResultDataAccessException) {
//            listOf()
//        }
//    }

    override fun getIds(): List<Int> {
        try{
            val lista = mutableListOf<Int>()
            _jdbcTemplate.queryForList("SELECT * FROM StudentId").forEach {
                lista.add(it["id"].toString().toInt())
            }
            lista.forEach(::println)
            return lista.toList()
        } catch (e: UncategorizedSQLException) {
            println("An error has occurred in${this.javaClass.name}.delete")
            return listOf()
        }
    }

    override fun add(nota: StudentNotat) {
        try {
            _jdbcTemplate.update("INSERT INTO StudentNota(id, intrebare) VALUES (?, ?)", nota.id, nota.intrebare)
        } catch (e: UncategorizedSQLException){
            println(e)
            println("An error has occurred in ${this.javaClass.name}.addBook")
        }
    }
}