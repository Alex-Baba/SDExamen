package com.sd.laborator

import com.sd.laborator.persistence.CatalogService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.sd.laborator.persistence.repositories"])
open class MainApp{
    @Bean
    open fun catalogService(): CatalogService = CatalogService()
}

fun main(args: Array<String>){
    val app = runApplication<MainApp>(*args)
    val catalog: CatalogService = app.getBean("catalogService") as CatalogService
    catalog.run()

}




