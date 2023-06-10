package com.sd.laborator.business.interfaces

import com.sd.laborator.models.Query

interface IBeerCacheService {
    fun addToCache(query: Query)
    fun getFromCache(queryString: String): Query?
    fun removeFromCache()
}