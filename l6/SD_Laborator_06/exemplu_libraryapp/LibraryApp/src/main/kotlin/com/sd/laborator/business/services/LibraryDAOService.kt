package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.persistence.entities.BookEntity
import com.sd.laborator.persistence.interfaces.IBookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LibraryDAOService : ILibraryDAOService {

    @Autowired
    private lateinit var repo: IBookRepository

    override fun createTable(){
        repo.createTable()
    }

    private fun mapEntityToDTO(bookEntity: BookEntity?): Book {
        return Book(Content(bookEntity?.author, bookEntity?.text, bookEntity?.name, bookEntity?.publisher))
    }

    private fun mapDTOToEntity(book: Book?): BookEntity {
        return BookEntity(0, book?.author!!, book?.content!!, book?.name!!, book?.publisher!!)
    }

    override fun getBooks(): Set<Book> {
        return repo.getAll().map{ this.mapEntityToDTO(it)}.toSet()
    }

    override fun addBook(book: Book) {
        repo.addBook(mapDTOToEntity(book))
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        return repo.getByAuthor(author).map{mapEntityToDTO(it)}.toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return repo.getByTitle(title).map{mapEntityToDTO(it)}.toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return repo.getByPublisher(publisher).map{mapEntityToDTO(it)}.toSet()
    }
}