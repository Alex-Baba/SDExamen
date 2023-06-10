package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Replicator

fun main(args: Array<String>) {
    runApplication<Replicator>(*args)
}