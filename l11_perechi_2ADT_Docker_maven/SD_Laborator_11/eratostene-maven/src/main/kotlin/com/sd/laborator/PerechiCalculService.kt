package com.sd.laborator

import jakarta.inject.Singleton

@Singleton
class PerechiCalculService {

    fun calcPerechi(listA: List<Int>, listB: List<Int>) : List<Pair<Int,Int>> {
        //zeu kotlin in calcul functional BOI!
        return listA.map { a -> listB.filter { b -> a * b == a + b * 3 }.map { b_bun -> Pair(a,b_bun) } }.toList().flatten()
    }
}