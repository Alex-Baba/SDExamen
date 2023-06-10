package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class SumRequest(n: Int, l: List<Int>) {

    private var number: Int = n
    private var list: List<Int> = l

    fun getNumber(): Int {
        return number
    }

    fun getList(): List<Int> {
        return list
    }
}