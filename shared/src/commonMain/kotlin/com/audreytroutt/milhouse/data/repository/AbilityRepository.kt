package com.audreytroutt.milhouse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.db.MilhouseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AbilityRepository(private val database: MilhouseDatabase) {

    fun getAllForCharacter(characterId: Long): Flow<List<Ability>> =
        database.abilityQueries.getAllForCharacter(characterId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toModel() } }

    suspend fun getById(id: Long): Ability? =
        database.abilityQueries.getById(id).executeAsOneOrNull()?.toModel()

    suspend fun insert(ability: Ability): Long {
        database.abilityQueries.insertNew(
            characterId = ability.characterId,
            name = ability.name,
            category = ability.category,
            description = ability.description,
            usesMax = ability.usesMax.toLong(),
            usesRemaining = ability.usesRemaining.toLong(),
            rechargeOn = ability.rechargeOn,
            isPassive = if (ability.isPassive) 1L else 0L
        )
        return database.abilityQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun insertAll(abilities: List<Ability>) {
        database.transaction {
            abilities.forEach { ability ->
                database.abilityQueries.insertOrIgnore(
                    characterId = ability.characterId,
                    name = ability.name,
                    category = ability.category,
                    description = ability.description,
                    usesMax = ability.usesMax.toLong(),
                    usesRemaining = ability.usesRemaining.toLong(),
                    rechargeOn = ability.rechargeOn,
                    isPassive = if (ability.isPassive) 1L else 0L
                )
            }
        }
    }

    suspend fun update(ability: Ability) {
        database.abilityQueries.update(
            characterId = ability.characterId,
            name = ability.name,
            category = ability.category,
            description = ability.description,
            usesMax = ability.usesMax.toLong(),
            usesRemaining = ability.usesRemaining.toLong(),
            rechargeOn = ability.rechargeOn,
            isPassive = if (ability.isPassive) 1L else 0L,
            id = ability.id
        )
    }

    suspend fun delete(ability: Ability) {
        database.abilityQueries.delete(ability.id)
    }

    suspend fun deleteAllForCharacter(characterId: Long) {
        database.abilityQueries.deleteAllForCharacter(characterId)
    }

    private fun com.audreytroutt.milhouse.db.Abilities.toModel() = Ability(
        id = id,
        characterId = characterId,
        name = name,
        category = category,
        description = description,
        usesMax = usesMax.toInt(),
        usesRemaining = usesRemaining.toInt(),
        rechargeOn = rechargeOn,
        isPassive = isPassive != 0L
    )
}
