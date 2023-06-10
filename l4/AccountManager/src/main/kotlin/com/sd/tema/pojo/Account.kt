package com.sd.tema.pojo

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Account(
    @Id
    var userKey: String = "",
    var user: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var birthDate: String = "",
    var email: String = ""
)
