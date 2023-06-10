package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller
class InputController {

    @Inject
    private lateinit var producer: Producer

    @Post(uri="/input", consumes=["text/plain"])
    fun index(@Body msg: String) {
        println("input: ${msg}")
        producer.send(msg.toByteArray())
    }
}