package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Query

interface ICacheService {
    fun addToCache(query: Query)
    fun getFromCache(queryString: String): Query?
}