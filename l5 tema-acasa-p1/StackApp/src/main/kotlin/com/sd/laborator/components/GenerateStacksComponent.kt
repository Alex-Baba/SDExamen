package com.sd.laborator.components

import com.sd.laborator.interfaces.ChainComponent
import com.sd.laborator.interfaces.PrimeNumberGenerator
import com.sd.laborator.model.Stack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GenerateStacksComponent: ChainComponent {

    @Autowired
    private lateinit var primeGenerator: PrimeNumberGenerator

    @Autowired
    private lateinit var nextComponent: ComputeComponent

    override fun nextInChain(op: String) {
        var A : Stack? = null
        var B : Stack? = null
        if(op == "regenerate_A" || (op == "compute" && nextComponent.getStackA() == null))
        {
            A = generateStack(20)
            nextComponent.setStackA(A)
        }
        if(op == "regenerate_B" || (op == "compute" && nextComponent.getStackB() == null))
        {
            B = generateStack(20)
            nextComponent.setStackB(B)
        }
        nextComponent.nextInChain(op)
    }

    private fun generateStack(count: Int): Stack? {
        if (count < 1)
            return null
        val X: MutableSet<Int> = mutableSetOf()
        while (X.count() < count)
            X.add(primeGenerator.generatePrimeNumber())
        return Stack(X)
    }

}

