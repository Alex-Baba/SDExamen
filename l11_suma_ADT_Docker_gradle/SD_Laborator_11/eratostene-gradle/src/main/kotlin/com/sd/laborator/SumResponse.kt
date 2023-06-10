package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class SumResponse(private var sum: Long) {

	fun getSum(): Long {
		return sum
	}
	fun setSum(s: Long) {
		this.sum = s
	}
}


