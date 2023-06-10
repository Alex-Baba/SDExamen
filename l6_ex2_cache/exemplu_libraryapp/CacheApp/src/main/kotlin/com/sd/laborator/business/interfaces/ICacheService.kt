package com.sd.laborator.business.interfaces

interface ICacheService {
    fun checkCache(query: String) : String
    fun addToCache(query: String, rez: String)
}