import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*

class MonitorMicroservice {
    private var monitorSocket: ServerSocket

    private lateinit var messageProcessorSocket: Socket
    private var receiveMessagesObservable: Observable<String>

    private val subscriptions = CompositeDisposable()

    private val bidQueue: Queue<Message> = LinkedList<Message>()
    private val microserviceConnections: MutableList<Socket> = mutableListOf()

    companion object Constants {
        const val MONITOR_PORT = 1800
    }

    init {
        File("log.txt").writeText("")
        monitorSocket = ServerSocket(MONITOR_PORT)

        println("MonitorMicroservice se executa pe portul: ${monitorSocket.localPort}")
        println("Se asteapta operatii de la toata lumeeea...")

        receiveMessagesObservable = Observable.create<String> { emitter ->

            while (true) {
                try {
                    val microserviceConnection = monitorSocket.accept()

                    println("S-a conectat microserviciul de pe portul:" + microserviceConnection.port)
                    microserviceConnections.add(microserviceConnection)

                    // se citeste mesajul de la bidder de pe socketul TCP
                    val bufferReader = BufferedReader(InputStreamReader(microserviceConnection.inputStream))

                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        bufferReader.close()
                        microserviceConnection.close()

                        emitter.onError(Exception("Eroare: Serviciul ${microserviceConnection.port} a fost deconectat."))
                    }

                    // se emite ce s-a citit ca si element in fluxul de mesaje
                    emitter.onNext(receivedMessage)
                } catch (e: SocketTimeoutException) {
                    emitter.onComplete()
                    break
                }
            }
        }
    }

    private fun receiveOperation() {
        val receiveBidsSubscription = receiveMessagesObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                println("Primit in monitor: " + message)
                logMessages(message.toString())
            },
            onComplete = {
                println("Nu mai primim mesaje.")
            },
            onError = { println("Eroare: $it") }
        )
        subscriptions.add(receiveBidsSubscription)
    }

    private fun logMessages(message :String) {
        File("log.txt").appendText(message + '\n')
    }

    fun run() {
        receiveOperation()
    }
}

fun main(args: Array<String>) {
    val monitorMicroservice = MonitorMicroservice()
    monitorMicroservice.run()
}