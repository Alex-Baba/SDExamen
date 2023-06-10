package com.sd.laborator.business.interfaces

import com.sd.laborator.models.Beer

interface IBeerUpdateService {
    fun updateBeer(beer: Beer)

}