package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class APIGateway
{
    @Bean
    open fun serviceBean() = ServiceClass()
}

fun main(args: Array<String>) {
    val context = runApplication<APIGateway>()
    (context.getBean("serviceBean") as ServiceClass).registerToDiscovery()
}

