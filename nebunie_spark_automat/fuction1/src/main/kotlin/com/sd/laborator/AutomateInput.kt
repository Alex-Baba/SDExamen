package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class AutomateInput {
    private var _currentState: Int = -1 // 0 to 4
    private var _input: Boolean = false // 0 to 4


    fun setInput(input: Boolean){
        _input = input
    }

    fun setCurrentState(currentState: Int){
        _currentState = currentState
    }

    fun getInput(): Boolean{
        return _input
    }

    fun getCurrentState(): Int {
        return _currentState.toInt()
    }
}