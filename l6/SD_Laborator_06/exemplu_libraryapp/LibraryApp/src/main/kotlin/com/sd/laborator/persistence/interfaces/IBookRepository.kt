package com.sd.laborator.persistence.interfaces

import com.sd.laborator.business.models.Book
import com.sd.laborator.persistence.entities.BookEntity

interface IBookRepository {
    // Create
    fun createTable()
    fun addBook(item: BookEntity)

    // Retrieve
    fun getAll(): MutableList<BookEntity?>
    fun getByAuthor(author: String): MutableList<BookEntity?>
    fun getByTitle(title: String): MutableList<BookEntity?>
    fun getByPublisher(publisher: String): MutableList<BookEntity?>

}