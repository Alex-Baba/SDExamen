package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class PrimeCheckResponse {

    private var primesList: List<Int>? = null

    fun getPrimesList(): List<Int>? {
        return primesList
    }

    fun setPrimesList(primes: List<Int>?) {
        primesList = primes
    }
}