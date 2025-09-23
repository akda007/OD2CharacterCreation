package com.akda.od2.domain.model

enum class AttributeType {FOR, DES, CON, INT, SAB, CAR}

data class Attribute(
    val type: AttributeType,
    val value: Int
) {
    val modificator: Int
        get() = when (value) {
            in 2..3 -> -3
            in 4..5 -> -2
            in 6..8 -> -1
            in 13..14 -> 1
            in 15..16 -> 2
            in 17..18 -> 3
            else -> 0
        }
}