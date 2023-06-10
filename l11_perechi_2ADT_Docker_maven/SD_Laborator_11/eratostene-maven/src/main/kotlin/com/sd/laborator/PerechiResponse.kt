package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class PerechiResponse(private var listaPerechi: List<Pair<Int,Int>>) {

	fun getList(): List<Pair<Int,Int>> {
		return listaPerechi
	}

	fun setList(l: List<Pair<Int,Int>>) {
		this.listaPerechi = l
	}
}


