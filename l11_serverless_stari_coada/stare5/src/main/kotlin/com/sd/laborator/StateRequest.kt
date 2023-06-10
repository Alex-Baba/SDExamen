package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class StateRequest {
    private lateinit var state: String
    private lateinit var input: String

    fun getState(): String {
        return state
    }

    fun getInput(): String {
        return input
    }

    fun setState(s: String){
        state = s
    }

    fun setInput(i: String){
        input = i
    }
}