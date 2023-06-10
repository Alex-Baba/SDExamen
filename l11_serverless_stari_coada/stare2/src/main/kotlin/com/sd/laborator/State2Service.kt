package com.sd.laborator

import jakarta.inject.Singleton
import java.util.*

@Singleton
class State2Service {
    fun newState(input: String) : String {
        when(input) {
            "a" -> return "stare2"
            "b" -> return "stare4"
        }
        return "bad-input"
    }
}