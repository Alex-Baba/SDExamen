package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BeerAppUpdate

fun main(args: Array<String>) {
    runApplication<BeerAppUpdate>(*args)
}