package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.IBeerCacheService
import com.sd.laborator.models.Query
import org.springframework.stereotype.Service

@Service
class BeerCacheService: IBeerCacheService {

    private val cacheMap: MutableMap<String, String> = mutableMapOf()

    override fun addToCache(query: Query) {
        removeFromCache()
        cacheMap[query.query] = query.result
    }

    override fun getFromCache(queryString: String): Query? {
        return cacheMap[queryString]?.let { Query(queryString, it) }
    }

    override fun removeFromCache() {
        cacheMap.clear()
    }

}