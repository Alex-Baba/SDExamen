package com.sd.laborator.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.JSONPObject
import com.github.fge.jsonpatch.JsonPatch
import com.sd.laborator.interfaces.IAgendaService
import com.sd.laborator.pojo.Person
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest

@RestController
class AgendaController {
    @Autowired
    private lateinit var _agendaService: IAgendaService

    // GRIJA LA "D" DIN SOLID...AR TREBUI UN SERVICIU DE DISCOVERY DAR ESTE UNUL FACUT LA ALT EXERCITIU
    private fun sendToCache(uri: String, res: String) {
        println("posting")
        val url = URL("http://localhost:8080/cache/post")

        val jsonObj = JSONObject()
        jsonObj.put("query",uri)
        jsonObj.put("result",res)
        jsonObj.put("timestamp",System.currentTimeMillis())

        val bytes = jsonObj.toString().toByteArray(StandardCharsets.UTF_8)

        val http = url.openConnection() as HttpURLConnection
        http.doOutput = true
        http.setRequestProperty("Content-Type","application/json")
        //http.setRequestProperty("Content-Length",bytes.size.toString())
        http.outputStream.write(bytes)
        val resp = BufferedReader(InputStreamReader(http.inputStream)).readText()
        http.disconnect()
    }

    private fun askCache(uri: String): String{
        println("posting2")
        val url = URL("http://localhost:8080/cache/get")

        val jsonObj = JSONObject()
        jsonObj.put("query",uri)

        val bytes = jsonObj.toString().toByteArray(StandardCharsets.UTF_8)

        val http = url.openConnection() as HttpURLConnection
        http.doOutput = true
        http.setRequestProperty("Content-Type","application/json")
        //http.setRequestProperty("Content-Length",bytes.size.toString())
        http.outputStream.write(bytes)

        val resp = BufferedReader(InputStreamReader(http.inputStream)).readText()

        http.disconnect()
        return resp
    }

    private fun fromStringToPerson(text: String) : Person {
        val res = text.dropLast(1).drop(7)
        println(res)
        val paramList = res.split(", ").map { it.split("=")[1] }
        val person = Person(paramList[0].toInt(), paramList[1], paramList[2], paramList[3])
        return person
    }

    @RequestMapping(value = ["/person"], method = [RequestMethod.POST])
    fun createPerson(@RequestBody person: Person): ResponseEntity<Unit> {
        _agendaService.createPerson(person)
        return ResponseEntity(Unit, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.GET])
    fun getPerson(@PathVariable id: Int): ResponseEntity<Person?> {
        val cacheRes: String = askCache("/person/$id")
        if(cacheRes == "") {
            val person: Person? = _agendaService.getPerson(id)
            val status = if (person == null) {
                HttpStatus.NOT_FOUND
            } else {
                HttpStatus.OK
            }
            sendToCache("/person/$id", person.toString())

            return ResponseEntity(person, status)
        } else {
            return  ResponseEntity(fromStringToPerson(cacheRes.split("\",\"")[1].split("\":\"")[1]), HttpStatus.OK)
        }
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@PathVariable id: Int, @RequestBody person: Person): ResponseEntity<Unit> {
        _agendaService.getPerson(id)?.let {
            _agendaService.updatePerson(it.id, person)
            return ResponseEntity(Unit, HttpStatus.ACCEPTED)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PATCH])
    fun patchPerson(@PathVariable id: Int, @RequestBody patchOperations: JsonPatch): ResponseEntity<Unit> {
        _agendaService.getPerson(id)?.let {

            // aplicam operatiile de Patch peste obiectul gasit
            val objectMapper = ObjectMapper()
            val patchedPersonJsonNode = patchOperations.apply(objectMapper.valueToTree(it))
            val patchedPerson = objectMapper.treeToValue(patchedPersonJsonNode, Person::class.java)

            // updatam obiectul obtinut dupa operatia de patch
            _agendaService.updatePerson(it.id, patchedPerson)
            return ResponseEntity(Unit, HttpStatus.NO_CONTENT)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.DELETE])
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Unit> {
        if (_agendaService.getPerson(id) != null) {
            _agendaService.deletePerson(id)
            return ResponseEntity(Unit, HttpStatus.OK)
        } else {
            return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/agenda"], method = [RequestMethod.GET])
    fun search(@RequestParam(required = false, name = "lastName", defaultValue = "") lastName: String,
               @RequestParam(required = false, name = "firstName", defaultValue = "") firstName: String,
               @RequestParam(required = false, name = "telephone", defaultValue = "") telephoneNumber: String,
               req: HttpServletRequest):
            ResponseEntity<List<Person>> {
        println(req.queryString)
        val cacheRes: String = askCache("/agenda?${req.queryString}")
        if(cacheRes == "") {
            val personList = _agendaService.searchAgenda(lastName, firstName, telephoneNumber)
            var httpStatus = HttpStatus.OK
            if (personList.isEmpty()) {
                httpStatus = HttpStatus.NO_CONTENT
            }
            val listString = personList.joinToString("^")
            println(listString)
            sendToCache("/agenda?${req.queryString}", listString)
            return ResponseEntity(personList, httpStatus)
        } else {
            println(cacheRes)
            val list = cacheRes.split("\",\"")[1].split("\":\"")[1].split("^")
            val personList = mutableListOf<Person>()
            list.forEach {
                personList.add(fromStringToPerson(it))
            }
            return ResponseEntity(personList,HttpStatus.OK)
        }
    }
}