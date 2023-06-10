package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ICachingService
import com.sd.laborator.business.models.CacheModel
import com.sd.laborator.persistence.entities.CacheEntity
import com.sd.laborator.persistence.interfaces.ICachingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CachingService : ICachingService {

    @Autowired
    private lateinit var repo: ICachingRepository

    override fun exists(query: String): String? {
        val cachedQuery = repo.getByQuery(query)
        if(cachedQuery != null)
        {
            if(System.currentTimeMillis() - cachedQuery.timestamp >= 3600000)
            {
                repo.deleteById(cachedQuery.id)
                return null
            }
            return cachedQuery.result
        }
        return null
    }

    private fun mapDTOToEntity(cache: CacheModel): CacheEntity {
        val time: Long = System.currentTimeMillis()
        return CacheEntity(0, time, cache.query, cache.result)
    }

    override fun addToCache(cache: CacheModel) {
        repo.add(mapDTOToEntity(cache))
    }

    override fun createTable() {
        repo.createTable()
    }
}