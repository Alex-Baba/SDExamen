package com.sd.laborator.components

import com.sd.laborator.interfaces.CartesianProductOperation
import com.sd.laborator.interfaces.ChainComponent
import com.sd.laborator.interfaces.UnionOperation
import com.sd.laborator.model.Stack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ComputeComponent: ChainComponent {

    private var A: Stack? = null
    private var B: Stack? = null

    @Autowired
    private lateinit var cartesianProductOperation: CartesianProductOperation

    @Autowired
    private lateinit var unionOperation: UnionOperation

    @Autowired
    private lateinit var nextComponent: ProducerComponent

    private fun computeExpression(A: Stack, B: Stack): Set<Pair<Int, Int>>? {
        if (A.data.count() == B.data.count()) {
            // (A x B) U (B x B)
            val partialResult1 = cartesianProductOperation.executeOperation(A.data, B.data)
            val partialResult2 = cartesianProductOperation.executeOperation(B.data, B.data)
            val result = unionOperation.executeOperation(partialResult1, partialResult2)
            return result
        }
        return null
    }

    override fun nextInChain(op: String) {
        var message : String = op.replace("regenerate_","") + "~Error"
        if(op == "compute")
        {
            val result = A?.let { B?.let { it1 -> computeExpression(it, it1) } }
            result?.let { message =  "compute~" + "{\"A\": \"" + A?.data.toString() +"\", \"B\": \"" + B?.data.toString() + "\", \"result\": \"" + result.toString() + "\"}"}
        }
        if(op.contains("regenerate"))
        {
            when(op){
                "regenerate_A" -> message = "A~" + A?.data.toString()
                "regenerate_B" -> message = "B~" + B?.data.toString()
            }
        }
        nextComponent.nextInChain(message)
    }

    fun setStackA(a: Stack?) {
        A = a
    }

    fun getStackA(): Stack? {
        return A
    }

    fun getStackB(): Stack? {
        return B
    }

    fun setStackB(b: Stack?) {
        B = b
    }

}