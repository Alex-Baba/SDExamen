package com.sd.laborator

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	val ctx = build()
		.args(*args)
		.packages("com.sd.laborator")
		.start()
}