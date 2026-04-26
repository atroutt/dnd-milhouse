package com.audreytroutt.milhouse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.db.MilhouseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActionRepository(private val database: MilhouseDatabase) {

    fun getAllForCharacter(characterId: Long): Flow<List<DndAction>> =
        database.actionQueries.getAllForCharacter(characterId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toModel() } }

    suspend fun getById(id: Long): DndAction? =
        database.actionQueries.getById(id).executeAsOneOrNull()?.toModel()

    suspend fun insert(action: DndAction): Long {
        database.actionQueries.insertNew(
            characterId = action.characterId,
            name = action.name,
            actionType = action.actionType,
            description = action.description,
            damage = action.damage,
            damageType = action.damageType,
            toHit = action.toHit,
            range = action.range,
            savingThrow = action.savingThrow
        )
        return database.actionQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun insertAll(actions: List<DndAction>) {
        database.transaction {
            actions.forEach { action ->
                database.actionQueries.insertOrIgnore(
                    characterId = action.characterId,
                    name = action.name,
                    actionType = action.actionType,
                    description = action.description,
                    damage = action.damage,
                    damageType = action.damageType,
                    toHit = action.toHit,
                    range = action.range,
                    savingThrow = action.savingThrow
                )
            }
        }
    }

    suspend fun update(action: DndAction) {
        database.actionQueries.update(
            characterId = action.characterId,
            name = action.name,
            actionType = action.actionType,
            description = action.description,
            damage = action.damage,
            damageType = action.damageType,
            toHit = action.toHit,
            range = action.range,
            savingThrow = action.savingThrow,
            id = action.id
        )
    }

    suspend fun delete(action: DndAction) {
        database.actionQueries.delete(action.id)
    }

    suspend fun deleteAllForCharacter(characterId: Long) {
        database.actionQueries.deleteAllForCharacter(characterId)
    }

    private fun com.audreytroutt.milhouse.db.Actions.toModel() = DndAction(
        id = id,
        characterId = characterId,
        name = name,
        actionType = actionType,
        description = description,
        damage = damage,
        damageType = damageType,
        toHit = toHit,
        range = range,
        savingThrow = savingThrow
    )
}
