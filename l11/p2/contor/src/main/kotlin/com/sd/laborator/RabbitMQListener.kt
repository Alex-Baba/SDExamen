package com.sd.laborator
import io.micronaut.rabbitmq.annotation.RabbitListener

@RabbitListener
class RabbitMQListner {

    @io.micronaut.rabbitmq.annotation.Queue("lab11.increment")
    fun receive(data: ByteArray) {
        val msg = String(data)
        println(msg)
        val req = Request()
        req.setId(msg)
        handler.execute(req)
    }

    companion object {
        private val handler = RequestHandler()
    }
}