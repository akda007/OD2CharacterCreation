package com.akda.od2.domain.repository

import com.akda.od2.domain.model.Player

interface PlayerRepository {
    suspend fun savePlayer(player: Player)
    suspend fun loadPlayer(id: String): Player?
    suspend fun listAllPlayers(): List<Player>
}