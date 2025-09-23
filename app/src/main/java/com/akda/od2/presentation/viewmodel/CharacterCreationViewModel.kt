package com.akda.od2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akda.od2.domain.application.CreatePlayer
import com.akda.od2.domain.application.RollAttributes
import com.akda.od2.domain.model.Attribute
import com.akda.od2.domain.model.AttributeType
import com.akda.od2.domain.model.CharacterClass
import com.akda.od2.domain.model.Player
import com.akda.od2.domain.model.Race
import com.akda.od2.domain.model.RollMethod
import com.akda.od2.domain.model.RollResult
import com.akda.od2.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Representa todo o estado da tela de criação de personagem.
 * A UI irá observar esta classe para saber o que exibir.
 */
data class CharacterCreationUiState(
    val characterName: String = "",
    val selectedRollMethod: RollMethod = RollMethod.AVENTUREIRO,
    val rollResult: RollResult? = null,
    val selectedRace: Race? = null,
    val selectedClass: CharacterClass? = null,
    val finalAttributes: Map<AttributeType, Attribute>? = null,
    val characterCreated: Boolean = false,
    val finalPersonagem: Player? = null,
    val savedCharacters: List<Player> = emptyList()
)

/**
 * ViewModel que gerencia a lógica da UI e o estado da tela de criação de personagem.
 * Ele sobrevive a mudanças de configuração (como rotação da tela).
 */
class CharacterCreationViewModel(
    private val rollAttributes: RollAttributes,
    private val createPersonagem: CreatePlayer,
    private val personagemRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterCreationUiState())
    val uiState: StateFlow<CharacterCreationUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.update { it.copy(savedCharacters = personagemRepository.listAllPlayers()) }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(characterName = name) }
    }

    fun onRollMethodSelected(method: RollMethod) {
        _uiState.update { it.copy(selectedRollMethod = method) }
    }

    fun rollAttributes() {
        val result = rollAttributes(uiState.value.selectedRollMethod)
        _uiState.update { it.copy(rollResult = result) }

        if (result is RollResult.Fixed) {
            _uiState.update {
                it.copy(finalAttributes = result.attributes.mapValues { (type, value) ->
                    Attribute(type, value)
                })
            }
        }
    }

    fun onAttributesAssigned(attributes: Map<AttributeType, Attribute>) {
        _uiState.update { it.copy(finalAttributes = attributes) }
    }

    fun onRaceSelected(race: Race) {
        _uiState.update { it.copy(selectedRace = race) }
    }

    fun onClassSelected(characterClass: CharacterClass) {
        _uiState.update { it.copy(selectedClass = characterClass) }
    }

    fun finalizeCharacter() {
        val currentState = uiState.value
        // Garante que todos os dados necessários foram preenchidos
        if (currentState.characterName.isNotBlank() &&
            currentState.finalAttributes != null &&
            currentState.selectedRace != null &&
            currentState.selectedClass != null
        ) {

            val personagem = createPersonagem(
                name = currentState.characterName,
                race = currentState.selectedRace,
                characterClass = currentState.selectedClass,
                attributes = currentState.finalAttributes
            )

            viewModelScope.launch {
                personagemRepository.savePlayer(personagem)
                loadCharacters()
                _uiState.update {
                    it.copy(
                        characterCreated = true,
                        finalPersonagem = personagem
                    )
                }
            }
        } else {
            // Aqui você poderia emitir um evento de erro para a UI, como um Toast
            println("[ERROR] Faltam dados para criar o personagem.")
        }
    }

    // Função para reiniciar o processo de criação
    fun resetCreation() {
        _uiState.value = CharacterCreationUiState()
    }
}

