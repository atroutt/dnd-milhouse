package com.audreytroutt.milhouse.data.dao

import androidx.room.*
import com.audreytroutt.milhouse.data.model.DndAction
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions WHERE characterId = :characterId ORDER BY actionType ASC, name ASC")
    fun getAllForCharacter(characterId: Long): Flow<List<DndAction>>

    @Query("SELECT * FROM actions WHERE id = :id")
    suspend fun getById(id: Long): DndAction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(action: DndAction): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(actions: List<DndAction>)

    @Update
    suspend fun update(action: DndAction)

    @Delete
    suspend fun delete(action: DndAction)

    @Query("DELETE FROM actions WHERE characterId = :characterId")
    suspend fun deleteAllForCharacter(characterId: Long)
}
