package com.audreytroutt.milhouse.data.repository

import com.audreytroutt.milhouse.data.dao.AbilityDao
import com.audreytroutt.milhouse.data.model.Ability
import kotlinx.coroutines.flow.Flow

class AbilityRepository(private val dao: AbilityDao) {
    fun getAllForCharacter(characterId: Long): Flow<List<Ability>> = dao.getAllForCharacter(characterId)
    suspend fun getById(id: Long): Ability? = dao.getById(id)
    suspend fun insert(ability: Ability): Long = dao.insert(ability)
    suspend fun insertAll(abilities: List<Ability>) = dao.insertAll(abilities)
    suspend fun update(ability: Ability) = dao.update(ability)
    suspend fun delete(ability: Ability) = dao.delete(ability)
}
