package com.sd.laborator

import io.micronaut.configuration.kafka.annotation.*
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitClient
import io.micronaut.rabbitmq.annotation.RabbitListener
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement
import io.micronaut.runtime.Micronaut
import java.util.*

var stare : String? = null
var input : String? = null

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val ctx = Micronaut.run(Application::class.java, *args)
        val handler = State1Function()
        val rabbitConsumer = ctx.getBean(RabbitMQConsumer::class.java)
        val kafkaConsumer = ctx.getBean(KafkaConsumer::class.java)
        val kafkaProducer = ctx.getBean(KafkaProducer::class.java)
        val rabbitProducer = ctx.getBean(RabbitProducer::class.java)
        kafkaProducer.sendProduct("stare1")
        while(true){
            if(stare != null && input != null) {
                println("$stare - $input")
                val req = StateRequest()
                req.setState(stare!!)
                req.setInput(input!!)
                val newState = handler.apply(req)
                stare = null
                input = null
                if(newState != "")
                {
                    if (newState=="bad-input"){
                        kafkaProducer.sendProduct("state1")
                    }else {
                        kafkaProducer.sendProduct(newState)
                    }
                }
                else {
                    rabbitProducer.send(req.getInput().toByteArray())
                    kafkaProducer.sendProduct(req.getState())
                }
            }
        }
    }

    @RabbitListener
    class RabbitMQConsumer {

        val messageLengths: MutableList<String> = Collections.synchronizedList(ArrayList())
        public var messageRabbit: String = ""

        @Queue("random.direct.in")
        fun receive(data: ByteArray, acknowledgement: RabbitAcknowledgement) {
            val string = String(data)
            messageLengths.add(string)
            println("Kotlin received ${data.size} bytes from RabbitMQ: ${string}")
            acknowledgement.ack()
            input = string
        }
    }

    @RabbitClient("random.direct")
    interface RabbitProducer {

        @Binding("random.direct.in-key")
        fun send(data: ByteArray)
    }

    @KafkaClient
    public interface KafkaProducer {

        @Topic("topic_stare")
        open fun sendProduct(name: String?)
    }

    @KafkaListener(offsetReset = OffsetReset.EARLIEST, groupId = "stare1", threads = 1)
    class KafkaConsumer {
        public var messageKafka: String = ""

        @Topic("topic_stare")
        fun receive(name: String) {
            println("Got Product - $name")
            stare = name
        }
    }
}