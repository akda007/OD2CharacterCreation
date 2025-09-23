package com.akda.od2.domain.application

import com.akda.od2.domain.model.Attribute
import com.akda.od2.domain.model.AttributeType
import com.akda.od2.domain.model.CharacterClass
import com.akda.od2.domain.model.Player
import com.akda.od2.domain.model.Race


class CreatePlayer {

    operator fun invoke(
        name: String,
        race: Race,
        characterClass: CharacterClass,
        attributes: Map<AttributeType, Attribute>,
    ): Player {
        val conModifier = attributes[AttributeType.CON]?.modificator ?: 0
        val desModifier = attributes[AttributeType.DES]?.modificator ?: 0
        val wisModifier = attributes[AttributeType.SAB]?.modificator ?: 0

        val maxHp = characterClass.baseHitPoints + conModifier

        val armorClass = 10 + desModifier

        val baseAttack = characterClass.baseAttackBonus

        val savingThrowBase = characterClass.baseSavingThrow
        val jpDex = savingThrowBase + desModifier
        val jpCon = savingThrowBase + conModifier
        val jpWis = savingThrowBase + wisModifier

        return Player(
            name = name,
            race = race,
            characterClass = characterClass,
            attributes = attributes,
            alignment = race.alignmentTendency,
            maxHitPoints = maxHp,
            currentHitPoints = maxHp,
            armorClass = armorClass,
            baseAttackBonus = baseAttack,
            jpDexterity = jpDex,
            jpConstitution = jpCon,
            jpWisdom = jpWis,
            movement = race.baseMovement,
            infravision = race.infravision
        )

    }
}