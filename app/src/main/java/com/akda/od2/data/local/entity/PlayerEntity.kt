package com.akda.od2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akda.od2.domain.model.Alignment
import com.akda.od2.domain.model.Attribute
import com.akda.od2.domain.model.AttributeType

@Entity(tableName = "players")
data class
PlayerEntity(
    @PrimaryKey val id: String,

    val name: String,

    val raceName: String,
    val characterClassName: String,

    val alignment: Alignment,

    val attributes: Map<AttributeType, Attribute>,

    val level: Int,
    val currentXP: Int,
    val maxHitPoints: Int,
    val currentHitPoints: Int,
    val armorClass: Int,
    val movement: Int,
    val infravision: Int,
    val baseAttackBonus: Int,
    val jpDexterity: Int,
    val jpConstitution: Int,
    val jpWisdom: Int
)