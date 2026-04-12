package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.STANDARD_ACTIONS
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import com.audreytroutt.milhouse.data.repository.ActionRepository
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharacterViewModel(
    private val repository: CharacterRepository,
    private val abilityRepository: AbilityRepository,
    private val actionRepository: ActionRepository
) : ViewModel() {

    val characters: StateFlow<List<DndCharacter>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCharacter(character: DndCharacter, speciesTraits: List<String> = emptyList()) {
        viewModelScope.launch {
            if (character.id == 0L) {
                val newId = repository.insert(character)
                if (speciesTraits.isNotEmpty()) {
                    abilityRepository.insertAll(speciesTraits.map { traitName ->
                        Ability(
                            characterId = newId,
                            name = traitName,
                            category = "Species Trait",
                            description = "",
                            isPassive = true
                        )
                    })
                }
                actionRepository.insertAll(STANDARD_ACTIONS.map { sa ->
                    DndAction(
                        characterId = newId,
                        name = sa.name,
                        actionType = sa.type,
                        description = sa.description
                    )
                })
            } else {
                repository.update(character)
            }
        }
    }

    fun deleteCharacter(character: DndCharacter) {
        viewModelScope.launch { repository.delete(character) }
    }

    companion object {
        fun factory(
            repository: CharacterRepository,
            abilityRepository: AbilityRepository,
            actionRepository: ActionRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CharacterViewModel(repository, abilityRepository, actionRepository) as T
            }
    }
}
