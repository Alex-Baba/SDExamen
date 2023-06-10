package com.sd.laborator

import jakarta.inject.Singleton
import java.util.*

@Singleton
class State1Service {
    fun newState(input: String) : String {
        when(input) {
            "a" -> return "stare2"
            "b" -> return "stare3"
        }
        return "bad-input"
    }
}