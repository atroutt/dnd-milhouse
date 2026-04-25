package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.api.SpellImportService
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.data.repository.SpellRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SpellFilter(
    val query: String = "",
    val levelFilter: Int? = null,
    val preparedOnly: Boolean = false,
    val classFilter: String? = null
)

sealed class ImportState {
    object Idle : ImportState()
    data class Loading(val fetched: Int, val total: Int) : ImportState()
    object Done : ImportState()
    data class Error(val message: String) : ImportState()
}

@OptIn(ExperimentalCoroutinesApi::class)
class SpellViewModel(
    private val repository: SpellRepository,
    private val characterId: Long
) : ViewModel() {

    private val _filter = MutableStateFlow(SpellFilter())
    val filter: StateFlow<SpellFilter> = _filter.asStateFlow()

    val spells: StateFlow<List<Spell>> = _filter
        .flatMapLatest { f ->
            repository.getAllForCharacter(characterId).map { list ->
                list.filter { spell ->
                    val matchesQuery = f.query.isEmpty() ||
                        spell.name.contains(f.query, ignoreCase = true) ||
                        spell.school.contains(f.query, ignoreCase = true) ||
                        spell.description.contains(f.query, ignoreCase = true)
                    val matchesLevel = f.levelFilter == null || spell.level == f.levelFilter
                    val matchesPrepared = !f.preparedOnly || spell.isPrepared
                    val matchesClass = f.classFilter == null ||
                        spell.classes.split(",").any { it.trim().equals(f.classFilter, ignoreCase = true) }
                    matchesQuery && matchesLevel && matchesPrepared && matchesClass
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClasses: StateFlow<List<String>> = repository.getAllForCharacter(characterId)
        .map { spells ->
            spells.flatMap { spell ->
                spell.classes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }.distinct().sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSpellCount: StateFlow<Int> = repository.getAllForCharacter(characterId)
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _editSpell = MutableStateFlow<Spell?>(null)
    val editSpell: StateFlow<Spell?> = _editSpell.asStateFlow()

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

    fun setQuery(query: String) = _filter.update { it.copy(query = query) }
    fun setLevelFilter(level: Int?) = _filter.update { it.copy(levelFilter = level) }
    fun setPreparedOnly(only: Boolean) = _filter.update { it.copy(preparedOnly = only) }
    fun setClassFilter(cls: String?) = _filter.update { it.copy(classFilter = cls) }

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

    fun importSrdSpells() {
        viewModelScope.launch {
            _importState.value = ImportState.Loading(0, 0)
            try {
                val spells = SpellImportService().fetchAllSpells(characterId) { fetched, total ->
                    _importState.value = ImportState.Loading(fetched, total)
                }
                repository.insertAll(spells)
                _importState.value = ImportState.Done
            } catch (e: Exception) {
                _importState.value = ImportState.Error(e.message ?: "Import failed")
            }
        }
    }

    fun dismissImportError() { _importState.value = ImportState.Idle }

    companion object {
        fun factory(repository: SpellRepository, characterId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SpellViewModel(repository, characterId) as T
            }
    }
}
