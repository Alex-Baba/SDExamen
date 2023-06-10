package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

@FunctionBean("perechi-formula")
class PerechiFormulaFunction : FunctionInitializer(), Function<PerechiRequest, PerechiResponse> {
    @Inject
    private lateinit var perechiCalculService: PerechiCalculService

    private val LOG: Logger = LoggerFactory.getLogger(PerechiFormulaFunction::class.java)

    override fun apply(msg : PerechiRequest) : PerechiResponse {
        // preluare numar din parametrul de intrare al functiei
        val listaA = msg.getListA()
        val listaB = msg.getListB()

        val response = PerechiResponse(listOf())

        // se verifica daca numarul nu depaseste maximul
        /*if (number >= perechiCalculService.MAX_SIZE) {
            LOG.error("Parametru prea mare! $number > maximul de ${perechiCalculService.MAX_SIZE}")
            response.setMessage("Se accepta doar parametri mai mici ca " + perechiCalculService.MAX_SIZE)
            return response
        }*/

        LOG.info("Se calculeaza perechile dupa formula 'a * b = a + b * 3' ...")

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        response.setList(perechiCalculService.calcPerechi(listaA, listaB))

        LOG.info("Calcul incheiat!")
        return response
    }   
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = PerechiFormulaFunction()
    function.run(args, { context -> function.apply(context.get(PerechiRequest::class.java))})
}