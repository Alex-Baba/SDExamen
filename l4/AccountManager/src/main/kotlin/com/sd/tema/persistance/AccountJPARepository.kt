package com.sd.tema.persistance

import com.sd.tema.pojo.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountJPARepository: JpaRepository<Account, String>{

    //fun findByUserAndPassword(user: String, pass:String) : Account?
    fun findByUser(user: String): List<Account>
}