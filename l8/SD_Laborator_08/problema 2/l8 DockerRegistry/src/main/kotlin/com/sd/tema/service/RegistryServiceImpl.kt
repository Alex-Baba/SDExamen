package com.sd.tema.service

import com.sd.tema.`interface`.RegistryService
import com.sd.tema.persistance.RegistryJPARepository
import com.sd.tema.pojo.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RegistryServiceImpl : RegistryService {

    @Autowired
    private lateinit var repository: RegistryJPARepository

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun addImage(image: Image) {
        if(image.imageId == "")
            image.imageId = getRandomString(12)
        repository.save(image)
    }

    override fun deleteImage(id: String) {
        repository.deleteById(id)
    }

    override fun modifyImage(image: Image, id: String) {
        deleteImage(id)
        image.imageId = id
        addImage(image)
    }

    override fun getImage(id: String): Image? {
        return repository.getById(id)
    }

    override fun getImageByNameAndTag(name: String, tag: String) : Image? {
       return repository.findByNameAndTag(name,tag)
    }

    override fun getImages(): Set<Image> {
        return repository.findAll().toSet()
    }

    override fun getImagesByName(name: String): List<Image> {
        return repository.findByName(name)
    }

}