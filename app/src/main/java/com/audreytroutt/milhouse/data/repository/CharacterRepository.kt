package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.CharacterDao
import com.audreytroutt.milhouse.data.model.DndCharacter
import kotlinx.coroutines.flow.Flow

class CharacterRepository(private val dao: CharacterDao) {
    fun getAll(): Flow<List<DndCharacter>> = dao.getAll()
    suspend fun getById(id: Long): DndCharacter? = dao.getById(id)
    suspend fun insert(character: DndCharacter): Long = dao.insert(character)
    suspend fun update(character: DndCharacter) = dao.update(character)
    suspend fun delete(character: DndCharacter) = dao.delete(character)
}
