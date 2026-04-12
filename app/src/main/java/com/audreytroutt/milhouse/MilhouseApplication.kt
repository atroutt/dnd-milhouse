package com.audreytroutt.milhouse

import android.app.Application
import androidx.room.Room
import com.audreytroutt.milhouse.data.db.AppDatabase
import com.audreytroutt.milhouse.data.db.MIGRATION_1_2
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import com.audreytroutt.milhouse.data.repository.ActionRepository
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import com.audreytroutt.milhouse.data.repository.NoteRepository
import com.audreytroutt.milhouse.data.repository.SpellRepository

class MilhouseApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "milhouse.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    val characterRepository: CharacterRepository by lazy { CharacterRepository(database.characterDao()) }
    val spellRepository: SpellRepository by lazy { SpellRepository(database.spellDao()) }
    val abilityRepository: AbilityRepository by lazy { AbilityRepository(database.abilityDao()) }
    val actionRepository: ActionRepository by lazy { ActionRepository(database.actionDao()) }
    val noteRepository: NoteRepository by lazy { NoteRepository(database.noteDao()) }
}
