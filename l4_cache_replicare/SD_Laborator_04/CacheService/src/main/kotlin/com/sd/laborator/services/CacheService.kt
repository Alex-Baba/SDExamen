package com.sd.laborator.services;

import com.sd.laborator.interfaces.ICacheService
import com.sd.laborator.pojo.Query
import org.springframework.stereotype.Service
import java.io.File

@Service
class CacheService : ICacheService {

    private val filePath : String = "CacheDB.txt"
    private val file: File = File(filePath)

    override fun addToCache(query: Query) {
        removeFromCache(query)
        file.appendText(query.toString())
        file.appendText("\n\r")
    }

    private fun checkValidity(query: Query): Boolean {
        if(System.currentTimeMillis() - query.timestamp.toLong() > 30 * 60 * 1000)
            return false
        return true
    }

    private fun removeFromCache(query: Query) {
        val newFileContent : MutableList<String> = mutableListOf()
        file.readLines().forEach {
            if(it.split("~")[0] != query.query)
                newFileContent.add(it)
        }
        file.writeText(newFileContent.joinToString("\n"))
    }

    override fun getFromCache(queryString: String): Query? {
        var query : Query? = null
        file.readLines().forEach {
            val queryVals = it.split("~")
            if(queryVals[0] == queryString)
                query = Query(queryVals[0], queryVals[1], queryVals[2].toLong())
        }

        if(query?.let { checkValidity(it) } == true)
            return query
        else {
            query?.let { removeFromCache(it) }
        }
        return query
    }

}
