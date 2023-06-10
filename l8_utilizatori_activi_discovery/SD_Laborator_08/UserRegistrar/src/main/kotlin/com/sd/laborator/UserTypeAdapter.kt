package com.sd.laborator

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter


class UserTypeAdapter : TypeAdapter<User>() {
    override fun write(p0: JsonWriter?, p1: User?) {
        if(p0 != null && p1 != null) {
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

    override fun read(p0: JsonReader?): User {
        val usr: User = User("","",-1)
        if(p0 != null) {
            p0.beginObject()
            var fieldname: String? = null

            while (p0.hasNext()) {
                var token: JsonToken = p0.peek()
                if (token == JsonToken.NAME) {
                    //get the current token
                    fieldname = p0.nextName()
                }
                if ("name" == fieldname) {
                    //move to next token
                    token = p0.peek()
                    usr.name = p0.nextString()
                }
                if ("host" == fieldname) {
                    //move to next token
                    token = p0.peek()
                    usr.host = p0.nextString()
                }
                if ("port" == fieldname) {
                    //move to next token
                    token = p0.peek()
                    usr.port = p0.nextInt()
                }
            }
            p0.endObject()
        }
        return usr
    }
}