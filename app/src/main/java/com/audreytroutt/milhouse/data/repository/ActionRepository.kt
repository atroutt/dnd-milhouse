package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.ActionDao
import com.audreytroutt.milhouse.data.model.DndAction
import kotlinx.coroutines.flow.Flow

class ActionRepository(private val dao: ActionDao) {
    fun getAll(): Flow<List<DndAction>> = dao.getAll()
    suspend fun getById(id: Long): DndAction? = dao.getById(id)
    suspend fun insert(action: DndAction): Long = dao.insert(action)
    suspend fun update(action: DndAction) = dao.update(action)
    suspend fun delete(action: DndAction) = dao.delete(action)
}
