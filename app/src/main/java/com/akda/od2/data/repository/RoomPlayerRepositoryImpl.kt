package com.akda.od2.data.repository

import com.akda.od2.data.local.PlayerDao
import com.akda.od2.data.local.entity.PlayerEntity
import com.akda.od2.domain.model.CharacterClass
import com.akda.od2.domain.model.Player
import com.akda.od2.domain.model.Race
import com.akda.od2.domain.repository.PlayerRepository
import java.util.UUID
import javax.inject.Inject

class RoomPlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao
) : PlayerRepository {

    override suspend fun savePlayer(player: Player) {

        val entity = player.toEntity() // Mapeia do Domínio para Entidade
        playerDao.savePlayer(entity)
        println("[DEBUG] Personagem '${player.name}' salvo no ROOM com o ID: ${player.id}")
    }

    override suspend fun listAllPlayers(): List<Player> {
        return playerDao.listAllPlayers().map { entity ->
            entity.toDomain() // Mapeia da Entidade para o Domínio
        }
    }

    override suspend fun loadPlayer(id: String): Player? {
        return playerDao.loadPlayer(id)?.toDomain()
    }
}


private fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = this.id,
        name = this.name,
        raceName = this.race.name,
        characterClassName = this.characterClass.name,

        alignment = this.alignment,
        attributes = this.attributes,

        level = this.level,
        currentXP = this.currentXP,
        maxHitPoints = this.maxHitPoints,
        currentHitPoints = this.currentHitPoints,
        armorClass = this.armorClass,
        movement = this.movement,
        infravision = this.infravision,
        baseAttackBonus = this.baseAttackBonus,
        jpDexterity = this.jpDexterity,
        jpConstitution = this.jpConstitution,
        jpWisdom = this.jpWisdom
    )
}

private fun PlayerEntity.toDomain(): Player {
    return Player(
        id = this.id,
        name = this.name,
        race = mapRaceFromName(this.raceName),
        characterClass = mapClassFromName(this.characterClassName),

        alignment = this.alignment,
        attributes = this.attributes,

        level = this.level,
        currentXP = this.currentXP,
        maxHitPoints = this.maxHitPoints,
        currentHitPoints = this.currentHitPoints,
        armorClass = this.armorClass,
        movement = this.movement,
        infravision = this.infravision,
        baseAttackBonus = this.baseAttackBonus,
        jpDexterity = this.jpDexterity,
        jpConstitution = this.jpConstitution,
        jpWisdom = this.jpWisdom
    )
}

private fun mapRaceFromName(name: String): Race {
    return when (name) {
        "Humano" -> Race.Human
        "Elfo" -> Race.Elf
        "Anão" -> Race.Dwarf
        "Halfling" -> Race.Halfling
        else -> throw IllegalArgumentException("Raça desconhecida: $name")
    }
}

private fun mapClassFromName(name: String): CharacterClass {
    return when (name) {
        "Guerreiro" -> CharacterClass.Warrior
        "Clérigo" -> CharacterClass.Clerig
        "Ladrão" -> CharacterClass.Thief
        "Mago" -> CharacterClass.Mage
        else -> throw IllegalArgumentException("Classe desconhecida: $name")
    }
}