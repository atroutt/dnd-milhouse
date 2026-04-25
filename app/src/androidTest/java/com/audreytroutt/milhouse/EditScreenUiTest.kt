package com.audreytroutt.milhouse

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.audreytroutt.milhouse.data.model.DndCharacter
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end UI tests using UIAutomator — works on any API level (including API 35+).
 * Unlike Espresso / createAndroidComposeRule, UIAutomator drives the app through the
 * accessibility layer and does not call InputManager.getInstance(), so the API-35
 * Espresso breakage does not affect these tests.
 *
 * Each test pre-seeds one character, launches the app, exercises the UI, then cleans up.
 *
 * What's covered:
 *   - Save button is visible (not hidden by outer TopAppBar) on every edit screen type
 *   - Save button is disabled until required fields are filled
 *   - Saving a new entity navigates back to the list and shows the item
 *   - Switch / bottom tabs are hidden while on an edit screen
 */
@RunWith(AndroidJUnit4::class)
class EditScreenUiTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val context = instrumentation.targetContext
    private val pkg = context.packageName
    private val timeout = 5_000L

    private lateinit var app: MilhouseApplication
    private var testCharacterId: Long = 0

    @Before
    fun seedAndLaunch() {
        runBlocking {
            app = instrumentation.targetContext.applicationContext as MilhouseApplication
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

        val intent = context.packageManager.getLaunchIntentForPackage(pkg)!!.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(pkg).depth(0)), timeout)
    }

    @After
    fun deleteCharacter() {
        runBlocking {
            val db = app.database
            db.spellDao().deleteAllForCharacter(testCharacterId)
            db.abilityDao().deleteAllForCharacter(testCharacterId)
            db.actionDao().deleteAllForCharacter(testCharacterId)
            db.noteDao().deleteAllForCharacter(testCharacterId)
            db.characterDao().getById(testCharacterId)?.let { db.characterDao().delete(it) }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun openCharacter() {
        device.wait(Until.hasObject(By.text("UI Test Hero")), timeout)
        device.findObject(By.text("UI Test Hero")).click()
    }

    private fun openTab(label: String) {
        // NavigationBarItem exposes itself via text (the label), not contentDescription,
        // in UIAutomator's accessibility view — so use By.text() rather than By.desc().
        device.wait(Until.hasObject(By.text(label)), timeout)
        device.findObject(By.text(label)).click()
    }

    private fun waitForText(text: String) =
        device.wait(Until.hasObject(By.text(text)), timeout)

    private fun waitForDesc(desc: String) =
        device.wait(Until.hasObject(By.desc(desc)), timeout)

    // ── Save button visibility ────────────────────────────────────────────────

    @Test
    fun spellEdit_saveButtonVisible_onNewSpell() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        assertNotNull("Save button should be visible", waitForText("Save"))
    }

    @Test
    fun abilityEdit_saveButtonVisible_onNewAbility() {
        openCharacter()
        openTab("Abilities")
        waitForDesc("Add Ability")
        device.findObject(By.desc("Add Ability")).click()
        assertNotNull("Save button should be visible", waitForText("Save"))
    }

    @Test
    fun actionEdit_saveButtonVisible_onNewAction() {
        openCharacter()
        openTab("Actions")
        waitForDesc("Add Action")
        device.findObject(By.desc("Add Action")).click()
        assertNotNull("Save button should be visible", waitForText("Save"))
    }

    @Test
    fun noteEdit_saveButtonVisible_onNewNote() {
        openCharacter()
        openTab("Notes")
        waitForDesc("Add Note")
        device.findObject(By.desc("Add Note")).click()
        assertNotNull("Save button should be visible", waitForText("Save"))
    }

    // ── Save button disabled until required field filled ──────────────────────

    @Test
    fun spellEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        waitForText("Save")

        val saveBtn = device.findObject(By.text("Save"))
        assertFalse("Save should be disabled before name is entered", saveBtn.isEnabled)

        device.findObject(By.text("Spell Name *")).text = "Magic Missile"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        assertNotNull("Save should be enabled after name entered",
            device.findObject(By.text("Save").enabled(true)))
    }

    @Test
    fun abilityEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        openTab("Abilities")
        waitForDesc("Add Ability")
        device.findObject(By.desc("Add Ability")).click()
        waitForText("Save")

        val saveBtn = device.findObject(By.text("Save"))
        assertFalse("Save should be disabled before name is entered", saveBtn.isEnabled)

        device.findObject(By.text("Ability Name *")).text = "Second Wind"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        assertNotNull("Save should be enabled after name entered",
            device.findObject(By.text("Save").enabled(true)))
    }

    @Test
    fun actionEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        openTab("Actions")
        waitForDesc("Add Action")
        device.findObject(By.desc("Add Action")).click()
        waitForText("Save")

        val saveBtn = device.findObject(By.text("Save"))
        assertFalse("Save should be disabled before name is entered", saveBtn.isEnabled)

        device.findObject(By.text("Action Name *")).text = "Longsword Attack"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        assertNotNull("Save should be enabled after name entered",
            device.findObject(By.text("Save").enabled(true)))
    }

    @Test
    fun noteEdit_saveDisabledWithoutTitle_enabledAfterTitle() {
        openCharacter()
        openTab("Notes")
        waitForDesc("Add Note")
        device.findObject(By.desc("Add Note")).click()
        waitForText("Save")

        val saveBtn = device.findObject(By.text("Save"))
        assertFalse("Save should be disabled before title is entered", saveBtn.isEnabled)

        device.findObject(By.text("Title *")).text = "Session notes"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        assertNotNull("Save should be enabled after title entered",
            device.findObject(By.text("Save").enabled(true)))
    }

    // ── Saving navigates back and item appears in list ────────────────────────

    @Test
    fun spellEdit_saveNewSpell_appearsInList() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        waitForText("Spell Name *")
        device.findObject(By.text("Spell Name *")).text = "Fireball"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Fireball should appear in list after save", waitForText("Fireball"))
    }

    @Test
    fun abilityEdit_saveNewAbility_appearsInList() {
        openCharacter()
        openTab("Abilities")
        waitForDesc("Add Ability")
        device.findObject(By.desc("Add Ability")).click()
        waitForText("Ability Name *")
        device.findObject(By.text("Ability Name *")).text = "Action Surge"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Action Surge should appear in list after save", waitForText("Action Surge"))
    }

    @Test
    fun noteEdit_saveNewNote_appearsInList() {
        openCharacter()
        openTab("Notes")
        waitForDesc("Add Note")
        device.findObject(By.desc("Add Note")).click()
        waitForText("Title *")
        device.findObject(By.text("Title *")).text = "Tavern encounter"
        device.wait(Until.hasObject(By.text("Save").enabled(true)), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Tavern encounter should appear in list after save",
            waitForText("Tavern encounter"))
    }

    // ── Outer chrome hidden while editing ─────────────────────────────────────

    @Test
    fun editScreen_switchButtonHidden_whileEditing() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        waitForText("Save")
        assertNull("Switch button should not be visible while editing",
            device.findObject(By.text("Switch")))
    }

    @Test
    fun editScreen_bottomTabsHidden_whileEditing() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        waitForText("Save")
        assertNull("Spells tab should not be visible while editing",
            device.findObject(By.text("Spells")))
        assertNull("Abilities tab should not be visible while editing",
            device.findObject(By.text("Abilities")))
    }

    @Test
    fun editScreen_switchAndTabsReappear_afterNavigatingBack() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        waitForText("Save")
        device.findObject(By.desc("Back")).click()
        assertNotNull("Switch button should reappear after back", waitForText("Switch"))
        assertNotNull("Spells tab should reappear after back", waitForText("Spells"))
    }
}
