package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class IntersectionResponse {
	private var message: String? = null
	private var intersection: List<Int>? = null

	fun getIntersection(): List<Int>? {
		return intersection
	}

	fun setIntersection(primes: List<Int>?) {
		this.intersection = primes
	}

	fun getMessage(): String? {
		return message
	}

	fun setMessage(message: String?) {
		this.message = message
	}
}


