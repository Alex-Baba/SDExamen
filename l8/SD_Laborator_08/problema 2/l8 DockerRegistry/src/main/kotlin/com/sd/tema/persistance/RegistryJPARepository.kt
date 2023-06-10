package com.sd.tema.persistance

import com.sd.tema.pojo.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistryJPARepository: JpaRepository<Image, String>{

    fun findByNameAndTag(name: String, tag:String): Image?
    fun findByName(name: String): List<Image>
}