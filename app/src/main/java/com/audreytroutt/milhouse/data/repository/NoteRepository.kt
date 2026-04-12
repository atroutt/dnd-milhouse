package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.NoteDao
import com.audreytroutt.milhouse.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun getAllForCharacter(characterId: Long): Flow<List<Note>> = dao.getAllForCharacter(characterId)
    suspend fun getById(id: Long): Note? = dao.getById(id)
    suspend fun insert(note: Note): Long = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)
}
