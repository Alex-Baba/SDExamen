package com.sd.laborator.persistence.interfaces

import com.sd.laborator.persistence.entities.CacheEntity

interface ICachingRepository {
    fun getByQuery(query: String): CacheEntity?
    fun add(cache: CacheEntity)
    fun createTable()
    fun deleteById(id: Int)
}