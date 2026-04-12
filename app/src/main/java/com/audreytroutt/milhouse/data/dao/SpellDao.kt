package com.audreytroutt.milhouse.data.dao

import androidx.room.*
import com.audreytroutt.milhouse.data.model.Spell
import kotlinx.coroutines.flow.Flow

@Dao
interface SpellDao {
    @Query("SELECT * FROM spells WHERE characterId = :characterId ORDER BY level ASC, name ASC")
    fun getAllForCharacter(characterId: Long): Flow<List<Spell>>

    @Query("SELECT * FROM spells WHERE id = :id")
    suspend fun getById(id: Long): Spell?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spell: Spell): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(spells: List<Spell>)

    @Update
    suspend fun update(spell: Spell)

    @Delete
    suspend fun delete(spell: Spell)

    @Query("DELETE FROM spells WHERE characterId = :characterId")
    suspend fun deleteAllForCharacter(characterId: Long)
}
