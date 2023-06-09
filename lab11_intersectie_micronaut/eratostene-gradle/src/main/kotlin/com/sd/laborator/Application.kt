package com.sd.laborator

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @Controller("/intersect")
    class LambdaController {

        @Get(uri="/", produces=["text/plain"])
        fun execute(): IntersectionResponse {
            return handler.get()
        }

        companion object {
            private val handler = IntersectionFuction()
        }
    }
}