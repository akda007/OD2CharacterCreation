package com.akda.od2.di

import android.app.Application
import androidx.room.Room
import com.akda.od2.data.local.AppDatabase
import com.akda.od2.data.local.PlayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "od2_database.db" // Nome do arquivo do banco
        ).build()
    }

    @Provides
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao {
        return appDatabase.playerDao()
    }
}