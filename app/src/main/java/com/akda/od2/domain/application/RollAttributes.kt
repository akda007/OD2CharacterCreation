package com.akda.od2.domain.application

import com.akda.od2.domain.model.AttributeType
import com.akda.od2.domain.model.RollMethod
import com.akda.od2.domain.model.RollResult


class RollAttributes {

    operator fun invoke(method: RollMethod): RollResult {
        return when (method) {
            RollMethod.CLASSICO -> {
                val finalAttributes = AttributeType.entries.associateWith { roll3d6() }
                RollResult.Fixed(finalAttributes)
            }
            // CORRIGIDO AQUI
            RollMethod.AVENTUREIRO -> {
                val rolledValues = List(6) { roll3d6() }
                RollResult.Distributable(rolledValues)
            }
            // CORRIGIDO AQUI
            RollMethod.HEROICO -> {
                val rolledValues = List(6) { roll4d6DropLowest() }
                RollResult.Distributable(rolledValues)
            }
        }
    }

    private fun roll3d6(): Int {
        return (1..3).sumOf { (1..6).random() }
    }

    private fun roll4d6DropLowest(): Int {
        val roll = List(4) { (1..6).random() }.sorted()

        return roll.drop(1).sum()
    }
}