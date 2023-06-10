package com.sd.laborator.services

import com.sd.laborator.interfaces.IAutomateInputQueue
import com.sd.laborator.pojo.AutomateInput
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AutomateInputQueue(private val kafkaTemplate: KafkaTemplate<String, String>) : IAutomateInputQueue {

    override fun pushInput(autInput: AutomateInput) {
        kafkaTemplate.send("topic_exemplu_kotlin", autInput.input.toString())
    }
}