package com.sd.tema.`interface`

import com.sd.tema.pojo.Account
import java.util.*

interface AccountManagerService {
    fun addAccount(account: Account)
    fun logIn(user: String, pass: String): Boolean
    fun deleteAccount(userKey: String)
    fun modifyAccount(newAccount: Account, userKey: String)
    fun getAccount(userKey: String): Account?
}