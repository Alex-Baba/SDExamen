package com.sd.laborator
import io.micronaut.core.annotation.Introspected
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject

@Introspected
class RequestHandler : MicronautRequestHandler<Request?, Unit>() {

    @Inject
    private lateinit var repo: Repository

    override fun execute(input: Request?){
        if (input != null) {
            input.getId()?.let{
                if(repo.findOne(it).isPresent)
                {
                    val currentCounter = repo.findOne(it).get().getCounter()
                    println("update")
                    repo.update(it, currentCounter + 1)
                } else
                {
                    println("insert")
                    repo.save(it, 0)
                }
            }
        }
        return
    }
}