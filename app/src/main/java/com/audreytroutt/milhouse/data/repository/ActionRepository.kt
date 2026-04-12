package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.ActionDao
import com.audreytroutt.milhouse.data.model.DndAction
import kotlinx.coroutines.flow.Flow

class ActionRepository(private val dao: ActionDao) {
    fun getAllForCharacter(characterId: Long): Flow<List<DndAction>> = dao.getAllForCharacter(characterId)
    suspend fun getById(id: Long): DndAction? = dao.getById(id)
    suspend fun insert(action: DndAction): Long = dao.insert(action)
    suspend fun insertAll(actions: List<DndAction>) = dao.insertAll(actions)
    suspend fun update(action: DndAction) = dao.update(action)
    suspend fun delete(action: DndAction) = dao.delete(action)
}
