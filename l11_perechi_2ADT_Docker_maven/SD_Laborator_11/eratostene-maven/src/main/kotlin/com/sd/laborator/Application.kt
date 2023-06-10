package com.sd.laborator

import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
        val handler = PerechiFormulaFunction()
        val A = (1..100).toList()
        val B = (1..100).toList()
        val perechi = handler.apply(PerechiRequest(A,B)).getList()
        println(perechi)
    }
}