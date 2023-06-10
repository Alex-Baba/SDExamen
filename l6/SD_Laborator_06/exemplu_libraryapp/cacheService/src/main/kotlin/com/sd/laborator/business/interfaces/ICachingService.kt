package com.sd.laborator.business.interfaces

import com.sd.laborator.business.models.CacheModel

interface ICachingService {
    fun exists(query: String): String?
    fun addToCache(cache: CacheModel)
    fun createTable()
}