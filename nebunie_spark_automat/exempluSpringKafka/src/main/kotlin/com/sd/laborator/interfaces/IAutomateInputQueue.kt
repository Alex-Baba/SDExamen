package com.sd.laborator.interfaces

import com.sd.laborator.pojo.AutomateInput

interface IAutomateInputQueue {
    fun pushInput(autInput: AutomateInput)
}