package com.sd.laborator

import com.sd.laborator.services.CLIService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class CLIWeatherApp
{
    @Bean
    open fun appBean() = CLIService()
}

fun main(args: Array<String>) {
    val context = runApplication<CLIWeatherApp>()
    val app = context.getBean("appBean") as CLIService
    app.runApp()
}

