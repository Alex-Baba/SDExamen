package com.sd.laborator.components

import com.sd.laborator.interfaces.ChainComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ConsumerComponent : ChainComponent {

    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    private lateinit var nextComponent: GenerateStacksComponent

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${stackapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        // the result: 114,101,103,101,110,101,114,97,116,101,95,65 --> needs processing
        val processed_msg = (msg.split(",").map { it.toInt().toChar() }).joinToString(separator="")
        println("messaged received: ")
        println(processed_msg)
        nextInChain(processed_msg)
    }

    override fun nextInChain(op: String) {
        nextComponent.nextInChain(op)
    }

}