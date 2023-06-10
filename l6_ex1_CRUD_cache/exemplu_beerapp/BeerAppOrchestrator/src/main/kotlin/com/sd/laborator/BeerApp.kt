package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BeerApp

fun main(args: Array<String>) {
    runApplication<BeerApp>(*args)
}