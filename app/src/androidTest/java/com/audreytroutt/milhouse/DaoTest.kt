package com.audreytroutt.milhouse

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.audreytroutt.milhouse.data.db.AppDatabase
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.data.model.Spell
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented DAO tests — run on device/emulator.
 * Each test gets a fresh in-memory database, so there's no shared state.
 *
 * What's covered:
 *   - Create a character (required for foreign-key style queries)
 *   - Insert + read back: Spell, Ability, Action, Note
 *   - Update: Spell name change persists
 *   - Delete single entity
 *   - deleteAllForCharacter cascade helper used by CharacterRepository
 */
@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var db: AppDatabase
    private val CHARACTER_ID = 1L

    // A minimal character so our characterId FK is realistic.
    private val testCharacter = DndCharacter(
        id = CHARACTER_ID,
        name = "Shoobert",
        characterClass = "Wizard",
        species = "Elf",
        colorIndex = 0,
        iconIndex = 0
    )

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // safe only in tests
            .build()
    }

    @After
    fun closeDb() = db.close()

    // ── Character ─────────────────────────────────────────────────────────────

    @Test
    fun character_insertAndRead() = runTest {
        db.characterDao().insert(testCharacter)
        val loaded = db.characterDao().getById(CHARACTER_ID)
        assertNotNull(loaded)
        assertEquals("Shoobert", loaded?.name)
        assertEquals("Wizard", loaded?.characterClass)
    }

    @Test
    fun character_deleteRemovesFromList() = runTest {
        db.characterDao().insert(testCharacter)
        val inserted = db.characterDao().getById(CHARACTER_ID)!!
        db.characterDao().delete(inserted)
        assertNull(db.characterDao().getById(CHARACTER_ID))
    }

    // ── Spells ────────────────────────────────────────────────────────────────

    @Test
    fun spell_insertAndReadBack() = runTest {
        val spell = Spell(
            characterId = CHARACTER_ID,
            name = "Fireball",
            level = 3,
            school = "Evocation",
            castingTime = "1 action",
            range = "150 feet",
            duration = "Instantaneous",
            components = "V, S, M",
            description = "A bright streak flashes..."
        )
        val id = db.spellDao().insert(spell)
        val loaded = db.spellDao().getById(id)
        assertNotNull(loaded)
        assertEquals("Fireball", loaded?.name)
        assertEquals(3, loaded?.level)
        assertEquals("Evocation", loaded?.school)
    }

    @Test
    fun spell_updatePersists() = runTest {
        val id = db.spellDao().insert(
            Spell(
                characterId = CHARACTER_ID, name = "Mage Armor", level = 1,
                school = "Abjuration", castingTime = "1 action", range = "Touch",
                duration = "8 hours", components = "V, S, M", description = "You surround..."
            )
        )
        val original = db.spellDao().getById(id)!!
        db.spellDao().update(original.copy(name = "Mage Armour"))
        assertEquals("Mage Armour", db.spellDao().getById(id)?.name)
    }

    @Test
    fun spell_deleteRemovesFromCharacterList() = runTest {
        val id = db.spellDao().insert(
            Spell(
                characterId = CHARACTER_ID, name = "Shield", level = 1,
                school = "Abjuration", castingTime = "1 reaction", range = "Self",
                duration = "1 round", components = "V, S", description = "+5 AC..."
            )
        )
        val spell = db.spellDao().getById(id)!!
        db.spellDao().delete(spell)
        val list = db.spellDao().getAllForCharacter(CHARACTER_ID).first()
        assertTrue(list.none { it.id == id })
    }

    @Test
    fun spell_deleteAllForCharacter() = runTest {
        db.spellDao().insertAll(
            listOf(
                Spell(characterId = CHARACTER_ID, name = "A", level = 0, school = "Evocation",
                    castingTime = "1 action", range = "30 ft", duration = "Instant",
                    components = "V", description = ""),
                Spell(characterId = CHARACTER_ID, name = "B", level = 1, school = "Evocation",
                    castingTime = "1 action", range = "30 ft", duration = "Instant",
                    components = "V", description = "")
            )
        )
        db.spellDao().deleteAllForCharacter(CHARACTER_ID)
        assertTrue(db.spellDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    // ── Abilities ─────────────────────────────────────────────────────────────

    @Test
    fun ability_insertAndReadBack() = runTest {
        val ability = Ability(
            characterId = CHARACTER_ID,
            name = "Arcane Recovery",
            category = "Class Feature",
            description = "Regain spell slots on short rest.",
            usesMax = 1,
            usesRemaining = 1,
            rechargeOn = "Long Rest"
        )
        val id = db.abilityDao().insert(ability)
        val loaded = db.abilityDao().getById(id)
        assertNotNull(loaded)
        assertEquals("Arcane Recovery", loaded?.name)
        assertEquals("Class Feature", loaded?.category)
        assertEquals(1, loaded?.usesMax)
    }

    @Test
    fun ability_insertAll_appearsInCharacterList() = runTest {
        val abilities = listOf(
            Ability(characterId = CHARACTER_ID, name = "Darkvision", category = "Species Trait",
                description = "See in the dark.", isPassive = true),
            Ability(characterId = CHARACTER_ID, name = "Keen Senses", category = "Species Trait",
                description = "Proficiency in Perception.", isPassive = true)
        )
        db.abilityDao().insertAll(abilities)
        val list = db.abilityDao().getAllForCharacter(CHARACTER_ID).first()
        assertEquals(2, list.size)
        assertTrue(list.any { it.name == "Darkvision" })
        assertTrue(list.any { it.name == "Keen Senses" })
    }

    @Test
    fun ability_deleteAllForCharacter() = runTest {
        db.abilityDao().insertAll(
            listOf(
                Ability(characterId = CHARACTER_ID, name = "A", category = "Other", description = ""),
                Ability(characterId = CHARACTER_ID, name = "B", category = "Other", description = "")
            )
        )
        db.abilityDao().deleteAllForCharacter(CHARACTER_ID)
        assertTrue(db.abilityDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Test
    fun action_insertAndReadBack() = runTest {
        val action = DndAction(
            characterId = CHARACTER_ID,
            name = "Attack",
            actionType = "Action",
            description = "Make a melee or ranged attack.",
            damage = "1d8",
            damageType = "Slashing",
            toHit = "+5"
        )
        val id = db.actionDao().insert(action)
        val loaded = db.actionDao().getById(id)
        assertNotNull(loaded)
        assertEquals("Attack", loaded?.name)
        assertEquals("Action", loaded?.actionType)
        assertEquals("+5", loaded?.toHit)
    }

    @Test
    fun action_insertAll_appearsInCharacterList() = runTest {
        val actions = listOf(
            DndAction(characterId = CHARACTER_ID, name = "Dash", actionType = "Action",
                description = "Double your movement."),
            DndAction(characterId = CHARACTER_ID, name = "Dodge", actionType = "Action",
                description = "Attackers have disadvantage.")
        )
        db.actionDao().insertAll(actions)
        val list = db.actionDao().getAllForCharacter(CHARACTER_ID).first()
        assertEquals(2, list.size)
    }

    @Test
    fun action_deleteAllForCharacter() = runTest {
        db.actionDao().insertAll(
            listOf(
                DndAction(characterId = CHARACTER_ID, name = "A", actionType = "Action", description = ""),
                DndAction(characterId = CHARACTER_ID, name = "B", actionType = "Action", description = "")
            )
        )
        db.actionDao().deleteAllForCharacter(CHARACTER_ID)
        assertTrue(db.actionDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    // ── Notes ─────────────────────────────────────────────────────────────────

    @Test
    fun note_insertAndReadBack() = runTest {
        val note = Note(
            characterId = CHARACTER_ID,
            title = "Session 1 recap",
            content = "We defeated the goblin warband.",
            tags = "session,combat"
        )
        val id = db.noteDao().insert(note)
        val loaded = db.noteDao().getById(id)
        assertNotNull(loaded)
        assertEquals("Session 1 recap", loaded?.title)
        assertEquals(listOf("session", "combat"), loaded?.tagList())
    }

    @Test
    fun note_updatePersists() = runTest {
        val id = db.noteDao().insert(
            Note(characterId = CHARACTER_ID, title = "Draft", content = "TBD")
        )
        val original = db.noteDao().getById(id)!!
        db.noteDao().update(original.copy(title = "Revised", content = "Finished content."))
        val updated = db.noteDao().getById(id)
        assertEquals("Revised", updated?.title)
        assertEquals("Finished content.", updated?.content)
    }

    @Test
    fun note_deleteAllForCharacter() = runTest {
        db.noteDao().insert(Note(characterId = CHARACTER_ID, title = "A", content = ""))
        db.noteDao().insert(Note(characterId = CHARACTER_ID, title = "B", content = ""))
        db.noteDao().deleteAllForCharacter(CHARACTER_ID)
        assertTrue(db.noteDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    // ── Cascade delete (CharacterRepository pattern) ──────────────────────────

    @Test
    fun cascadeDelete_removesAllDataForCharacter() = runTest {
        // Simulate what CharacterRepository.delete() does
        db.characterDao().insert(testCharacter)
        db.spellDao().insert(Spell(
            characterId = CHARACTER_ID, name = "Prestidigitation", level = 0,
            school = "Transmutation", castingTime = "1 action", range = "10 ft",
            duration = "1 hour", components = "V, S", description = "Minor magic."
        ))
        db.abilityDao().insert(Ability(
            characterId = CHARACTER_ID, name = "Spellcasting", category = "Class Feature",
            description = "Cast spells using Intelligence."
        ))
        db.actionDao().insert(DndAction(
            characterId = CHARACTER_ID, name = "Dodge", actionType = "Action",
            description = "Attackers have disadvantage."
        ))
        db.noteDao().insert(Note(
            characterId = CHARACTER_ID, title = "Backstory", content = "Born in a tower..."
        ))

        // Delete all child data then the character (repository order)
        db.spellDao().deleteAllForCharacter(CHARACTER_ID)
        db.abilityDao().deleteAllForCharacter(CHARACTER_ID)
        db.actionDao().deleteAllForCharacter(CHARACTER_ID)
        db.noteDao().deleteAllForCharacter(CHARACTER_ID)
        db.characterDao().delete(testCharacter)

        assertNull(db.characterDao().getById(CHARACTER_ID))
        assertTrue(db.spellDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
        assertTrue(db.abilityDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
        assertTrue(db.actionDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
        assertTrue(db.noteDao().getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }
}
