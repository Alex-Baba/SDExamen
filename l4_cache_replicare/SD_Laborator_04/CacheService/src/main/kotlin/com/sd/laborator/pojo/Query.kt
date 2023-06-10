package com.sd.laborator.pojo

data class Query(
    var query: String,
    var result: String,
    var timestamp: Long
) {
    override fun toString(): String {
        return "$query~$result~$timestamp"
    }
}