package com.akda.od2.domain.application

import com.akda.od2.domain.model.Player
import javax.inject.Inject
import kotlin.random.Random

// Simple model for the enemy
data class Monster(
    val name: String,
    val ac: Int,
    var hp: Int,
    val attackBonus: Int,
    val damageDie: Int // e.g., 6 for 1d6
)

data class BattleLog(
    val message: String,
    val turn: Int
)

class BattleSimulator @Inject constructor() {

    // Returns a log string and updates HP references directly
    fun processTurn(player: Player, monster: Monster, turn: Int): List<String> {
        val logs = mutableListOf<String>()

        // 1. Player Attacks
        if (player.currentHitPoints > 0) {
            val d20 = rollD20()
            val hit = (d20 + player.baseAttackBonus) >= monster.ac

            if (hit) {
                // Simplification: Assuming 1d8 damage for player for now
                val dmg = (1..8).random()
                monster.hp -= dmg
                logs.add("Turn $turn: ${player.name} attacks (Rolled $d20) and hits ${monster.name} for $dmg damage!")
            } else {
                logs.add("Turn $turn: ${player.name} attacks (Rolled $d20) and misses.")
            }
        }

        // 2. Monster Attacks (if still alive)
        if (monster.hp > 0 && player.currentHitPoints > 0) {
            val d20 = rollD20()
            val hit = (d20 + monster.attackBonus) >= player.armorClass

            if (hit) {
                val dmg = (1..monster.damageDie).random()
                player.currentHitPoints -= dmg
                logs.add("Turn $turn: ${monster.name} attacks back (Rolled $d20) and HITS ${player.name} for $dmg damage!")
            } else {
                logs.add("Turn $turn: ${monster.name} attacks (Rolled $d20) and misses.")
            }
        }

        return logs
    }

    fun generateRandomMonster(): Monster {
        val monsters = listOf(
            Monster("Goblin", 12, 4, 1, 4),
            Monster("Orc", 13, 8, 2, 6),
            Monster("Skeleton", 12, 6, 1, 6),
            Monster("Kobold", 11, 3, 0, 4)
        )
        return monsters.random().copy() // Copy to ensure fresh instance
    }

    private fun rollD20(): Int = Random.nextInt(1, 21)
}