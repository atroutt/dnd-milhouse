package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharacterViewModel(private val repository: CharacterRepository) : ViewModel() {

    val characters: StateFlow<List<DndCharacter>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCharacter(character: DndCharacter) {
        viewModelScope.launch {
            if (character.id == 0L) repository.insert(character)
            else repository.update(character)
        }
    }

    fun deleteCharacter(character: DndCharacter) {
        viewModelScope.launch { repository.delete(character) }
    }

    companion object {
        fun factory(repository: CharacterRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CharacterViewModel(repository) as T
            }
    }
}
