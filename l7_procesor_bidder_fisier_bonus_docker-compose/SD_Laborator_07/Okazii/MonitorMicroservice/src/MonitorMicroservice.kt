import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class MonitorMicroservice {

    private val connections: MutableList<Socket> = mutableListOf()
    private var receiveObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val file = File("/logs/logs.txt")

    companion object Constants {
        val MONITOR_HOST = if(System.getenv("HOST") == null) "localhost" else System.getenv("HOST")
        val PORT = if(System.getenv("PORT") == null) 10010 else System.getenv("PORT").toInt()
    }

    init {
        file.writeText("")
        val monitorSocket = ServerSocket(PORT)
        println("Incepe monitorizarea baietii...")

        receiveObservable = Observable.create<String>{emitter ->
            // se asteapta conexiuni
            while (true) {
                try {
                    val connection = monitorSocket.accept()
                    connections.add(connection)
                    thread {
                        println(connection.port)
                        while(true) {
                            try {
                                // se citeste mesajul de la bidder de pe socketul TCP
                                val bufferReader = BufferedReader(InputStreamReader(connection.inputStream))
                                val receivedMessage = bufferReader.readLine()

                                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                                if (receivedMessage == null) {
                                    // deci subscriber-ul respectiv a fost deconectat
                                    bufferReader.close()
                                    connection.close()
                                    break
                                    //emitter.onError(Exception("Eroare: Bidder-ul ${connection.port} a fost deconectat."))
                                }
                                // se emite ce s-a citit ca si element in fluxul de mesaje
                                emitter.onNext(receivedMessage)
                            } catch (se: SocketException) {
                                connection.close()
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    emitter.onError(Exception("Eroare: ${e.message}"))
                    break
                }
            }
        }
    }

    private fun receiveMessages() {
        // se incepe prin a primi ofertele de la bidderi
        val subscription = receiveObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                println(message)
                if(message.sender == "bidder") {
                    file.appendText(message.body)
                    file.appendText("\n")
                }
            },
            onError = { println("Eroare: $it") }
        )
        subscriptions.add(subscription)
    }

    fun run(){
        receiveMessages()
    }
}

fun main(args: Array<String>){
    val monitor = MonitorMicroservice()
    monitor.run()
}