package com.sd.laborator

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity(value = "button_counter")
class Entity {

    @Id
    private lateinit var id_button: String
    private var counter: Int = 0

    fun getId_button() : String {
        return id_button
    }

    fun setCounter(counter: Int) {
        this.counter = counter
    }

    fun setId_button(id : String) {
        this.id_button = id
    }

    fun getCounter() : Int {
        return counter
    }
}