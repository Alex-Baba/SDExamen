package sd.tema.bucatar.components

import sd.tema.bucatar.model.TipComanda
import kotlin.random.Random

class Bucatar {

    private val id: String
    private var occupied : Boolean = false

    init {
        id = "Bucatar" + getRandomString(6)
    }

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun isOccupied() = occupied

    private fun generateTimeToCook(comanda: TipComanda) = comanda.ordinal * 3000L + Random.nextInt(500)

    suspend fun cook(comanda: TipComanda) : String {
        occupied = true
        Thread.sleep(generateTimeToCook(comanda))
        occupied = false
        return "bucatar.order.$comanda"
    }
}