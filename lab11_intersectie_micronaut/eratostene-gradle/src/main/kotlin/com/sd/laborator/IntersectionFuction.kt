package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier

// curl --location --request POST 'http://localhost:8080/'
@FunctionBean("intersection")
class IntersectionFuction : FunctionInitializer(), Supplier<IntersectionResponse> {
    @Inject
    private lateinit var _intersectionService: IntersectionService

    @Inject
    private lateinit var _randomListGenerator: RandomListService

    private val LOG: Logger = LoggerFactory.getLogger(IntersectionFuction::class.java)

    override fun get(): IntersectionResponse {

        val response = IntersectionResponse()


        LOG.info("Se calculeaza intersectia...")

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul

        var list1 = _randomListGenerator.getRandomList(100)
        var list2 = _randomListGenerator.getRandomList(100)

        LOG.info("Prima lista:$list1")
        LOG.info("A doua lista:$list2")

        LOG.info("Se calculeaza intersectia...")

        var intersectionList = _intersectionService.getListIntersection(list1, list2)

        LOG.info("Lista intersectii:$intersectionList")
        response.setIntersection(intersectionList)

        response.setMessage("Calcul efectuat cu succes!")

        LOG.info("Calcul incheiat!")
        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
   val function = IntersectionFuction()
    function.run(args, { context -> function.get()})

    println("End of aplication")
}