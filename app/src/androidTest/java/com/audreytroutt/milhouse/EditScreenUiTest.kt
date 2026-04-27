package com.audreytroutt.milhouse

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import com.audreytroutt.milhouse.data.repository.ActionRepository
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import com.audreytroutt.milhouse.data.repository.NoteRepository
import com.audreytroutt.milhouse.data.repository.SpellRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

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

    private var testCharacterId: Long = 0

    @Before
    fun seedAndLaunch() {
        runBlocking {
            testCharacterId = GlobalContext.get().get<CharacterRepository>().insert(
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
            val koin = GlobalContext.get()
            val characterRepo = koin.get<CharacterRepository>()
            characterRepo.getById(testCharacterId)?.let { character ->
                characterRepo.delete(
                    character,
                    koin.get<SpellRepository>(),
                    koin.get<AbilityRepository>(),
                    koin.get<ActionRepository>(),
                    koin.get<NoteRepository>()
                )
            }
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
    //
    // UIAutomator reads accessibility node properties; on API 35+ Compose's
    // TextButton(enabled = false) does not reliably surface isEnabled=false on
    // the node that By.text("Save") resolves to.  We verify behavior instead:
    // clicking Save without the required field must keep us on the edit screen,
    // and clicking it after filling the field must save and navigate back.

    @Test
    fun spellEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        waitForDesc("Add Spell")
        device.findObject(By.desc("Add Spell")).click()
        waitForText("Save")

        device.findObject(By.text("Save")).click()
        assertNotNull("Still on edit screen — Save was disabled",
            device.findObject(By.text("Spell Name *")))

        device.findObject(By.text("Spell Name *")).text = "Magic Missile"
        device.wait(Until.hasObject(By.text("Save")), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Magic Missile in list confirms Save fired",
            waitForText("Magic Missile"))
    }

    @Test
    fun abilityEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        openTab("Abilities")
        waitForDesc("Add Ability")
        device.findObject(By.desc("Add Ability")).click()
        waitForText("Save")

        device.findObject(By.text("Save")).click()
        assertNotNull("Still on edit screen — Save was disabled",
            device.findObject(By.text("Ability Name *")))

        device.findObject(By.text("Ability Name *")).text = "Second Wind"
        device.wait(Until.hasObject(By.text("Save")), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Second Wind in list confirms Save fired",
            waitForText("Second Wind"))
    }

    @Test
    fun actionEdit_saveDisabledWithoutName_enabledAfterName() {
        openCharacter()
        openTab("Actions")
        waitForDesc("Add Action")
        device.findObject(By.desc("Add Action")).click()
        waitForText("Save")

        device.findObject(By.text("Save")).click()
        assertNotNull("Still on edit screen — Save was disabled",
            device.findObject(By.text("Action Name *")))

        device.findObject(By.text("Action Name *")).text = "Longsword Attack"
        device.wait(Until.hasObject(By.text("Save")), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Longsword Attack in list confirms Save fired",
            waitForText("Longsword Attack"))
    }

    @Test
    fun noteEdit_saveDisabledWithoutTitle_enabledAfterTitle() {
        openCharacter()
        openTab("Notes")
        waitForDesc("Add Note")
        device.findObject(By.desc("Add Note")).click()
        waitForText("Save")

        device.findObject(By.text("Save")).click()
        assertNotNull("Still on edit screen — Save was disabled",
            device.findObject(By.text("Title *")))

        device.findObject(By.text("Title *")).text = "Session notes"
        device.wait(Until.hasObject(By.text("Save")), timeout)
        device.findObject(By.text("Save")).click()
        assertNotNull("Session notes in list confirms Save fired",
            waitForText("Session notes"))
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
