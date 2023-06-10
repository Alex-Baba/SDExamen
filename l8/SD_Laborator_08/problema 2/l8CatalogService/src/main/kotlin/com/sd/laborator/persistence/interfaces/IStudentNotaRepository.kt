package com.sd.laborator.persistence.interfaces

import com.sd.laborator.persistence.entities.StudentNotat

interface IStudentNotaRepository {
    fun getIds(): List<Int?>
    fun add(nota: StudentNotat)
    fun createTable()
    fun deleteById(id: Long)
    fun getInterbari(): Long?
    fun getAll(): List<StudentNotat?>
}