package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class WeatherApp
{
    @Bean
    open fun serviceBean() = ServiceClass()
}

/*
    !!! CRED CA NU MAI MERGE API ASTA...BAFTA!!!
 */

fun main(args: Array<String>) {
    val context = runApplication<WeatherApp>()
    (context.getBean("serviceBean") as ServiceClass).registerToDiscovery()
}

