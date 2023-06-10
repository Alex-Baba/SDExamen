package com.sd.tema.service

import com.sd.tema.`interface`.AccountManagerService
import com.sd.tema.persistance.AccountJPARepository
import com.sd.tema.pojo.Account
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors

@Service
class AccountManagerServiceImpl : AccountManagerService {

    @Autowired
    private lateinit var repository: AccountJPARepository

    override fun addAccount(account: Account){

        account.lastName = URL("http://localhost:8002/encrypt/${account.lastName}").readText()
        account.firstName = URL("http://localhost:8002/encrypt/${account.firstName}").readText()
        account.birthDate = URL("http://localhost:8002/encrypt/${account.birthDate}").readText()
        account.password = URL("http://localhost:8002/hash/${account.user + account.password}").readText()
        account.userKey = URL("http://localhost:8002/key?user=${account.user}").readText()

        repository.save(account)
    }

    override fun logIn(user: String, pass: String): Boolean {
        val accounts: List<Account> = repository.findByUser(user)
        accounts.forEach {
            val isGood = URL("http://localhost:8002/matches/${user+pass}/${it.password}").readText()
            println(isGood)
            if(isGood.toBooleanStrict())
            {
                return true
            }
        }
        return false
    }

    override fun deleteAccount(userKey: String) {
        repository.deleteById(userKey)
    }

    override fun modifyAccount(newAccount: Account, userKey: String) {
        newAccount.userKey = userKey

        repository.deleteById(userKey)
        addAccount(newAccount)
    }

    private fun postToUrl(url: String, data: String) : String {
        val nextServiceURL = URL(url)

        val http: HttpURLConnection = nextServiceURL.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.setRequestProperty("accept", "application/json");
        http.doOutput = true
        http.setRequestProperty("Content-Type", "application/json")

        val out: ByteArray = data.toByteArray(StandardCharsets.UTF_8)

        val stream = http.outputStream
        stream.write(out)

        val bufferedReader =  BufferedReader(InputStreamReader(http.inputStream))
        val rawResponse =  bufferedReader.lines().collect(Collectors.joining())
        http.disconnect()

        return rawResponse
    }

    override fun getAccount(userKey: String): Account? {
        if(repository.findById(userKey).isEmpty)
            return null

        val account : Account = repository.findById(userKey).get()

        println(account)
        account.lastName = postToUrl("http://localhost:8002/decrypt", account.lastName)
        account.firstName = postToUrl("http://localhost:8002/decrypt", account.firstName)
        account.birthDate = postToUrl("http://localhost:8002/decrypt", account.birthDate)

        return account
    }

}