package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ICachingService
import com.sd.laborator.business.models.CacheModel
import com.sd.laborator.presentation.config.RabbitMQComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CachingController {

    @Autowired
    private lateinit var cachingService: ICachingService

    @Autowired
    private lateinit var rabbitMq: RabbitMQComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = rabbitMq.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${library.rabbitmq.stare.queue}"])
    fun receiveStare(msg: String) {
        println("Stare:")
        val (query, result) = msg.split("~")
        println(query)
        println(result)
        cachingService.addToCache(CacheModel(query, result))
    }

    @RabbitListener(queues = ["\${library.rabbitmq.comenzi.queue}"])
    fun receiveComenzi(msg: String) {
        println("Comanda:")
        println(msg)
        if(msg == "createTable") {
            cachingService.createTable()
            return
        }

        val result = cachingService.exists(msg)
        if(result != null){
            sendFisier("HIT~$result")
        }
        else{
            sendFisier("MISS")
        }
    }

    private fun sendFisier(msg: String) {
        println("message: ")
        println(msg)
        this.amqpTemplate.convertAndSend(rabbitMq.getExchange(),
            rabbitMq.getRoutingKey(),
            msg)
    }
}