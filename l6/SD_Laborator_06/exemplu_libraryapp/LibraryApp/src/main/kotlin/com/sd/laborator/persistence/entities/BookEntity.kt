package com.sd.laborator.persistence.entities

data class BookEntity(
    var id: Int,
    var author: String,
    var text: String,
    var name: String,
    var publisher: String
)