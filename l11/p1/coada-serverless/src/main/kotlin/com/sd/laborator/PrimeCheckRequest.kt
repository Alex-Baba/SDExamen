package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class PrimeCheckRequest {

    private lateinit var lista: List<Integer>

    fun getLista(): List<Int> {
        return lista.map { it.toInt() }
    }
}