package com.audreytroutt.milhouse.data.dao

import androidx.room.*
import com.audreytroutt.milhouse.data.model.Ability
import kotlinx.coroutines.flow.Flow

@Dao
interface AbilityDao {
    @Query("SELECT * FROM abilities WHERE characterId = :characterId ORDER BY category ASC, name ASC")
    fun getAllForCharacter(characterId: Long): Flow<List<Ability>>

    @Query("SELECT * FROM abilities WHERE id = :id")
    suspend fun getById(id: Long): Ability?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ability: Ability): Long

    @Update
    suspend fun update(ability: Ability)

    @Delete
    suspend fun delete(ability: Ability)

    @Query("DELETE FROM abilities WHERE characterId = :characterId")
    suspend fun deleteAllForCharacter(characterId: Long)
}
