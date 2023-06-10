package com.sd.tema.controller

import com.sd.tema.pojo.Account
import com.sd.tema.service.AccountManagerServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.sqlite.SQLiteException

@RestController
class AccountManagerController {

    @Autowired
    private lateinit var service: AccountManagerServiceImpl

    @PostMapping("/account")
    fun createAccount(@RequestBody account: Account) : ResponseEntity<String> {
        try {
            service.addAccount(account)
        }
        catch (sqlExcp : SQLiteException)
        {
            return ResponseEntity("Bad user and pass, try other combination!", HttpStatus.NOT_ACCEPTABLE)
        }
        return ResponseEntity("Created", HttpStatus.CREATED)
    }

    @GetMapping("/account")
    fun logIn(@RequestParam("user") user:String, @RequestParam("pass") pass:String) : ResponseEntity<Boolean>{
        val id = service.logIn(user,pass)
        return ResponseEntity(id, if(id) HttpStatus.OK else HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/account")
    fun deleteAccount(@RequestParam("userKey") userKey: String) : ResponseEntity<Unit>{
        service.deleteAccount(userKey)
        return ResponseEntity(Unit, HttpStatus.OK)
    }

    @GetMapping("/account/profile")
    fun getProfile(@RequestParam("userKey") userKey: String) : ResponseEntity<Account?>{
        val account = service.getAccount(userKey)
        return ResponseEntity(account, if(account != null) HttpStatus.OK else HttpStatus.NOT_FOUND)
    }

    @PutMapping("/account/profile")
    fun updateProfile(@RequestBody newAccount: Account, @RequestParam("userKey") userKey: String) : ResponseEntity<Unit>{
        service.modifyAccount(newAccount, userKey)
        return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }
}