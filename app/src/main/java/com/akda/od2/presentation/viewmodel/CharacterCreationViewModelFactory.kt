package com.akda.od2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akda.od2.data.repository.InMemoryPlayerRepositoryImpl
import com.akda.od2.domain.application.CreatePlayer
import com.akda.od2.domain.application.RollAttributes
import com.akda.od2.domain.repository.PlayerRepository

// Objeto singleton para garantir que temos apenas uma instância do repositório
object Dependencies {
    val repository: PlayerRepository = InMemoryPlayerRepositoryImpl()
    val rollAttributes = RollAttributes()
    val createPersonagem = CreatePlayer()
}

class CharacterCreationViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterCreationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterCreationViewModel(
                rollAttributes = Dependencies.rollAttributes,
                createPersonagem = Dependencies.createPersonagem,
                personagemRepository = Dependencies.repository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
