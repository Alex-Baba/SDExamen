package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @Controller
    class LambdaController {
        @Post("/eratostene")
        fun executeEratostene(@Body request: EratosteneRequest): EratosteneResponse {
            return handlerEratostene.apply(request)
        }

        @Post("/check-prime")
        fun executeCheckPrime(@Body request: PrimeCheckRequest): PrimeCheckResponse {
            return handlerCheckPrime.apply(request)
        }

        companion object {
            private val handlerEratostene = EratosteneFunction()
            private val handlerCheckPrime = CheckPrimeFunction()
        }
    }
}