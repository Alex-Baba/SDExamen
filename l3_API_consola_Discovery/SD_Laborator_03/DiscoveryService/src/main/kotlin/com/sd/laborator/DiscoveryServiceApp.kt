package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DiscoveryServiceApp

fun main(args: Array<String>) {
    runApplication<DiscoveryServiceApp>()
}

