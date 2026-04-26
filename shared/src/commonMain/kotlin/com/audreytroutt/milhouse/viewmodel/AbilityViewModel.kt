package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AbilityViewModel(
    private val repository: AbilityRepository,
    private val characterId: Long
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _categoryFilter = MutableStateFlow<String?>(null)
    val categoryFilter: StateFlow<String?> = _categoryFilter.asStateFlow()

    val abilities: StateFlow<List<Ability>> = combine(_query, _categoryFilter) { q, cat -> q to cat }
        .flatMapLatest { (q, cat) ->
            repository.getAllForCharacter(characterId).map { list ->
                list.filter { ability ->
                    val matchesQuery = q.isEmpty() ||
                        ability.name.contains(q, ignoreCase = true) ||
                        ability.description.contains(q, ignoreCase = true)
                    val matchesCategory = cat == null || ability.category == cat
                    matchesQuery && matchesCategory
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editAbility = MutableStateFlow<Ability?>(null)
    val editAbility: StateFlow<Ability?> = _editAbility.asStateFlow()

    fun setQuery(query: String) { _query.value = query }
    fun setCategoryFilter(category: String?) { _categoryFilter.value = category }

    fun loadAbility(id: Long) {
        viewModelScope.launch { _editAbility.value = repository.getById(id) }
    }

    fun clearEditAbility() { _editAbility.value = null }

    fun saveAbility(ability: Ability) {
        viewModelScope.launch {
            if (ability.id == 0L) repository.insert(ability) else repository.update(ability)
        }
    }

    fun deleteAbility(ability: Ability) {
        viewModelScope.launch { repository.delete(ability) }
    }
}
