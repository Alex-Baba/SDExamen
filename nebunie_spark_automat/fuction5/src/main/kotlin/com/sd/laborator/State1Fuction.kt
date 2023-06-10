package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import java.util.function.Function
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@FunctionBean("state1fuction")
class State1Fuction : FunctionInitializer(), Function<AutomateInput,AutomateOutput> {

    @Inject
    private val LOG: Logger = LoggerFactory.getLogger(State1Fuction::class.java)

    override fun apply(input: AutomateInput): AutomateOutput {

        val output: AutomateOutput = AutomateOutput()
        var currentInputValue: Boolean = input.getInput()

        LOG.info("Suntem in fuctia 1 cu input-ul: ${currentInputValue} si starea curenta ${input.getCurrentState()}.")

        if (input.getCurrentState() == 4){

            if (currentInputValue) {
                output.setCurrentState(0)
                LOG.info("----Mergem spre 0")
            }
            else{
                output.setCurrentState(0)
                LOG.info("----Mergem spre 0")
            }
        }
        return output
    }
}

fun main(args : Array<String>) {
    val function = State1Fuction()
    function.run(args, { context -> function.apply(context.get(AutomateInput::class.java))})
}