package com.sd.laborator.persistence.interfaces

import com.sd.laborator.models.Beer

interface IBeerRepository {
    fun add(beer: Beer)
}