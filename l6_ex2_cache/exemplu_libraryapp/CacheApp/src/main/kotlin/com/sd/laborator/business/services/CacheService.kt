package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ICacheService
import org.springframework.stereotype.Service
import java.io.File

@Service
class CacheService : ICacheService {

    private val file = File("/home/student/SD/susDE/sub_practice_rez/l6_ex2_cache/exemplu_libraryapp/CacheApp/cache.txt")

    override fun checkCache(query: String): String {
        file.readLines().forEach {
            if(it.contains(query)) {
                if(checkValidity(it.split("~")[2].toLong()))
                    return it
                else {
                    removeFromCache(query)
                    return ""
                }
            }
        }
        return ""
    }

    private fun removeFromCache(query: String) {
        val newLines: MutableList<String> = mutableListOf()
        file.readLines().forEach {
            if(!it.contains(query))
                newLines.add(it)
        }
        file.writeText(newLines.joinToString("\n\r"))
    }

    //remove if older than 1 minute
    private fun checkValidity(timestamp: Long) : Boolean {
        if(System.currentTimeMillis() - timestamp > 1000 * 60)
            return false
        return true
    }

    override fun addToCache(query: String, rez: String) {
        file.appendText("${query.replace("\n","")}~${rez.replace("\n","")}~${System.currentTimeMillis()}")
        file.appendText("\n")
    }

}