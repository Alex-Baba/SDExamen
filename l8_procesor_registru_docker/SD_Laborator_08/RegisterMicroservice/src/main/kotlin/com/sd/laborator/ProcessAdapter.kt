package com.sd.laborator

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

//chestia asta e pt ca probabil m-am complicat si acu trebuie sa fac nebunii cu json...
class ProcessAdapter : TypeAdapter<Process>() {

    override fun write(p0: JsonWriter?, p1: Process?) {
        if (p0 != null && p1 != null) {
            p0.beginObject()
            p0.name("name")
            p0.value(p1.name)
            p0.name("host")
            p0.value(p1.host)
            p0.name("port")
            p0.value(p1.port)
            p0.endObject()
        }
    }

    override fun read(p0: JsonReader?): Process {
        var fieldname: String = ""
        var process = com.sd.laborator.Process("","",0)
        if(p0 != null){
            p0.beginObject()
            while (p0.hasNext()) {
                var token: JsonToken = p0.peek()
                if (token == JsonToken.NAME) {
                    //get the current token
                    fieldname = p0.nextName()
                }
                if ("name" == fieldname) {
                    //move to next token
                    token = p0.peek()
                    process.name = p0.nextString()
                }
                if ("host" == fieldname) {
                    //move to next token
                    token = p0.peek()
                    process.host = p0.nextString()
                }
                if("port" == fieldname) {
                    token = p0.peek()
                    process.port = p0.nextInt()
                }
            }
            p0.endObject()

        }
        return process
    }
}