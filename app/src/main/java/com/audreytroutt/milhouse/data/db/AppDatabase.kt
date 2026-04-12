package com.audreytroutt.milhouse.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.audreytroutt.milhouse.data.dao.AbilityDao
import com.audreytroutt.milhouse.data.dao.ActionDao
import com.audreytroutt.milhouse.data.dao.CharacterDao
import com.audreytroutt.milhouse.data.dao.NoteDao
import com.audreytroutt.milhouse.data.dao.SpellDao
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.data.model.Spell

@Database(
    entities = [DndCharacter::class, Spell::class, Ability::class, DndAction::class, Note::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun spellDao(): SpellDao
    abstract fun abilityDao(): AbilityDao
    abstract fun actionDao(): ActionDao
    abstract fun noteDao(): NoteDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the characters table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `characters` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `characterClass` TEXT NOT NULL,
                `species` TEXT NOT NULL,
                `colorIndex` INTEGER NOT NULL,
                `iconIndex` INTEGER NOT NULL
            )
        """.trimIndent())
        // Insert a default character to own all existing data
        database.execSQL(
            "INSERT INTO `characters` (`name`, `characterClass`, `species`, `colorIndex`, `iconIndex`) VALUES ('My Character', '', '', 0, 0)"
        )
        // Add characterId to existing tables, pointing at the new default character (id = 1)
        database.execSQL("ALTER TABLE `spells` ADD COLUMN `characterId` INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE `abilities` ADD COLUMN `characterId` INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE `actions` ADD COLUMN `characterId` INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE `notes` ADD COLUMN `characterId` INTEGER NOT NULL DEFAULT 1")
    }
}
