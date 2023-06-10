package com.sd.tema

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["com.sd.tema.pojo"])
@EnableJpaRepositories(basePackages = ["com.sd.tema.persistance"])
class RegistryApp

fun main(args: Array<String>) {
    runApplication<RegistryApp>(*args)
}
