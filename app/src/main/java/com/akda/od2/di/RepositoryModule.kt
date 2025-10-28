package com.akda.od2.di

import com.akda.od2.data.repository.RoomPlayerRepositoryImpl
import com.akda.od2.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        roomPlayerRepositoryImpl: RoomPlayerRepositoryImpl
    ): PlayerRepository
}