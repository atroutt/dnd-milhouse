package com.audreytroutt.milhouse.data.dao

import androidx.room.*
import com.audreytroutt.milhouse.data.model.DndAction
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions ORDER BY actionType ASC, name ASC")
    fun getAll(): Flow<List<DndAction>>

    @Query("SELECT * FROM actions WHERE id = :id")
    suspend fun getById(id: Long): DndAction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(action: DndAction): Long

    @Update
    suspend fun update(action: DndAction)

    @Delete
    suspend fun delete(action: DndAction)
}
