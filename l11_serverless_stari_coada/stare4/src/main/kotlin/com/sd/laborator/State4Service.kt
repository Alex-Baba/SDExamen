package com.sd.laborator

import jakarta.inject.Singleton
import java.util.*

@Singleton
class State4Service {
    fun newState(input: String): String {
        when(input)
        {
            "a" -> return "stare5"
        }
        return "bad-input"
    }
}