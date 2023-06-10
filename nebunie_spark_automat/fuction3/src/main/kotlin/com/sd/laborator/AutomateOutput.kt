package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class AutomateOutput {
    private var currentState: Int = -1  // 0 to 4

    fun getCurrentState(): Int {
        return currentState.toInt()
    }

    fun setCurrentState(currentState: Int) {
        this.currentState = currentState
    }
}

