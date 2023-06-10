package com.sd.laborator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.io.File

@EnableBinding(Source::class)
@SpringBootApplication
open class SpringDataFlowTimeSourceApplication {
    companion object{
        var index = 0
        val file = File("/home/student/SD/homeworks-1311a-IulianMurariu-Tanasache/l9/SD_Laborator_09/exemplul 1/DataFlow Source/comenzi.txt")
        val commands = file.readLines().toMutableList()
    }

    fun createPayload(): String{
        if(commands.size <= index)
            return ""
        val command = commands[index]
        println(command)
        index++
        println(index)
        return "$command~\"\""
    }

    @Bean
    open fun returnFunc(): () -> String {
        return { createPayload() }
    }

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = [Poller(fixedDelay = "5000", maxMessagesPerPoll = "1")])
    open fun timeMessageSource(@Autowired func: () -> String): () -> Message<String> {
        return { MessageBuilder.withPayload(func()).build() }
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataFlowTimeSourceApplication>(*args)
}