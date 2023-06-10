package com.sd.tema.controller

import com.sd.tema.`interface`.RegistryService
import com.sd.tema.pojo.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class RegistryController {

    @Autowired
    private lateinit var service: RegistryService

    @PostMapping("/registry")
    fun addImage(@RequestBody image: Image){
        service.addImage(image)
    }

    @GetMapping("/registry")
    fun getAllImages() : Set<Image>{
        return service.getImages()
    }

    @DeleteMapping("/registry/{id}")
    fun deleteImage(@PathVariable("id") id:String){
        service.deleteImage(id)
    }

    @GetMapping("/registry/{id}")
    fun getImageById(@PathVariable("id") id:String) : Image?{
        return service.getImage(id)
    }

    @PutMapping("/registry/{id}")
    fun updateImage(@PathVariable("id") id:String, @RequestBody image: Image) : Image?{
        service.modifyImage(image,id)
        return service.getImage(id)
    }

    @GetMapping("/registry/{name}/{tag}")
    fun getImageByNameAndTag(@PathVariable("name") name:String, @PathVariable("tag") tag:String) : Image? {
        return service.getImageByNameAndTag(name,tag)
    }

    @GetMapping("/registry/name/{name}")
    fun getImageByName(@PathVariable("name") name:String) : List<Image> {
        return service.getImagesByName(name)
    }
}