package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.STANDARD_ACTIONS
import com.audreytroutt.milhouse.data.model.SpeciesTrait
import com.audreytroutt.milhouse.data.model.classFeatures
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import com.audreytroutt.milhouse.data.repository.ActionRepository
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import com.audreytroutt.milhouse.data.repository.NoteRepository
import com.audreytroutt.milhouse.data.repository.SpellRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharacterViewModel(
    private val repository: CharacterRepository,
    private val spellRepository: SpellRepository,
    private val abilityRepository: AbilityRepository,
    private val actionRepository: ActionRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    val characters: StateFlow<List<DndCharacter>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCharacter(character: DndCharacter, speciesTraits: List<SpeciesTrait> = emptyList()) {
        viewModelScope.launch {
            if (character.id == 0L) {
                val newId = repository.insert(character)
                val featureAbilities = buildList {
                    classFeatures(character.characterClass).forEach { cf ->
                        add(Ability(
                            characterId = newId,
                            name = cf.name,
                            category = "Class Feature",
                            description = cf.description
                        ))
                    }
                    speciesTraits.forEach { trait ->
                        add(Ability(
                            characterId = newId,
                            name = trait.name,
                            category = "Species Trait",
                            description = trait.description,
                            isPassive = true
                        ))
                    }
                }
                if (featureAbilities.isNotEmpty()) abilityRepository.insertAll(featureAbilities)
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
        viewModelScope.launch {
            repository.delete(character, spellRepository, abilityRepository, actionRepository, noteRepository)
        }
    }
}
