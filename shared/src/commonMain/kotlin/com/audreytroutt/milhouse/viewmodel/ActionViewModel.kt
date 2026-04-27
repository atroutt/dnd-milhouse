package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.repository.ActionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ActionViewModel(
    private val repository: ActionRepository,
    private val characterId: Long
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _typeFilter = MutableStateFlow<String?>(null)
    val typeFilter: StateFlow<String?> = _typeFilter.asStateFlow()

    val actions: StateFlow<List<DndAction>> = combine(_query, _typeFilter) { q, type -> q to type }
        .flatMapLatest { (q, type) ->
            repository.getAllForCharacter(characterId).map { list ->
                list.filter { action ->
                    val matchesQuery = q.isEmpty() ||
                        action.name.contains(q, ignoreCase = true) ||
                        action.description.contains(q, ignoreCase = true)
                    val matchesType = type == null || action.actionType == type
                    matchesQuery && matchesType
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editAction = MutableStateFlow<DndAction?>(null)
    val editAction: StateFlow<DndAction?> = _editAction.asStateFlow()

    fun setQuery(query: String) { _query.value = query }
    fun setTypeFilter(type: String?) { _typeFilter.value = type }

    fun loadAction(id: Long) {
        viewModelScope.launch { _editAction.value = repository.getById(id) }
    }

    fun clearEditAction() { _editAction.value = null }

    fun saveAction(action: DndAction) {
        viewModelScope.launch {
            if (action.id == 0L) repository.insert(action) else repository.update(action)
        }
    }

    fun deleteAction(action: DndAction) {
        viewModelScope.launch { repository.delete(action) }
    }
}
