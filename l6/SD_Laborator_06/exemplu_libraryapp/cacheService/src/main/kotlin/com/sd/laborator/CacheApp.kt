package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CacheApp

fun main(args: Array<String>) {
    runApplication<CacheApp>(*args)
}
