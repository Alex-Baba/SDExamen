package com.sd.laborator.business.interfaces

import com.sd.laborator.models.Beer

interface IBeerCreateService {
    fun createBeerTable()
    fun addBeer(beer: Beer)
}