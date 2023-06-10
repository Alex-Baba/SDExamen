package com.sd.laborator.persistence.entities

data class CacheEntity(
    var id: Int,
    var timestamp: Long,
    var query: String,
    var result: String
)