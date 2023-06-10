package com.sd.laborator

import jakarta.inject.Singleton
import java.util.*


@Singleton
class IntersectionService {

    fun getListIntersection(list1: List<Int>,list2: List<Int>): List<Int> {
        val intersectedSet = list1.intersect(list2.toSet())

        val intersectedList = mutableListOf<Int>()
        intersectedList.addAll(intersectedSet)

        return  intersectedList
    }
}