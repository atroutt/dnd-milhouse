package com.audreytroutt.milhouse

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.audreytroutt.milhouse.data.model.DndCharacter
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end UI tests using the real app and a real (in-device) database.
 * Each test pre-seeds one character, exercises the UI, then cleans up.
 *
 * What's covered:
 *   - Save button is visible (not hidden by outer TopAppBar) on every edit screen type
 *   - Save button is disabled until required fields are filled
 *   - Saving a new entity navigates back to the list and shows the item
 *   - Switch / bottom tabs are hidden while on an edit screen
 */
@RunWith(AndroidJUnit4::class)
class EditScreenUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var app: MilhouseApplication
    private var testCharacterId: Long = 0

    @Before
    fun seedCharacter() = runBlocking {
        app = composeTestRule.activity.application as MilhouseApplication
        testCharacterId = app.database.characterDao().insert(
            DndCharacter(
                name = "UI Test Hero",
                characterClass = "Fighter",
                species = "Human",
                colorIndex = 0,
                iconIndex = 0
            )
        )
    }

    @After
    fun deleteCharacter() = runBlocking {
        val db = app.database
        db.spellDao().deleteAllForCharacter(testCharacterId)
        db.abilityDao().deleteAllForCharacter(testCharacterId)
        db.actionDao().deleteAllForCharacter(testCharacterId)
        db.noteDao().deleteAllForCharacter(testCharacterId)
        db.characterDao().getById(testCharacterId)?.let { db.characterDao().delete(it) }
    }

    // ── Helper: navigate into the character's tab screen ─────────────────────

    private fun openCharacter() {
        composeTestRule.onNodeWithText("UI Test Hero").performClick()
    }

    private fun openTab(contentDescription: String) {
        composeTestRule.onNodeWithContentDescription(contentDescription).performClick()
    }

    // ── Save button visibility ────────────────────────────────────────────────

    @Test
    fun spellEdit_saveButtonVisible_onNewSpell() {
        openCharacter()
        composeTestRule.onNodeWithContentDescription("Add Spell").performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun abilityEdit_saveButtonVisible_onNewAbility() {
        openCharacter()
        openTab("Abilities")
        composeTestRule.onNodeWithContentDescription("Add Ability").performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun actionEdit_saveButtonVisible_onNewAction() {
        openCharacter()
        openTab("Actions")
        composeTestRule.onNodeWithContentDescription("Add Action").performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun noteEdit_saveButtonVisible_onNewNote() {
        openCharacter()
        openTab("Notes")
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    // ── Save button disabled until required field filled ──────────────────────

    @Test
    fun spellEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        composeTestRule.onNodeWithContentDescription("Add Spell").performClick()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Spell Name *").performTextInput("Magic Missile")
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun abilityEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        openTab("Abilities")
        composeTestRule.onNodeWithContentDescription("Add Ability").performClick()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Ability Name *").performTextInput("Second Wind")
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun actionEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        openTab("Actions")
        composeTestRule.onNodeWithContentDescription("Add Action").performClick()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Action Name *").performTextInput("Longsword Attack")
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun noteEdit_saveDisabledWithoutTitle_enabledAfterTitle() {
        openCharacter()
        openTab("Notes")
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Title *").performTextInput("Session notes")
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    // ── Saving navigates back and item appears in list ────────────────────────

    @Test
    fun spellEdit_saveNewSpell_appearsInList() {
        openCharacter()
        composeTestRule.onNodeWithContentDescription("Add Spell").performClick()
        composeTestRule.onNodeWithText("Spell Name *").performTextInput("Fireball")
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Fireball").assertIsDisplayed()
    }

    @Test
    fun abilityEdit_saveNewAbility_appearsInList() {
        openCharacter()
        openTab("Abilities")
        composeTestRule.onNodeWithContentDescription("Add Ability").performClick()
        composeTestRule.onNodeWithText("Ability Name *").performTextInput("Action Surge")
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Action Surge").assertIsDisplayed()
    }

    @Test
    fun noteEdit_saveNewNote_appearsInList() {
        openCharacter()
        openTab("Notes")
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()
        composeTestRule.onNodeWithText("Title *").performTextInput("Tavern encounter")
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Tavern encounter").assertIsDisplayed()
    }

    // ── Outer chrome hidden while editing ─────────────────────────────────────

    @Test
    fun editScreen_switchButtonHidden_whileEditing() {
        openCharacter()
        composeTestRule.onNodeWithContentDescription("Add Spell").performClick()
        // "Switch" button in outer TopAppBar should not exist on screen
        composeTestRule.onNodeWithText("Switch").assertDoesNotExist()
    }

    @Test
    fun editScreen_bottomTabsHidden_whileEditing() {
        openCharacter()
        composeTestRule.onNodeWithContentDescription("Add Spell").performClick()
        // Bottom tab labels shouldn't be visible while editing
        composeTestRule.onNodeWithText("Spells").assertDoesNotExist()
        composeTestRule.onNodeWithText("Abilities").assertDoesNotExist()
    }

    @Test
    fun editScreen_switchAndTabsReappear_afterNavigatingBack() {
        openCharacter()
        composeTestRule.onNodeWithContentDescription("Add Spell").performClick()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Switch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Spells").assertIsDisplayed()
    }
}
