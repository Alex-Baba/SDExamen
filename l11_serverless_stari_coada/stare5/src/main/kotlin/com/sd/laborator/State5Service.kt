package com.sd.laborator

import jakarta.inject.Singleton
import java.io.File
import java.util.*

@Singleton
class State5Service {
    fun newState(input: String) : String {
        val file = File("/home/student/SD/susDE/sub_practice_rez/l11_serverless_stari_coada/log.txt")
        file.appendText("a mers!\n")
        return "stare1"
    }
}