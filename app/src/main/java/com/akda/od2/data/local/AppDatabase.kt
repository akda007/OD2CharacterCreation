package com.akda.od2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.akda.od2.data.local.converters.DatabaseConverters
import com.akda.od2.data.local.entity.PlayerEntity

@Database(
    entities = [PlayerEntity::class],
    version = 1
)
@TypeConverters(DatabaseConverters::class) // Registra nossos conversores
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
}