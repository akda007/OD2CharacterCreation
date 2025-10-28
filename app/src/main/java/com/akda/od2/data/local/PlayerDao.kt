package com.akda.od2.data.local

import androidx.room.*
import com.akda.od2.data.local.entity.PlayerEntity

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayer(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    suspend fun loadPlayer(id: String): PlayerEntity?

    @Query("SELECT * FROM players")
    suspend fun listAllPlayers(): List<PlayerEntity>
}