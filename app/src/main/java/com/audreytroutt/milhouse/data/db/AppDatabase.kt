package com.audreytroutt.milhouse.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.audreytroutt.milhouse.data.dao.AbilityDao
import com.audreytroutt.milhouse.data.dao.ActionDao
import com.audreytroutt.milhouse.data.dao.NoteDao
import com.audreytroutt.milhouse.data.dao.SpellDao
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.data.model.Spell

@Database(
    entities = [Spell::class, Ability::class, DndAction::class, Note::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spellDao(): SpellDao
    abstract fun abilityDao(): AbilityDao
    abstract fun actionDao(): ActionDao
    abstract fun noteDao(): NoteDao
}
