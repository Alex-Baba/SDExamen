package sd.tema.bucatar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import sd.tema.bucatar.components.Bucatar

@SpringBootApplication
class BucatarApp{

    @Bean("bucatar1")
    fun bucatar1() = Bucatar()

    @Bean("bucatar2")
    fun bucatar2() = Bucatar()

    @Bean("bucatar3")
    fun bucatar3() = Bucatar()
}

fun main(args: Array<String>){
    runApplication<BucatarApp>(*args)
}