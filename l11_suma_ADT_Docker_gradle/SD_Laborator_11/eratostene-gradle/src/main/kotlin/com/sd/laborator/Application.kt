package com.sd.laborator

import io.micronaut.runtime.Micronaut
import kotlin.random.Random

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)

        //nu stiu daca vrea alt program sau pot face tot aici restul...fac aici, mi-e lene
        val handler = SumFunction()
        val A = (1..100).map { /*Random.nextInt()*/ it }
        val B = (1..100).map { handler.apply(SumRequest(it, A)).getSum() }
        println(B)
    }
}