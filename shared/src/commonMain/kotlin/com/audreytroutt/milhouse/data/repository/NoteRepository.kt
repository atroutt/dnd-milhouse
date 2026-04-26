package com.audreytroutt.milhouse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.db.MilhouseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val database: MilhouseDatabase) {

    fun getAllForCharacter(characterId: Long): Flow<List<Note>> =
        database.noteQueries.getAllForCharacter(characterId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toModel() } }

    suspend fun getById(id: Long): Note? =
        database.noteQueries.getById(id).executeAsOneOrNull()?.toModel()

    suspend fun insert(note: Note): Long {
        database.noteQueries.insertNew(
            characterId = note.characterId,
            title = note.title,
            content = note.content,
            tags = note.tags,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
        return database.noteQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun update(note: Note) {
        database.noteQueries.update(
            characterId = note.characterId,
            title = note.title,
            content = note.content,
            tags = note.tags,
            updatedAt = note.updatedAt,
            id = note.id
        )
    }

    suspend fun delete(note: Note) {
        database.noteQueries.delete(note.id)
    }

    suspend fun deleteAllForCharacter(characterId: Long) {
        database.noteQueries.deleteAllForCharacter(characterId)
    }

    private fun com.audreytroutt.milhouse.db.Notes.toModel() = Note(
        id = id,
        characterId = characterId,
        title = title,
        content = content,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
