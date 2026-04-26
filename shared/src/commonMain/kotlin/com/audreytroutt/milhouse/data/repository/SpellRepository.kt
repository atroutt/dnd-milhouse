package com.audreytroutt.milhouse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.db.MilhouseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SpellRepository(private val database: MilhouseDatabase) {

    fun getAllForCharacter(characterId: Long): Flow<List<Spell>> =
        database.spellQueries.getAllForCharacter(characterId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toModel() } }

    suspend fun getById(id: Long): Spell? =
        database.spellQueries.getById(id).executeAsOneOrNull()?.toModel()

    suspend fun insert(spell: Spell): Long {
        var newId = 0L
        database.transaction {
            database.spellQueries.insertNew(
                characterId = spell.characterId,
                name = spell.name,
                level = spell.level.toLong(),
                school = spell.school,
                castingTime = spell.castingTime,
                range = spell.range,
                duration = spell.duration,
                components = spell.components,
                materialComponents = spell.materialComponents,
                description = spell.description,
                higherLevels = spell.higherLevels,
                classes = spell.classes,
                isConcentration = if (spell.isConcentration) 1L else 0L,
                isRitual = if (spell.isRitual) 1L else 0L,
                isPrepared = if (spell.isPrepared) 1L else 0L
            )
            newId = database.spellQueries.lastInsertRowId().executeAsOne()
        }
        return newId
    }

    suspend fun insertAll(spells: List<Spell>) {
        database.transaction {
            spells.forEach { spell ->
                database.spellQueries.insertOrIgnore(
                    characterId = spell.characterId,
                    name = spell.name,
                    level = spell.level.toLong(),
                    school = spell.school,
                    castingTime = spell.castingTime,
                    range = spell.range,
                    duration = spell.duration,
                    components = spell.components,
                    materialComponents = spell.materialComponents,
                    description = spell.description,
                    higherLevels = spell.higherLevels,
                    classes = spell.classes,
                    isConcentration = if (spell.isConcentration) 1L else 0L,
                    isRitual = if (spell.isRitual) 1L else 0L,
                    isPrepared = if (spell.isPrepared) 1L else 0L
                )
            }
        }
    }

    suspend fun update(spell: Spell) {
        database.spellQueries.update(
            characterId = spell.characterId,
            name = spell.name,
            level = spell.level.toLong(),
            school = spell.school,
            castingTime = spell.castingTime,
            range = spell.range,
            duration = spell.duration,
            components = spell.components,
            materialComponents = spell.materialComponents,
            description = spell.description,
            higherLevels = spell.higherLevels,
            classes = spell.classes,
            isConcentration = if (spell.isConcentration) 1L else 0L,
            isRitual = if (spell.isRitual) 1L else 0L,
            isPrepared = if (spell.isPrepared) 1L else 0L,
            id = spell.id
        )
    }

    suspend fun delete(spell: Spell) {
        database.spellQueries.delete(spell.id)
    }

    suspend fun deleteAllForCharacter(characterId: Long) {
        database.spellQueries.deleteAllForCharacter(characterId)
    }

    private fun com.audreytroutt.milhouse.db.Spells.toModel() = Spell(
        id = id,
        characterId = characterId,
        name = name,
        level = level.toInt(),
        school = school,
        castingTime = castingTime,
        range = range,
        duration = duration,
        components = components,
        materialComponents = materialComponents,
        description = description,
        higherLevels = higherLevels,
        classes = classes,
        isConcentration = isConcentration != 0L,
        isRitual = isRitual != 0L,
        isPrepared = isPrepared != 0L
    )
}
