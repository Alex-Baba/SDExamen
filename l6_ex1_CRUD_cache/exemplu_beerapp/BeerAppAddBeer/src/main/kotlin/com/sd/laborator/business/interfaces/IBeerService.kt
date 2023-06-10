package com.sd.laborator.business.interfaces

import com.sd.laborator.models.Beer

interface IBeerService {
    fun addBeer(beer: Beer)
}