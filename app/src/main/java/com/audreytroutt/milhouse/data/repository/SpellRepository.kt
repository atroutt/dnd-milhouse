package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.SpellDao
import com.audreytroutt.milhouse.data.model.Spell
import kotlinx.coroutines.flow.Flow

class SpellRepository(private val dao: SpellDao) {
    fun getAll(): Flow<List<Spell>> = dao.getAll()
    suspend fun getById(id: Long): Spell? = dao.getById(id)
    suspend fun count(): Int = dao.count()
    suspend fun insert(spell: Spell): Long = dao.insert(spell)
    suspend fun insertAll(spells: List<Spell>) = dao.insertAll(spells)
    suspend fun update(spell: Spell) = dao.update(spell)
    suspend fun delete(spell: Spell) = dao.delete(spell)
}
