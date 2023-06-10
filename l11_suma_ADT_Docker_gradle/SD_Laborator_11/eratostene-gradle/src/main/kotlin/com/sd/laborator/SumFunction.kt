package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import jakarta.inject.Inject

@FunctionBean("square_sum")
class SumFunction : FunctionInitializer(), Function<SumRequest, SumResponse> {
    @Inject
    private lateinit var sumService: SumService

    private val LOG: Logger = LoggerFactory.getLogger(SumFunction::class.java)

    override fun apply(msg : SumRequest) : SumResponse {
        // preluare numar din parametrul de intrare al functiei
        val number = msg.getNumber()
        val list = msg.getList()

        val response = SumResponse(-1)

        // se verifica daca lista e prea mica fata de N
        if (number > list.size) {
            LOG.error("Parametru prea mare! $number > dimensiunea listei (${list.size})")
            //response.setMessage("Se accepta doar parametri mai mici ca " + sumService.MAX_SIZE)
            return response
        }

        LOG.info("Se calculeaza suma pana la $number ...")

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        response.setSum(sumService.calcSum(number, list))

        LOG.info("Calcul incheiat!")

        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) {
    val function = SumFunction()
    function.run(args, { context -> function.apply(context.get(SumRequest::class.java))})
}