package com.sd.laborator

import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient("random.direct")
interface Producer {

    @Binding("random.direct.in-key")
    fun send(data: ByteArray)
}