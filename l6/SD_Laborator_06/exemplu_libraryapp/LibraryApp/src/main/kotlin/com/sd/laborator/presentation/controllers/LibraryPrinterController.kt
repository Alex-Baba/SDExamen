package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import com.sd.laborator.business.models.Book
import com.sd.laborator.presentation.config.RabbitMQComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LibraryPrinterController {
    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    @Autowired
    private lateinit var rabbitMqComponent: RabbitMQComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        amqpTemplate = rabbitMqComponent.rabbitTemplate()
    }


    @RequestMapping("/print", method = [RequestMethod.GET])
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        val books: String = when (format) {
            "html" -> _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
            "json" -> _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
            "raw" -> _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
            else -> "Not implemented"
        }
        sendCommand("/print?format=${format}~${books}")
        return books
    }

    @RequestMapping("/find-json", method = [RequestMethod.GET])
    fun customFindJson(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ): String {
        var books: String = ""
        if (author != "")
            books = this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByAuthor(author))
        if (title != "")
            books = this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByTitle(title))
        if (publisher != "")
            books = this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByPublisher(publisher))
        if (author == "" && title == "" && publisher == "")
            books = "Not a valid field"
        val params: String = "?" + if(author != "") "author=${author.replace(" ", "%20")}&" else "" + if(title != "") "title=${title.replace(" ", "%20")}&" else "" + if(publisher != "") "publisher=${publisher.replace(" ", "%20")}&" else ""
        sendCommand("/find-json" + params.dropLast(1) + "~${books}")
        return books
    }

    @GetMapping("/find")
    fun customFindCustomFormat(
        @RequestParam(required = true, name = "format") format: String,
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ) : String {
        var booksString: String = ""
        if (author == "" && title == "" && publisher == "")
            booksString =  "Not a valid field"
        var books = setOf<Book>()
        if (author != "")
            books = this._libraryDAOService.findAllByAuthor(author)
        if (title != "")
            books = this._libraryDAOService.findAllByTitle(title)
        if (publisher != "")
            books = this._libraryDAOService.findAllByPublisher(publisher)
        booksString = when (format) {
            "html" -> _libraryPrinterService.printHTML(books)
            "json" -> _libraryPrinterService.printJSON(books)
            "raw" -> _libraryPrinterService.printRaw(books)
            else -> "Not implemented"
        }
        val params: String = "" + if(author != "") "author=${author.replace(" ", "%20")}&" else "" + if(title != "") "title=${title.replace(" ", "%20")}&" else "" + if(publisher != "") "publisher=${publisher.replace(" ", "%20")}&" else ""
        sendCommand("/find?format=${format}&${params}".dropLast(1) + "~${booksString}")
        return booksString
    }

    @PostMapping("/add-books")
    fun uploadJsonToDatabase(@RequestBody books: List<Book>) {
        books.forEach {
            _libraryDAOService.addBook(it)
        }
        return
    }

    @GetMapping("/table")
    fun createTable() : String{
        _libraryDAOService.createTable()
        return "Created table!"
    }

    fun sendCommand(message: String) {
        println("message: ")
        println(message)
        this.amqpTemplate.convertAndSend(rabbitMqComponent.getExchange(),
            rabbitMqComponent.getRoutingKey(),
            message)
    }

}