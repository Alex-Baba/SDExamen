package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class PerechiRequest(l1: List<Int>, l2: List<Int>) {
    private var listA: List<Int> = l1
    private var listB: List<Int> = l2

    fun getListA(): List<Int> {
        return listA
    }

    fun getListB(): List<Int> {
        return listB
    }
}