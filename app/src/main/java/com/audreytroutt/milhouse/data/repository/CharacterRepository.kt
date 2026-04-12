package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.AbilityDao
import com.audreytroutt.milhouse.data.dao.ActionDao
import com.audreytroutt.milhouse.data.dao.CharacterDao
import com.audreytroutt.milhouse.data.dao.NoteDao
import com.audreytroutt.milhouse.data.dao.SpellDao
import com.audreytroutt.milhouse.data.model.DndCharacter
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val dao: CharacterDao,
    private val spellDao: SpellDao,
    private val abilityDao: AbilityDao,
    private val actionDao: ActionDao,
    private val noteDao: NoteDao
) {
    fun getAll(): Flow<List<DndCharacter>> = dao.getAll()
    suspend fun getById(id: Long): DndCharacter? = dao.getById(id)
    suspend fun insert(character: DndCharacter): Long = dao.insert(character)
    suspend fun update(character: DndCharacter) = dao.update(character)
    suspend fun delete(character: DndCharacter) {
        spellDao.deleteAllForCharacter(character.id)
        abilityDao.deleteAllForCharacter(character.id)
        actionDao.deleteAllForCharacter(character.id)
        noteDao.deleteAllForCharacter(character.id)
        dao.delete(character)
    }
}
