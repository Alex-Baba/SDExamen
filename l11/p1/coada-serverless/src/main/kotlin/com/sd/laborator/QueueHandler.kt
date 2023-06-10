package com.sd.laborator
import io.micronaut.core.annotation.Introspected
import io.micronaut.function.aws.MicronautRequestHandler
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.netty.DefaultHttpClient
import jakarta.inject.Inject
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Introspected
class QueueHandler() : MicronautRequestHandler<PrimeCheckRequest?, PrimeCheckResponse?>() {

    /*@Inject
    private lateinit var httpClient: HttpClient*/

    private val LOG: Logger = LoggerFactory.getLogger(QueueHandler::class.java)

    override fun execute(input: PrimeCheckRequest?): PrimeCheckResponse? {
        return if (input != null) {
            val primeCheckResponse = PrimeCheckResponse()

            val req = HttpRequest.POST("http://localhost:8080/check-prime", input).
                                        header(HttpHeaders.CONTENT_TYPE, "application/json").
                                        header(HttpHeaders.ACCEPT, "application/json")

            var listaResp : List<Int>? = null
            val httpClient = DefaultHttpClient()
            var flag = false
            httpClient.retrieve(req, PrimeCheckResponse::class.java).subscribe(object: Subscriber<PrimeCheckResponse>{
                override fun onNext(t: PrimeCheckResponse?) {
                    if(t == null)
                    {
                        LOG.error("am primit null...")
                    } else {
                        LOG.info("am primit ${t.getPrimesList()}")
                        primeCheckResponse.setPrimesList(t.getPrimesList())
                    }
                }

                override fun onSubscribe(s: Subscription?) {
                    LOG.info("?")
                    s?.request(1)
                }

                override fun onError(t: Throwable?) {
                    LOG.error("${t?.message}")
                }

                override fun onComplete() {
                    LOG.info("A facut ceva si e complet")
                    flag = true
                }
            })
            while(!flag);
            return primeCheckResponse
        } else {
            null
        }
    }
}