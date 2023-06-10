package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

@FunctionBean("state1")
class State2Function : FunctionInitializer(), Function<StateRequest, String> {
    @Inject
    private lateinit var state2Service: State2Service

    private val LOG: Logger = LoggerFactory.getLogger(State2Function::class.java)

    override fun apply(msg : StateRequest) : String {
        val state = msg.getState()
        val input = msg.getInput()

        if(state != "stare2")
        {
            println("Not my state")
            return ""
        }

        return state2Service.newState(input)
    }   
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = State2Function()
    function.run(args, { context -> function.apply(context.get(StateRequest::class.java))})
}