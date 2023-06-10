package com.sd.laborator.persistence.interfaces

import com.sd.laborator.models.Beer

interface IBeerRepository {
    // Retrieve
    fun getAll(): MutableList<Beer?>
    fun getByName(name: String): Beer?
    fun getByPrice(price: Float): MutableList<Beer?>

}