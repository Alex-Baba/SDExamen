package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ICacheService
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

    @Autowired
    private lateinit var _cacheService : ICacheService

    @Autowired
    fun initTemplate() {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${spring.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        println(msg)
        if(msg.contains("query")) {
            val resp  =_cacheService.checkCache(msg.split("~")[1])
            if(resp == "")
                sendMessage("MISS~")
            else {
                var newResp = resp.drop(resp.split("~")[0].length + 1)
                println(newResp)
                sendMessage("HIT~$newResp")
            }
        }
        else if(msg.contains("put")){
            _cacheService.addToCache(msg.split("~")[1], msg.split("~")[2])
        }
    }

    private fun sendMessage(msg: String) {
        println("Message to send: $msg")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKey(), msg)
    }
}