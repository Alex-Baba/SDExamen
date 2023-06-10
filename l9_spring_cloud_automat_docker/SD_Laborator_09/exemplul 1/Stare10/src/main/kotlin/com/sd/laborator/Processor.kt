package com.sd.laborator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder

interface OtherSource {
    @Output("stare00")
    fun stare00_out(): MessageChannel?

    @Input("stare00")
    fun stare00_input(): MessageChannel?

    @Output("stare01")
    fun stare01_out(): MessageChannel?

    @Input("stare01")
    fun stare01_input(): MessageChannel?

    @Output("stare10")
    fun stare10_out(): MessageChannel?

    @Input("stare10")
    fun stare10_input(): MessageChannel?

    @Output("stare11")
    fun stare11_out(): MessageChannel?

    @Input("stare11")
    fun stare11_input(): MessageChannel?

    @Input("sink")
    fun sink(): MessageChannel?

}

@EnableBinding(OtherSource::class)
@SpringBootApplication
open class SpringDataFlowTimeProcessorApplication {

    @Autowired
    private lateinit var channels: OtherSource

    @Transformer
    fun transform(input: String?) {
        println(input)
        when(input) {
            "0" -> channels.stare10_out()?.send(MessageBuilder.withPayload(input.drop(1)).build())
            "1" -> channels.stare01_out()?.send(MessageBuilder.withPayload(input.drop(1)).build())
            //else -> "badinput"
        }
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataFlowTimeProcessorApplication>(*args)
}