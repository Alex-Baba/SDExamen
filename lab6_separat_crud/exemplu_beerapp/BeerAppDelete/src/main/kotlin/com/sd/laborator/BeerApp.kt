package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BeerAppDelete

fun main(args: Array<String>) {
    runApplication<BeerAppDelete>(*args)
}