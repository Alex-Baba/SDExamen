package com.sd.laborator.presentation.controllers

import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RabbitComponentController {
    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    private var cacheResp = ""
    private var cacheStatus : Boolean = false

    fun hasCacheResponded() : Boolean{
        if(cacheStatus)
        {
            cacheStatus = false
            return true
        }
        return false
    }

    fun getCacheResp() = cacheResp // HIT~rez sau MISS~

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${spring.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        println("received:$msg")
        cacheStatus = true
        cacheResp = msg
    }

    fun askCache(query: String) {
        sendMessage("query~$query")
    }

    private fun sendMessage(msg: String) {
        println("Message to send: $msg")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKey(), msg)
    }

    fun sendToCache(s: String, resp: String) {
        sendMessage("put~$s~$resp")
    }
}