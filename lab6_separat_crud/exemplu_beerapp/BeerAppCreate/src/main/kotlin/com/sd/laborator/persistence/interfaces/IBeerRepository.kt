package com.sd.laborator.persistence.interfaces

import com.sd.laborator.models.Beer

interface IBeerRepository {
    // Create
    fun createTable()
    fun add(beer: Beer)

}