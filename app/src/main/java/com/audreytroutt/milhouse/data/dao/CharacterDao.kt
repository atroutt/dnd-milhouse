package com.audreytroutt.milhouse.data.dao

import androidx.room.*
import com.audreytroutt.milhouse.data.model.DndCharacter
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY name ASC")
    fun getAll(): Flow<List<DndCharacter>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Long): DndCharacter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: DndCharacter): Long

    @Update
    suspend fun update(character: DndCharacter)

    @Delete
    suspend fun delete(character: DndCharacter)
}
