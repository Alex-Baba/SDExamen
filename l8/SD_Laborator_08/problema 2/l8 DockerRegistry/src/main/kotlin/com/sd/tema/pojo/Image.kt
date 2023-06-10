package com.sd.tema.pojo

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Image(
    @Id
    var imageId: String = "",
    var name: String = "",
    var tag: String = "",
    var space: String = ""
)
