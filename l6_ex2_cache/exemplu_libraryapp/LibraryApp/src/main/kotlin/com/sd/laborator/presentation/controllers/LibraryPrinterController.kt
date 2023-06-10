package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
class LibraryPrinterController {
    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    @Autowired
    private lateinit var _rabbitMQ: RabbitComponentController

    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        _rabbitMQ.askCache("/print?format=$format")
        while(!_rabbitMQ.hasCacheResponded()){}
        val cacheResp = _rabbitMQ.getCacheResp()
        if(cacheResp.contains("HIT")){
            return cacheResp.split("~")[1]
        }
        val resp = when (format) {
            "html" -> _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
            "json" -> _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
            "raw" -> _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
            else -> "Not implemented"
        }

        _rabbitMQ.sendToCache("/print?format=$format",resp)
        return resp
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String,
        req: HttpServletRequest): String {
        println(req.queryString)
        _rabbitMQ.askCache("/find?${req.queryString}")
        while(!_rabbitMQ.hasCacheResponded()){}
        val cacheResp = _rabbitMQ.getCacheResp()
        println(cacheResp)
        if(cacheResp.contains("HIT")){
            return cacheResp.split("~")[1]
        }
        var resp = ""
        if (author != "")
            resp =  this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByAuthor(author))
        else if (title != "")
            resp =  this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByTitle(title))
        else if (publisher != "")
            resp =  this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByPublisher(publisher))
        else
            resp = "Not a valid field"
        _rabbitMQ.sendToCache("/find?${req.queryString}", resp)
        return resp
    }

}