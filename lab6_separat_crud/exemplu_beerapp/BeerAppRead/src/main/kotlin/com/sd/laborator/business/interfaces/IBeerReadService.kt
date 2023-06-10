package com.sd.laborator.business.interfaces

import com.sd.laborator.models.Beer

interface IBeerReadService {
    fun getBeers(): MutableList<Beer?>
    fun getBeerByName(name: String): Beer?
    fun getBeerByPrice(price: Float):  MutableList<Beer?>
}