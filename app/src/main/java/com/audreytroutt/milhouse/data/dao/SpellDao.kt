package com.audreytroutt.milhouse.data.dao

import androidx.room.*
import com.audreytroutt.milhouse.data.model.Spell
import kotlinx.coroutines.flow.Flow

@Dao
interface SpellDao {
    @Query("SELECT * FROM spells ORDER BY level ASC, name ASC")
    fun getAll(): Flow<List<Spell>>

    @Query("SELECT * FROM spells WHERE id = :id")
    suspend fun getById(id: Long): Spell?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spell: Spell): Long

    @Update
    suspend fun update(spell: Spell)

    @Delete
    suspend fun delete(spell: Spell)
}
