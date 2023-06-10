package com.sd.laborator

import java.net.Socket

data class User (
    var name: String,
    var host: String,
    var port: Int,
    var conn: Socket? = null
)