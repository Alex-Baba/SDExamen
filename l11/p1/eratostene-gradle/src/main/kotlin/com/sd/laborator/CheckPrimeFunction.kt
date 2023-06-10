package com.sd.laborator

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

@FunctionBean("checkPrime")
class CheckPrimeFunction : FunctionInitializer(), Function<PrimeCheckRequest, PrimeCheckResponse> {

    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun apply(t: PrimeCheckRequest): PrimeCheckResponse {
        val list = t.getLista()
        val response = PrimeCheckResponse()

        val maxNumber = list.maxOf { it }

        LOG.info("numarul maxim din lista e $maxNumber")

        response.setPrimesList(eratosteneSieveService.findPrimesLessThan(maxNumber + 1).filter { it -> list.contains(it) })

        LOG.info("S-a calculat ceva")

        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) {
    val function = CheckPrimeFunction()
    function.run(args, { context -> function.apply(context.get(PrimeCheckRequest::class.java))})
}