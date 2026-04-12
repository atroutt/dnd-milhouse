package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.data.repository.SpellRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SpellFilter(
    val query: String = "",
    val levelFilter: Int? = null, // null = all, 0 = cantrips
    val preparedOnly: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class SpellViewModel(private val repository: SpellRepository) : ViewModel() {

    private val _filter = MutableStateFlow(SpellFilter())
    val filter: StateFlow<SpellFilter> = _filter.asStateFlow()

    val spells: StateFlow<List<Spell>> = _filter
        .flatMapLatest { f ->
            repository.getAll().map { list ->
                list.filter { spell ->
                    val matchesQuery = f.query.isEmpty() ||
                        spell.name.contains(f.query, ignoreCase = true) ||
                        spell.school.contains(f.query, ignoreCase = true) ||
                        spell.description.contains(f.query, ignoreCase = true)
                    val matchesLevel = f.levelFilter == null || spell.level == f.levelFilter
                    val matchesPrepared = !f.preparedOnly || spell.isPrepared
                    matchesQuery && matchesLevel && matchesPrepared
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editSpell = MutableStateFlow<Spell?>(null)
    val editSpell: StateFlow<Spell?> = _editSpell.asStateFlow()

    fun setQuery(query: String) = _filter.update { it.copy(query = query) }
    fun setLevelFilter(level: Int?) = _filter.update { it.copy(levelFilter = level) }
    fun setPreparedOnly(only: Boolean) = _filter.update { it.copy(preparedOnly = only) }

    fun loadSpell(id: Long) {
        viewModelScope.launch { _editSpell.value = repository.getById(id) }
    }

    fun clearEditSpell() { _editSpell.value = null }

    fun saveSpell(spell: Spell) {
        viewModelScope.launch {
            if (spell.id == 0L) repository.insert(spell) else repository.update(spell)
        }
    }

    fun deleteSpell(spell: Spell) {
        viewModelScope.launch { repository.delete(spell) }
    }

    fun togglePrepared(spell: Spell) {
        viewModelScope.launch { repository.update(spell.copy(isPrepared = !spell.isPrepared)) }
    }

    companion object {
        fun factory(repository: SpellRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SpellViewModel(repository) as T
            }
    }
}
