package com.sd.laborator

import jakarta.inject.Singleton

@Singleton
class SumService {

    fun calcSum(number: Int, list: List<Int>): Long {
        return list.take(number).fold(0) { acc: Int, it: Int -> acc + it * it }.toLong()
    }
}