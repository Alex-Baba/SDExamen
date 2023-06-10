package com.sd.orchestration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MainService

fun main(args: Array<String>) {
    runApplication<MainService>(*args)
}

