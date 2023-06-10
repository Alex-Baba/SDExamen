package com.sd.laborator

import jakarta.inject.Singleton
import java.util.*
import java.util.concurrent.ThreadLocalRandom


@Singleton
class RandomListService {
    fun getRandomList(n: Int) : List<Int>{
        val numbers: MutableList<Int> = mutableListOf()


        for (i in 0..n) {
            val randInt: Int = ThreadLocalRandom.current().nextInt(0, 100)
            numbers.add(randInt)
        }
        numbers.shuffle();

        return numbers
    }
}
