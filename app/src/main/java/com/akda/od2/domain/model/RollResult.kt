package com.akda.od2.domain.model


sealed class RollResult {

    data class Distributable(val valores: List<Int>): RollResult()
    data class Fixed(val attributes: Map<AttributeType, Int>): RollResult()
}