package com.akda.od2.data.repository

import com.akda.od2.domain.model.Player
import com.akda.od2.domain.repository.PlayerRepository
import java.util.UUID

class InMemoryPlayerRepositoryImpl : PlayerRepository {
    private val savedPlayers = mutableMapOf<String, Player>()

    override suspend fun savePlayer(player: Player) {
        val id = UUID.randomUUID().toString()

        savedPlayers[id] = player
        println("[DEBUG] Personagem '${player.name}' salvo na memória com o ID: $id")
    }

    override suspend fun listAllPlayers(): List<Player> {
        return savedPlayers.values.toList()
    }

    override suspend fun loadPlayer(id: String): Player? {
        return savedPlayers[id]
    }
}