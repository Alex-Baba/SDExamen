package com.sd.tema.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sd.tema.`interface`.BlacklistInterface
import org.springframework.stereotype.Service
import java.io.File

@Service
class BlacklistService : BlacklistInterface {
    override fun isBlacklisted(locationFrom: String, locationAccessed: String): String {
        //val bufferedReader: BufferedReader = File("blacklist.json").bufferedReader()
        //val inputString = bufferedReader.use { it.readText() }
        val objectMaper = jacksonObjectMapper()
        val map: HashMap<String, List<String>> = objectMaper.readValue(File("/home/student/SD/repo-classroom/l3/tema/Orchestration/tema_acasa/BlacklistService/blacklist.json"))
        map.forEach{it ->
            println(it.key)
            println(it.value)
            if(it.key == locationAccessed && it.value.contains(locationFrom))
                return "Blocked"
        }
        return "Allowed"
    }


}