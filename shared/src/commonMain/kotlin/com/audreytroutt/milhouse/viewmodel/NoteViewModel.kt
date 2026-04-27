package com.audreytroutt.milhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.data.model.currentTimeMillis
import com.audreytroutt.milhouse.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModel(
    private val repository: NoteRepository,
    private val characterId: Long
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _tagFilter = MutableStateFlow<String?>(null)
    val tagFilter: StateFlow<String?> = _tagFilter.asStateFlow()

    val notes: StateFlow<List<Note>> = combine(_query, _tagFilter) { q, tag -> q to tag }
        .flatMapLatest { (q, tag) ->
            repository.getAllForCharacter(characterId).map { list ->
                list.filter { note ->
                    val matchesQuery = q.isEmpty() ||
                        note.title.contains(q, ignoreCase = true) ||
                        note.content.contains(q, ignoreCase = true) ||
                        note.tags.contains(q, ignoreCase = true)
                    val matchesTag = tag == null || note.tagList().any { it.equals(tag, ignoreCase = true) }
                    matchesQuery && matchesTag
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<String>> = repository.getAllForCharacter(characterId)
        .map { notes -> notes.flatMap { it.tagList() }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editNote = MutableStateFlow<Note?>(null)
    val editNote: StateFlow<Note?> = _editNote.asStateFlow()

    fun setQuery(query: String) { _query.value = query }
    fun setTagFilter(tag: String?) { _tagFilter.value = tag }

    fun loadNote(id: Long) {
        viewModelScope.launch { _editNote.value = repository.getById(id) }
    }

    fun clearEditNote() { _editNote.value = null }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            val now = currentTimeMillis()
            if (note.id == 0L) {
                repository.insert(note.copy(createdAt = now, updatedAt = now))
            } else {
                repository.update(note.copy(updatedAt = now))
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.delete(note) }
    }
}
