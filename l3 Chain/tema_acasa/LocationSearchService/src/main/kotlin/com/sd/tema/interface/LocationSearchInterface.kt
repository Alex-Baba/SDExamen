package com.sd.tema.`interface`

interface LocationSearchInterface {
    fun getLocationId(locationName: String): Int
    fun getLocationCode(woid: Int): String
}