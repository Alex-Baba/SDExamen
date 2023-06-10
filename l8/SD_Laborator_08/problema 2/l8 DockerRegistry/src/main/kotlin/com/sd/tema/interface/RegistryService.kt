package com.sd.tema.`interface`

import com.sd.tema.pojo.Image

interface RegistryService {
    fun addImage(image: Image)
    fun deleteImage(id: String)
    fun modifyImage(image: Image, id: String)
    fun getImage(id: String): Image?
    fun getImageByNameAndTag(name: String, tag: String): Image?
    fun getImages(): Set<Image>
    fun getImagesByName(name: String): List<Image>
}