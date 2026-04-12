package com.audreytroutt.milhouse.data.model

data class StandardAction(val name: String, val type: String, val description: String)

val STANDARD_ACTIONS = listOf(
    StandardAction("Attack", "Action", "Make one melee or ranged attack. If you have Extra Attack, you make multiple attacks with this action."),
    StandardAction("Dash", "Action", "Gain extra movement for the current turn equal to your speed after applying any modifiers."),
    StandardAction("Disengage", "Action", "Your movement doesn't provoke opportunity attacks for the rest of the turn."),
    StandardAction("Dodge", "Action", "Until the start of your next turn, any attack roll made against you has disadvantage if you can see the attacker, and you make Dexterity saving throws with advantage."),
    StandardAction("Help", "Action", "Give an ally advantage on their next ability check or attack roll before the start of your next turn."),
    StandardAction("Hide", "Action", "Make a Dexterity (Stealth) check in an attempt to hide."),
    StandardAction("Ready", "Action", "Define a trigger and a corresponding reaction to take when that trigger occurs."),
    StandardAction("Search", "Action", "Devote your attention to finding something. Depending on the nature of your search, the DM might have you make a Wisdom (Perception) check or an Intelligence (Investigation) check."),
    StandardAction("Use an Object", "Action", "Use an object, such as drinking a potion, using a healer's kit, or interacting with a complex mechanism."),
    StandardAction("Off-hand Attack", "Bonus Action", "When you take the Attack action with a light melee weapon, you can use a bonus action to attack with a different light melee weapon in your other hand."),
)
