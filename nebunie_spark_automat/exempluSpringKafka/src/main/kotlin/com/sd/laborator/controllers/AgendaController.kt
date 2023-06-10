package com.sd.laborator.controllers


import com.sd.laborator.interfaces.IAutomateInputQueue
import com.sd.laborator.pojo.AutomateInput
import com.sd.laborator.pojo.AutomateState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AgendaController {
    @Autowired
    private lateinit var _automateInputQueue: IAutomateInputQueue

    private  var _currentState: Int = -1


    @RequestMapping(value = ["/set_state"], method = [RequestMethod.POST])
    fun setState(
        @RequestParam(required = true, name = "value", defaultValue = "") value: Int,
    ): ResponseEntity<Unit> {
        println("We be setting state")

        _currentState = value
        return ResponseEntity(HttpStatus.OK)
    }


    @RequestMapping(value = ["/get_state"], method = [RequestMethod.GET])
    fun getState(): ResponseEntity<AutomateState?>
    {
        println("We be getting state")

        var autState = AutomateState()
        autState.state = _currentState

        val status = if (_currentState == -1) {
            HttpStatus.NOT_FOUND
        } else {
            HttpStatus.OK
        }

        return ResponseEntity(autState, status)
    }

    @RequestMapping(value = ["/push"], method = [RequestMethod.POST])
    fun pushInput(
        @RequestParam(required = true, name = "value", defaultValue = "") value: Boolean,
    ): ResponseEntity<Unit> {

        println("We be pushin")
        val autInput:AutomateInput = AutomateInput()
        autInput.input = value
        _automateInputQueue.pushInput(autInput)

        return ResponseEntity(HttpStatus.OK)
    }

}