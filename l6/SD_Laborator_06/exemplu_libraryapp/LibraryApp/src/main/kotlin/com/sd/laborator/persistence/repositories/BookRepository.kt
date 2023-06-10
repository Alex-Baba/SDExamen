package com.sd.laborator.persistence.repositories

import com.sd.laborator.persistence.entities.BookEntity
import com.sd.laborator.persistence.interfaces.IBookRepository
import com.sd.laborator.persistence.mappers.BookRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class BookRepository: IBookRepository {
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<BookEntity?> = BookRowMapper()

    override fun createTable() {
        _jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS books(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        author VARCHAR(50),
                                        title VARCHAR(50),
                                        publisher VARCHAR(50), 
                                        text TEXT,
                                        UNIQUE(author,title,publisher,text))""")
    }

    override fun addBook(item: BookEntity) {
        try {
            _jdbcTemplate.update("INSERT INTO books(author, title, publisher, text) VALUES (?, ?, ?, ?)", item.author, item.name, item.publisher, item.text)
        } catch (e: UncategorizedSQLException){
            println(e)
            println("An error has occurred in ${this.javaClass.name}.addBook")
        }
    }

    override fun getAll(): MutableList<BookEntity?> {
        return _jdbcTemplate.query("SELECT * FROM books", _rowMapper)
    }

    override fun getByAuthor(author: String): MutableList<BookEntity?> {
        return try {
            _jdbcTemplate.query("SELECT * FROM books WHERE author = '$author'", _rowMapper)
        } catch (e: EmptyResultDataAccessException) {
            mutableListOf()
        }
    }

    override fun getByTitle(title: String): MutableList<BookEntity?>  {
        return try {
            _jdbcTemplate.query("SELECT * FROM books WHERE title = '$title'", _rowMapper)
        } catch (e: EmptyResultDataAccessException) {
            mutableListOf()
        }
    }

    override fun getByPublisher(publisher: String): MutableList<BookEntity?>  {
        return try {
            _jdbcTemplate.query("SELECT * FROM books WHERE publisher = '$publisher'", _rowMapper)
        } catch (e: EmptyResultDataAccessException) {
            mutableListOf()
        }
    }
}