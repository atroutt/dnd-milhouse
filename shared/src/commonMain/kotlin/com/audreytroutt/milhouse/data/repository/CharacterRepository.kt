package com.audreytroutt.milhouse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.db.MilhouseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepository(private val database: MilhouseDatabase) {

    fun getAll(): Flow<List<DndCharacter>> =
        database.characterQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toModel() } }

    suspend fun getById(id: Long): DndCharacter? =
        database.characterQueries.getById(id).executeAsOneOrNull()?.toModel()

    suspend fun insert(character: DndCharacter): Long {
        database.characterQueries.insertNew(
            name = character.name,
            characterClass = character.characterClass,
            species = character.species,
            colorIndex = character.colorIndex.toLong(),
            iconIndex = character.iconIndex.toLong()
        )
        return database.characterQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun update(character: DndCharacter) {
        database.characterQueries.update(
            name = character.name,
            characterClass = character.characterClass,
            species = character.species,
            colorIndex = character.colorIndex.toLong(),
            iconIndex = character.iconIndex.toLong(),
            id = character.id
        )
    }

    suspend fun delete(
        character: DndCharacter,
        spellRepo: SpellRepository,
        abilityRepo: AbilityRepository,
        actionRepo: ActionRepository,
        noteRepo: NoteRepository
    ) {
        spellRepo.deleteAllForCharacter(character.id)
        abilityRepo.deleteAllForCharacter(character.id)
        actionRepo.deleteAllForCharacter(character.id)
        noteRepo.deleteAllForCharacter(character.id)
        database.characterQueries.delete(character.id)
    }

    private fun com.audreytroutt.milhouse.db.Characters.toModel() = DndCharacter(
        id = id,
        name = name,
        characterClass = characterClass,
        species = species,
        colorIndex = colorIndex.toInt(),
        iconIndex = iconIndex.toInt()
    )
}
