package com.sd.laborator
import io.micronaut.core.annotation.Introspected

@Introspected
class Request {
    private var id: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(idd: String?) {
        this.id = idd
    }
}