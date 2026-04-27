package com.audreytroutt.milhouse

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import com.audreytroutt.milhouse.data.repository.ActionRepository
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import com.audreytroutt.milhouse.data.repository.NoteRepository
import com.audreytroutt.milhouse.data.repository.SpellRepository
import com.audreytroutt.milhouse.db.MilhouseDatabase
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
 * Instrumented repository tests — run on device/emulator.
 * Each test gets a fresh in-memory SQLDelight database, so there's no shared state.
 *
 * What's covered:
 *   - Create a character (required for foreign-key style queries)
 *   - Insert + read back: Spell, Ability, Action, Note
 *   - Update: all entity types
 *   - Delete single entity: all entity types
 *   - deleteAllForCharacter cascade helper used by CharacterRepository
 *   - isPrepared toggle persists (used by SpellViewModel.togglePrepared)
 *   - usesRemaining mutation persists (ability use-tracking gameplay loop)
 *   - insertAll: bulk inserts multiple spells in one transaction (SRD import)
 *   - Boolean flags stored as INTEGER round-trip correctly (isConcentration, isRitual, isPassive)
 */
@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var driver: SqlDriver
    private lateinit var db: MilhouseDatabase
    private lateinit var characterRepo: CharacterRepository
    private lateinit var spellRepo: SpellRepository
    private lateinit var abilityRepo: AbilityRepository
    private lateinit var actionRepo: ActionRepository
    private lateinit var noteRepo: NoteRepository

    // Stub characterId — SQLite doesn't enforce FKs without PRAGMA foreign_keys = ON,
    // so tests that don't need a real character row can safely use this value.
    private val CHARACTER_ID = 1L

    private val testCharacter = DndCharacter(
        name = "Shoobert",
        characterClass = "Wizard",
        species = "Elf",
        colorIndex = 0,
        iconIndex = 0
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        driver = AndroidSqliteDriver(MilhouseDatabase.Schema, context, null) // null = in-memory
        db = MilhouseDatabase(driver)
        characterRepo = CharacterRepository(db)
        spellRepo = SpellRepository(db)
        abilityRepo = AbilityRepository(db)
        actionRepo = ActionRepository(db)
        noteRepo = NoteRepository(db)
    }

    @After
    fun teardown() = driver.close()

    // ── Character ─────────────────────────────────────────────────────────────

    @Test
    fun character_insertAndRead() = runTest {
        val id = characterRepo.insert(testCharacter)
        val loaded = characterRepo.getById(id)
        assertNotNull(loaded)
        assertEquals("Shoobert", loaded?.name)
        assertEquals("Wizard", loaded?.characterClass)
    }

    @Test
    fun character_updatePersists() = runTest {
        val id = characterRepo.insert(testCharacter)
        val original = characterRepo.getById(id)!!
        characterRepo.update(original.copy(name = "Shoobert II", characterClass = "Sorcerer"))
        val updated = characterRepo.getById(id)
        assertEquals("Shoobert II", updated?.name)
        assertEquals("Sorcerer", updated?.characterClass)
    }

    @Test
    fun character_deleteRemovesFromList() = runTest {
        val id = characterRepo.insert(testCharacter)
        val inserted = characterRepo.getById(id)!!
        characterRepo.delete(inserted, spellRepo, abilityRepo, actionRepo, noteRepo)
        assertNull(characterRepo.getById(id))
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
        val id = spellRepo.insert(spell)
        val loaded = spellRepo.getById(id)
        assertNotNull(loaded)
        assertEquals("Fireball", loaded?.name)
        assertEquals(3, loaded?.level)
        assertEquals("Evocation", loaded?.school)
    }

    @Test
    fun spell_updatePersists() = runTest {
        val id = spellRepo.insert(
            Spell(
                characterId = CHARACTER_ID, name = "Mage Armor", level = 1,
                school = "Abjuration", castingTime = "1 action", range = "Touch",
                duration = "8 hours", components = "V, S, M", description = "You surround..."
            )
        )
        val original = spellRepo.getById(id)!!
        spellRepo.update(original.copy(name = "Mage Armour"))
        assertEquals("Mage Armour", spellRepo.getById(id)?.name)
    }

    @Test
    fun spell_deleteRemovesFromCharacterList() = runTest {
        val id = spellRepo.insert(
            Spell(
                characterId = CHARACTER_ID, name = "Shield", level = 1,
                school = "Abjuration", castingTime = "1 reaction", range = "Self",
                duration = "1 round", components = "V, S", description = "+5 AC..."
            )
        )
        val spell = spellRepo.getById(id)!!
        spellRepo.delete(spell)
        assertTrue(spellRepo.getAllForCharacter(CHARACTER_ID).first().none { it.id == id })
    }

    @Test
    fun spell_deleteAllForCharacter() = runTest {
        spellRepo.insertAll(
            listOf(
                Spell(characterId = CHARACTER_ID, name = "A", level = 0, school = "Evocation",
                    castingTime = "1 action", range = "30 ft", duration = "Instant",
                    components = "V", description = ""),
                Spell(characterId = CHARACTER_ID, name = "B", level = 1, school = "Evocation",
                    castingTime = "1 action", range = "30 ft", duration = "Instant",
                    components = "V", description = "")
            )
        )
        spellRepo.deleteAllForCharacter(CHARACTER_ID)
        assertTrue(spellRepo.getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    @Test
    fun spell_togglePrepared_persists() = runTest {
        val id = spellRepo.insert(
            Spell(characterId = CHARACTER_ID, name = "Fly", level = 3, school = "Transmutation",
                castingTime = "1 action", range = "Touch", duration = "10 minutes",
                components = "V, S, M", description = "You grant a willing creature the ability to fly.")
        )
        val original = spellRepo.getById(id)!!
        assertTrue(!original.isPrepared)
        spellRepo.update(original.copy(isPrepared = true))
        assertTrue(spellRepo.getById(id)!!.isPrepared)
        spellRepo.update(spellRepo.getById(id)!!.copy(isPrepared = false))
        assertTrue(!spellRepo.getById(id)!!.isPrepared)
    }

    @Test
    fun spell_booleanFlags_roundTrip() = runTest {
        val id = spellRepo.insert(
            Spell(
                characterId = CHARACTER_ID, name = "Concentration Ritual", level = 2,
                school = "Divination", castingTime = "10 minutes", range = "Self",
                duration = "1 hour", components = "V, S", description = "...",
                isConcentration = true, isRitual = true
            )
        )
        val loaded = spellRepo.getById(id)!!
        assertTrue(loaded.isConcentration)
        assertTrue(loaded.isRitual)
        assertTrue(!loaded.isPrepared)
    }

    @Test
    fun spell_insertAll_insertsAll() = runTest {
        spellRepo.insertAll(
            listOf(
                Spell(characterId = CHARACTER_ID, name = "Fireball", level = 3, school = "Evocation",
                    castingTime = "1 action", range = "150 feet", duration = "Instantaneous",
                    components = "V, S, M", description = "Big boom."),
                Spell(characterId = CHARACTER_ID, name = "Magic Missile", level = 1, school = "Evocation",
                    castingTime = "1 action", range = "120 feet", duration = "Instantaneous",
                    components = "V, S", description = "Unerring darts of force.")
            )
        )
        assertEquals(2, spellRepo.getAllForCharacter(CHARACTER_ID).first().size)
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
        val id = abilityRepo.insert(ability)
        val loaded = abilityRepo.getById(id)
        assertNotNull(loaded)
        assertEquals("Arcane Recovery", loaded?.name)
        assertEquals("Class Feature", loaded?.category)
        assertEquals(1, loaded?.usesMax)
    }

    @Test
    fun ability_insertAll_appearsInCharacterList() = runTest {
        abilityRepo.insertAll(
            listOf(
                Ability(characterId = CHARACTER_ID, name = "Darkvision", category = "Species Trait",
                    description = "See in the dark.", isPassive = true),
                Ability(characterId = CHARACTER_ID, name = "Keen Senses", category = "Species Trait",
                    description = "Proficiency in Perception.", isPassive = true)
            )
        )
        val list = abilityRepo.getAllForCharacter(CHARACTER_ID).first()
        assertEquals(2, list.size)
        assertTrue(list.any { it.name == "Darkvision" })
        assertTrue(list.any { it.name == "Keen Senses" })
    }

    @Test
    fun ability_updatePersists() = runTest {
        val id = abilityRepo.insert(
            Ability(characterId = CHARACTER_ID, name = "Second Wind", category = "Class Feature",
                description = "Regain HP.", usesMax = 1, usesRemaining = 1, rechargeOn = "Short Rest")
        )
        val original = abilityRepo.getById(id)!!
        abilityRepo.update(original.copy(usesRemaining = 0))
        assertEquals(0, abilityRepo.getById(id)?.usesRemaining)
    }

    @Test
    fun ability_usesRemaining_roundTrips() = runTest {
        val id = abilityRepo.insert(
            Ability(characterId = CHARACTER_ID, name = "Action Surge", category = "Class Feature",
                description = "Take an additional action.", usesMax = 3, usesRemaining = 3,
                rechargeOn = "Long Rest")
        )
        val loaded = abilityRepo.getById(id)!!
        assertEquals(3, loaded.usesMax)
        assertEquals(3, loaded.usesRemaining)

        abilityRepo.update(loaded.copy(usesRemaining = 1))
        assertEquals(1, abilityRepo.getById(id)?.usesRemaining)
    }

    @Test
    fun ability_isPassive_roundTrips() = runTest {
        val id = abilityRepo.insert(
            Ability(characterId = CHARACTER_ID, name = "Darkvision", category = "Species Trait",
                description = "See in the dark.", isPassive = true)
        )
        assertTrue(abilityRepo.getById(id)!!.isPassive)
    }

    @Test
    fun ability_delete() = runTest {
        val id = abilityRepo.insert(
            Ability(characterId = CHARACTER_ID, name = "Channel Divinity", category = "Class Feature",
                description = "Channel divine energy.")
        )
        val ability = abilityRepo.getById(id)!!
        abilityRepo.delete(ability)
        assertNull(abilityRepo.getById(id))
    }

    @Test
    fun ability_deleteAllForCharacter() = runTest {
        abilityRepo.insertAll(
            listOf(
                Ability(characterId = CHARACTER_ID, name = "A", category = "Other", description = ""),
                Ability(characterId = CHARACTER_ID, name = "B", category = "Other", description = "")
            )
        )
        abilityRepo.deleteAllForCharacter(CHARACTER_ID)
        assertTrue(abilityRepo.getAllForCharacter(CHARACTER_ID).first().isEmpty())
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
        val id = actionRepo.insert(action)
        val loaded = actionRepo.getById(id)
        assertNotNull(loaded)
        assertEquals("Attack", loaded?.name)
        assertEquals("Action", loaded?.actionType)
        assertEquals("+5", loaded?.toHit)
    }

    @Test
    fun action_insertAll_appearsInCharacterList() = runTest {
        actionRepo.insertAll(
            listOf(
                DndAction(characterId = CHARACTER_ID, name = "Dash", actionType = "Action",
                    description = "Double your movement."),
                DndAction(characterId = CHARACTER_ID, name = "Dodge", actionType = "Action",
                    description = "Attackers have disadvantage.")
            )
        )
        val list = actionRepo.getAllForCharacter(CHARACTER_ID).first()
        assertEquals(2, list.size)
    }

    @Test
    fun action_updatePersists() = runTest {
        val id = actionRepo.insert(
            DndAction(characterId = CHARACTER_ID, name = "Attack", actionType = "Action",
                description = "Make an attack.", toHit = "+4")
        )
        val original = actionRepo.getById(id)!!
        actionRepo.update(original.copy(toHit = "+6", damage = "1d8+4", damageType = "Piercing"))
        val updated = actionRepo.getById(id)!!
        assertEquals("+6", updated.toHit)
        assertEquals("1d8+4", updated.damage)
        assertEquals("Piercing", updated.damageType)
    }

    @Test
    fun action_delete() = runTest {
        val id = actionRepo.insert(
            DndAction(characterId = CHARACTER_ID, name = "Shove", actionType = "Action",
                description = "Push a creature.")
        )
        val action = actionRepo.getById(id)!!
        actionRepo.delete(action)
        assertNull(actionRepo.getById(id))
    }

    @Test
    fun action_deleteAllForCharacter() = runTest {
        actionRepo.insertAll(
            listOf(
                DndAction(characterId = CHARACTER_ID, name = "A", actionType = "Action", description = ""),
                DndAction(characterId = CHARACTER_ID, name = "B", actionType = "Action", description = "")
            )
        )
        actionRepo.deleteAllForCharacter(CHARACTER_ID)
        assertTrue(actionRepo.getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    // ── Notes ─────────────────────────────────────────────────────────────────

    @Test
    fun note_insertAndReadBack() = runTest {
        val note = Note(
            characterId = CHARACTER_ID,
            title = "Session 1 recap",
            content = "We defeated the goblin war band.",
            tags = "session,combat"
        )
        val id = noteRepo.insert(note)
        val loaded = noteRepo.getById(id)
        assertNotNull(loaded)
        assertEquals("Session 1 recap", loaded?.title)
        assertEquals(listOf("session", "combat"), loaded?.tagList())
    }

    @Test
    fun note_updatePersists() = runTest {
        val id = noteRepo.insert(
            Note(characterId = CHARACTER_ID, title = "Draft", content = "TBD")
        )
        val original = noteRepo.getById(id)!!
        noteRepo.update(original.copy(title = "Revised", content = "Finished content."))
        val updated = noteRepo.getById(id)
        assertEquals("Revised", updated?.title)
        assertEquals("Finished content.", updated?.content)
    }

    @Test
    fun note_delete() = runTest {
        val id = noteRepo.insert(Note(characterId = CHARACTER_ID, title = "To delete", content = "gone"))
        val note = noteRepo.getById(id)!!
        noteRepo.delete(note)
        assertNull(noteRepo.getById(id))
    }

    @Test
    fun note_deleteAllForCharacter() = runTest {
        noteRepo.insert(Note(characterId = CHARACTER_ID, title = "A", content = ""))
        noteRepo.insert(Note(characterId = CHARACTER_ID, title = "B", content = ""))
        noteRepo.deleteAllForCharacter(CHARACTER_ID)
        assertTrue(noteRepo.getAllForCharacter(CHARACTER_ID).first().isEmpty())
    }

    // ── Cascade delete (CharacterRepository pattern) ──────────────────────────

    @Test
    fun cascadeDelete_removesAllDataForCharacter() = runTest {
        val charId = characterRepo.insert(testCharacter)
        val character = characterRepo.getById(charId)!!

        spellRepo.insert(Spell(
            characterId = charId, name = "Prestidigitation", level = 0,
            school = "Transmutation", castingTime = "1 action", range = "10 ft",
            duration = "1 hour", components = "V, S", description = "Minor magic."
        ))
        abilityRepo.insert(Ability(
            characterId = charId, name = "Spellcasting", category = "Class Feature",
            description = "Cast spells using Intelligence."
        ))
        actionRepo.insert(DndAction(
            characterId = charId, name = "Dodge", actionType = "Action",
            description = "Attackers have disadvantage."
        ))
        noteRepo.insert(Note(
            characterId = charId, title = "Backstory", content = "Born in a tower..."
        ))

        characterRepo.delete(character, spellRepo, abilityRepo, actionRepo, noteRepo)

        assertNull(characterRepo.getById(charId))
        assertTrue(spellRepo.getAllForCharacter(charId).first().isEmpty())
        assertTrue(abilityRepo.getAllForCharacter(charId).first().isEmpty())
        assertTrue(actionRepo.getAllForCharacter(charId).first().isEmpty())
        assertTrue(noteRepo.getAllForCharacter(charId).first().isEmpty())
    }
}
