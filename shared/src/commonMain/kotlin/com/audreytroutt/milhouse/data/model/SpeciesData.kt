package com.audreytroutt.milhouse.data.model

data class SpeciesTrait(val name: String, val description: String = "")
data class SpeciesEntry(val name: String, val traits: List<SpeciesTrait>)

val SPECIES_DATA = listOf(
    SpeciesEntry("Aasimar", listOf(
        SpeciesTrait("Celestial Resistance", "Resistance to necrotic and radiant damage."),
        SpeciesTrait("Darkvision", "You can see in dim light within 60 feet of you as if it were bright light."),
        SpeciesTrait("Healing Hands", "As an action, you can touch a creature and restore hit points."),
        SpeciesTrait("Light Bearer", "You know the light cantrip."),
        SpeciesTrait("Celestial Revelation", "Transform to unleash the spark within your soul."),
    )),
    SpeciesEntry("Boggart", listOf(
        SpeciesTrait("Goblinoid", "Your creature type is Goblinoid."),
        SpeciesTrait("Darkvision", "Accustomed to life in shadow."),
        SpeciesTrait("Fey Ancestry", "You have advantage on saving throws you make to avoid or end the charmed condition."),
        SpeciesTrait("Fury of the Small", "When you damage a creature larger than you, you can cause extra damage."),
        SpeciesTrait("Nimble Escape", "You can take the Disengage or Hide action as a bonus action."),
    )),
    SpeciesEntry("Dhampir", listOf(
        SpeciesTrait("Darkvision", "Vision adapted to the night."),
        SpeciesTrait("Spider Climb", "Movement across walls and ceilings."),
        SpeciesTrait("Trace of Undeath", "A lingering connection to the dead."),
        SpeciesTrait("Vampiric Bite", "A lethal fanged attack that feeds the hunger."),
    )),
    SpeciesEntry("Dragonborn", listOf(
        SpeciesTrait("Dragon Ancestry", "Determines the breath weapon and damage resistance type."),
        SpeciesTrait("Breath Weapon", "Exhale a burst of energy based on ancestry."),
        SpeciesTrait("Damage Resistance", "Inherent protection against an energy type."),
        SpeciesTrait("Darkvision", "Sharp sight in low-light conditions."),
        SpeciesTrait("Draconic Flight", "The ability to soar as their ancestors did."),
    )),
    SpeciesEntry("Dwarf", listOf(
        SpeciesTrait("Darkvision", "Standard vision in lightless environments."),
        SpeciesTrait("Dwarven Resilience", "Hardiness against physical or environmental tolls."),
        SpeciesTrait("Dwarven Toughness", "Increased durability and hit points."),
        SpeciesTrait("Stonecunning", "Intuitive knowledge regarding stonework."),
    )),
    SpeciesEntry("Elf", listOf(
        SpeciesTrait("Darkvision", "Vision in dim light and darkness."),
        SpeciesTrait("Elven Lineage", "Specific heritage and magical ancestry."),
        SpeciesTrait("Fey Ancestry", "Protection against being charmed and magic sleep."),
        SpeciesTrait("Keen Senses", "Proficiency in the Perception skill."),
        SpeciesTrait("Trance", "Meditative state instead of sleep."),
    )),
    SpeciesEntry("Faerie", listOf(
        SpeciesTrait("Fey", "Classified as a fey creature."),
        SpeciesTrait("Faerie Magic", "Innate magical abilities like pranks and illusions."),
        SpeciesTrait("Flight", "Ability to fly using wings."),
    )),
    SpeciesEntry("Flamekin", listOf(
        SpeciesTrait("Darkvision", "Vision in dim or lightless environments."),
        SpeciesTrait("Fire Resistance", "Inherent protection against fire damage."),
        SpeciesTrait("Reach to the Blaze", "Innate fire-related magic."),
    )),
    SpeciesEntry("Gnome", listOf(
        SpeciesTrait("Darkvision", "Standard vision in subterranean or dark areas."),
        SpeciesTrait("Gnomish Cunning", "Advantage on mental saving throws against magic."),
        SpeciesTrait("Gnomish Lineage", "Specific sub-heritage and innate talents."),
    )),
    SpeciesEntry("Goliath", listOf(
        SpeciesTrait("Giant Ancestry", "Specific heritage providing unique defensive abilities."),
        SpeciesTrait("Large Form", "Ability to briefly increase size and strength."),
        SpeciesTrait("Powerful Build", "Count as one size larger for carrying capacity and pushing/lifting."),
    )),
    SpeciesEntry("Halfling", listOf(
        SpeciesTrait("Brave", "Advantage on saves against being frightened."),
        SpeciesTrait("Halfling Nimbleness", "Can move through the space of larger creatures."),
        SpeciesTrait("Luck", "Reroll a d20 roll of 1."),
        SpeciesTrait("Naturally Stealthy", "Can hide behind larger creatures."),
    )),
    SpeciesEntry("Harengon", listOf(
        SpeciesTrait("Hare-Trigger", "You can add your proficiency bonus to your initiative rolls."),
        SpeciesTrait("Leporine Senses", "You have proficiency in the Perception skill."),
        SpeciesTrait("Lucky Footwork", "When you fail a Dexterity saving throw, you can use your reaction to add a d4 to the roll."),
        SpeciesTrait("Rabbit Hop", "As a bonus action, you can jump a distance in feet equal to 5 times your proficiency bonus."),
    )),
    SpeciesEntry("Human", listOf(
        SpeciesTrait("Resourceful", "Innate ability to find solutions in difficult situations."),
        SpeciesTrait("Skillful", "Gains proficiency in a skill of choice."),
        SpeciesTrait("Versatile", "Ability to adapt to various challenges and roles."),
    )),
    SpeciesEntry("Kalashtar", listOf(
        SpeciesTrait("Dual Mind", "You have advantage on all Wisdom saving throws."),
        SpeciesTrait("Mental Discipline", "You have resistance to psychic damage."),
        SpeciesTrait("Mindlink", "You can speak telepathically to any creature you can see within 60 feet."),
        SpeciesTrait("Severed from Dreams", "Kalashtar don't dream and are immune to spells that require you to dream."),
    )),
    SpeciesEntry("Kenku", listOf(
        SpeciesTrait("Expert Duplication", "When you copy writing or craftwork produced by yourself or someone else, you have advantage on any ability checks you make to produce an exact duplicate."),
        SpeciesTrait("Kenku Recall", "You have proficiency in two skills of your choice. Additionally, when you make an ability check using any skill in which you have proficiency, you can give yourself advantage on the check. You can use this advantage a number of times equal to your proficiency bonus, regaining all uses after a long rest."),
        SpeciesTrait("Mimicry", "You can accurately mimic sounds you have heard, including voices. A creature that hears the sounds can tell they are imitations only with a successful Wisdom (Insight) check against a DC of 8 + your proficiency bonus + your Charisma modifier."),
    )),
    SpeciesEntry("Kithkin", listOf(
        SpeciesTrait("Brave", "You have advantage on saving throws you make to avoid or end the frightened condition."),
        SpeciesTrait("Kithkin Nimbleness", "You can move through the space of any creature that is of a size larger than yours."),
        SpeciesTrait("Luck", "When you roll a 1 on the d20 for an attack roll, ability check, or saving throw, you can reroll the die."),
        SpeciesTrait("Naturally Stealthy", "You can attempt to hide even when you are obscured only by a creature that is larger than you."),
    )),
    SpeciesEntry("Orc", listOf(
        SpeciesTrait("Adrenaline Rush", "A sudden burst of speed or energy."),
        SpeciesTrait("Darkvision", "Aided vision in subterranean or night settings."),
        SpeciesTrait("Relentless Endurance", "The ability to withstand a lethal blow."),
    )),
    SpeciesEntry("Tiefling", listOf(
        SpeciesTrait("Darkvision", "Vision in dim light and darkness."),
        SpeciesTrait("Fiendish Legacy", "Determines specific innate magical abilities."),
        SpeciesTrait("Otherworldly Presence", "Manifestation of their fiendish heritage in social or magical ways."),
    )),
    SpeciesEntry("Warforged", listOf(
        SpeciesTrait("Construct Resilience", "You have advantage on saving throws against being poisoned and have resistance to poison damage."),
        SpeciesTrait("Integrated Protection", "Your body has built-in defensive layers."),
        SpeciesTrait("Sentry's Rest", "When you take a long rest, you spend at least 6 hours in an inactive, motionless state."),
        SpeciesTrait("Specialized Design", "You gain one skill proficiency and one tool proficiency of your choice."),
        SpeciesTrait("Tireless", "You don't need to eat, drink, or breathe."),
    )),
)

val SPECIES_NAMES: List<String> = SPECIES_DATA.map { it.name }

fun speciesTraits(speciesName: String): List<SpeciesTrait> =
    SPECIES_DATA.find { it.name.equals(speciesName, ignoreCase = true) }?.traits ?: emptyList()
