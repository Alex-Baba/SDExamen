package com.sd.laborator.persistence.interfaces

import com.sd.laborator.models.Beer

interface IBeerRepository {
    // Update
    fun update(beer: Beer)
}