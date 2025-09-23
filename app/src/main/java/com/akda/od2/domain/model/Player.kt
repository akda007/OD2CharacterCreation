package com.akda.od2.domain.model


data class Player(
    val name: String,
    val race: Race,
    val characterClass: CharacterClass,
    val attributes: Map<AttributeType, Attribute>,

    val level: Int = 1,
    val alignment: Alignment,
    val currentXP: Int = 0,

    val maxHitPoints: Int,
    val currentHitPoints: Int,

    val armorClass: Int,
    val movement: Int,
    val infravision: Int,

    val baseAttackBonus: Int,

    val jpDexterity: Int,
    val jpConstitution: Int,
    val jpWisdom: Int
) {
    val meleeAttackBonus: Int
        get() = baseAttackBonus + (attributes[AttributeType.FOR]?.modificator ?: 0)

    val rangedAttackBonus: Int
        get() = baseAttackBonus + (attributes[AttributeType.DES]?.modificator ?: 0)
}