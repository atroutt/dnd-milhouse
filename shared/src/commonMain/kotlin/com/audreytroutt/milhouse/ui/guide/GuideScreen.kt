package com.audreytroutt.milhouse.ui.guide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("D&D Quick Guide") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            GuideSection(title = "Combat — The Action Budget") {
                GuideEntry(
                    heading = "Your turn",
                    body = "Every turn you have a budget of things you can do. You don't have to use all of them, but you can't go over."
                )
                BulletList(
                    items = listOf(
                        "Action — Your main contribution: Attack, Cast a Spell, Dash (extra movement), Disengage (move without provoking opportunity attacks), Help, or Hide.",
                        "Bonus Action — A quick extra. You can only do this if a specific spell or ability says it uses a Bonus Action.",
                        "Movement — Your speed (usually 30 ft). You can split this up before and after your Action.",
                        "Reaction — One per round, triggered by something else (e.g., an Opportunity Attack when an enemy runs away from you).",
                    )
                )
                GuideEntry(
                    heading = "Concentration",
                    body = "You can't hold two concentration spells at once — casting a second ends the first. If you take damage, make a Constitution saving throw (DC = 10 or half the damage taken, whichever is higher) or lose the spell."
                )
                GuideEntry(
                    heading = "Death Saving Throws",
                    body = "When at 0 HP, roll a d20 each turn — no modifiers. 10 or higher is a success; below 10 is a failure. Three successes = stable. Three failures = dead. A 1 counts as two failures; a 20 brings you back to 1 HP."
                )
            }

            GuideSection(title = "Spells — \"To Hit\" vs. \"Save DC\"") {
                GuideEntry(
                    heading = "How to tell which one a spell uses",
                    body = "Look at the spell description. It will say one of two things:"
                )
                GuideEntry(heading = "\"Make a Spell Attack\"", body = null)
                BulletList(
                    items = listOf(
                        "You roll: 1d20 + your Spell Attack Bonus.",
                        "You need to meet or beat the target's AC.",
                        "If you hit, roll damage. If you miss, usually nothing happens.",
                    )
                )
                GuideEntry(heading = "\"The target must make a [Stat] saving throw\"", body = null)
                BulletList(
                    items = listOf(
                        "You roll nothing — just tell the DM your Spell Save DC.",
                        "The DM rolls a d20 for the enemy; they must meet or beat your DC to resist.",
                        "If they fail: full damage or effect.",
                        "If they succeed: usually half damage, or no effect for conditions like Stunned.",
                    )
                )
                GuideEntry(
                    heading = "Can I cast two spells in one turn?",
                    body = "The Bonus Action Rule: if you cast a spell as a Bonus Action (like Healing Word), the only other spell you can cast that turn is a Cantrip with a casting time of 1 Action."
                )
            }

            GuideSection(title = "Leveling Up Checklist") {
                GuideEntry(heading = "Follow these steps in order", body = null)
                NumberedList(
                    items = listOf(
                        "Hit Points — Roll your Hit Die (or take the average) and add your Constitution modifier. Add that total to your max HP.",
                        "Proficiency Bonus — Check if your bonus increases (at levels 5, 9, 13, and 17). If it does, your Attack rolls and Skill bonuses all go up by 1.",
                        "Class Features — Look at your class table. Did you gain a new ability or a Subclass feature?",
                        "Ability Score Improvement (ASI) — Every few levels you can add +2 to one stat, +1 to two stats, or take a Feat.",
                        "Spells (if applicable) — Check if you gained new Spell Slots or the ability to learn or prepare new spells.",
                    )
                )
                GuideEntry(
                    heading = "The \"Invisible\" Updates",
                    body = "Don't forget to update your Passive Perception if your Proficiency Bonus or Wisdom modifier changed."
                )
            }

            GuideSection(title = "Roleplay") {
                GuideEntry(
                    heading = "What can I do with Inspiration?",
                    body = "If the DM gave you Inspiration for a cool moment, spend it to give yourself Advantage on one d20 roll. Use it or lose it!"
                )
                GuideEntry(
                    heading = "Can I use my turn to talk to the enemy?",
                    body = "Yes! Brief utterances are free. You can try to Persuade, Intimidate, or Deceive, but making a formal check usually takes your Action."
                )
                GuideEntry(
                    heading = "Beyond the numbers",
                    body = "If you want to move past just saying \"I attack the goblin,\" here are a few ways to get creative."
                )
                GuideEntry(heading = "1. Narrate the \"Fluff\"", body = "Describe the visual of your action instead of just stating the mechanic — it adds flavor without changing any rules.")
                BulletList(
                    items = listOf(
                        "The Finishing Blow — when you kill an enemy, describe it: \"I spin my staff low to sweep their legs, then bring the head of the weapon down for the final strike.\"",
                        "Spell Customization — what does your magic look like? Do your Magic Missiles look like glowing purple darts, or a swarm of translucent spectral bees?",
                        "The Near Miss — if an enemy misses your AC, describe how you avoided it. Did you parry with your shield, or did you barely lean aside and feel the wind of the blade?",
                    )
                )
                GuideEntry(heading = "2. Use the \"Yes, And...\" Rule", body = "Borrow from improv: accept what another player sets up and build on it.")
                BulletList(
                    items = listOf(
                        "Build Relationships — if another character does something cool, acknowledge it in character: \"Nice shot, Bard! I didn't know you had that in you.\"",
                        "Shared Backstories — connect your history to someone else at the table. Maybe you and the Paladin grew up in the same village, or the Rogue owes you 10 gold from a botched card game years ago.",
                    )
                )
                GuideEntry(heading = "3. Give Your Character a Quirk or Vice", body = "Flaws are often more fun to play than strengths. Pick a specific behavioral trait unrelated to stats.")
                BulletList(
                    items = listOf(
                        "The Nervous Habit — you polish your shield whenever the party stops to talk, or hum a specific tune whenever you're in a dark cave.",
                        "A Specific Motivation — instead of \"I want gold,\" your character is obsessed with finding rare tea leaves, or has a pathological need to be the one who opens every door.",
                        "Low-Stakes Superstitions — your character refuses to sleep in a room with an even number of windows, or believes seeing a black crow is good luck.",
                    )
                )
                GuideEntry(heading = "4. Think Outside the Box in Combat", body = "Combat doesn't have to be a standing duel. Use the environment with your Action.")
                BulletList(
                    items = listOf(
                        "Environmental Hazards — can you cut the rope holding up a chandelier? Can you kick a table over to create Half Cover for the wizard?",
                        "The Help Action — if you can't deal much damage, describe how you're distracting the boss to give the Rogue Advantage on their next hit.",
                    )
                )
                GuideEntry(heading = "5. Develop a Signature", body = "Give your character something they're known for — a trademark move, like a catchphrase in a show.")
                BulletList(
                    items = listOf(
                        "A Calling Card — you leave a specific coin on the eyes of fallen foes.",
                        "A Victory Pose — every time you succeed on a difficult check, you do a specific little victory dance or a stoic nod.",
                        "A Unique Voice or Mannerism — you don't need a professional accent. Changing your pitch, speaking slower, or using specific words (like \"perchance\" or \"mate\") makes the character feel distinct from yourself.",
                    )
                )
            }

            GuideSection(title = "Common Confusion") {
                GuideEntry(heading = "DC vs. AC — what's the difference?", body = null)
                BulletList(
                    items = listOf(
                        "AC (Armor Class) — the number you need to meet or beat to hit a creature with an attack.",
                        "DC (Difficulty Class) — the number you need to meet or beat on a saving throw or skill check.",
                    )
                )
                GuideEntry(heading = "Ability Check vs. Saving Throw", body = null)
                BulletList(
                    items = listOf(
                        "Ability Check (Active) — you are trying to do something to the world (e.g., kick down a door).",
                        "Saving Throw (Reactive) — the world is trying to do something to you (e.g., a fireball is exploding in your face; try to dodge).",
                    )
                )
                GuideEntry(
                    heading = "Natural 20 on a skill check — automatic success?",
                    body = "No. Critical Success only applies to Attack Rolls in the official rules. For skill checks, a 20 is just a high number — if the DC is 30 and your total doesn't reach it, you still fail. (Many DMs house-rule this.)"
                )
                GuideEntry(
                    heading = "What is Passive Perception?",
                    body = "It's your \"always-on\" awareness. If it's higher than a monster's Stealth roll, you notice them without even trying to look."
                )
                GuideEntry(
                    heading = "What does \"Proficient\" mean?",
                    body = "You've had training in that skill or tool. You add your Proficiency Bonus (+2, +3, etc.) to that roll. Without proficiency, you just roll d20 + your basic Ability Modifier."
                )
            }

            GuideSection(title = "Mechanics Cheat Sheet") {
                GuideEntry(
                    heading = "Advantage / Disadvantage",
                    body = "Roll two d20s. Take the highest for Advantage, the lowest for Disadvantage. They don't stack — multiple sources of Advantage plus one Disadvantage is still a straight roll."
                )
                GuideEntry(
                    heading = "Resistance",
                    body = "If you have resistance to a damage type (like Fire), you take half damage from it."
                )
                GuideEntry(heading = "Cover", body = null)
                BulletList(
                    items = listOf(
                        "Half Cover — +2 to AC and Dexterity saving throws.",
                        "Three-Quarters Cover — +5 to AC and Dexterity saving throws.",
                        "Total Cover — you cannot be targeted by a direct attack at all.",
                    )
                )
                GuideEntry(heading = "Resting", body = null)
                BulletList(
                    items = listOf(
                        "Short Rest (1 hour) — Spend Hit Dice to heal.",
                        "Long Rest (8 hours) — Regain all HP and half your total Hit Dice (rounded up).",
                    )
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GuideSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        HorizontalDivider()
        content()
    }
}

@Composable
private fun GuideEntry(heading: String, body: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(heading, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        if (body != null) {
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("•", style = MaterialTheme.typography.bodyMedium)
                Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NumberedList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEachIndexed { index, item ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${index + 1}.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}
