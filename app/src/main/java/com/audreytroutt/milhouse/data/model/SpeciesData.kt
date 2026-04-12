package com.audreytroutt.milhouse.data.model

data class SpeciesEntry(val name: String, val traits: List<String>)

val SPECIES_DATA = listOf(
    SpeciesEntry("Aasimar", listOf("Celestial Resistance", "Darkvision", "Healing Hands", "Light Bearer", "Celestial Revelation")),
    SpeciesEntry("Boggart", listOf("Goblinoid", "Darkvision", "Fey Ancestry", "Fury of the Small", "Nimble Escape")),
    SpeciesEntry("Dhampir", listOf("Darkvision", "Spider Climb", "Trace of Undeath", "Vampiric Bite")),
    SpeciesEntry("Dragonborn", listOf("Dragon Ancestry", "Breath Weapon", "Damage Resistance", "Darkvision", "Draconic Flight")),
    SpeciesEntry("Dwarf", listOf("Darkvision", "Dwarven Resilience", "Dwarven Toughness", "Stonecunning")),
    SpeciesEntry("Elf", listOf("Darkvision", "Elven Lineage", "Fey Ancestry", "Keen Senses", "Trance")),
    SpeciesEntry("Faerie", listOf("Fey", "Faerie Magic", "Flight")),
    SpeciesEntry("Flamekin", listOf("Darkvision", "Fire Resistance", "Reach to the Blaze")),
    SpeciesEntry("Gnome", listOf("Darkvision", "Gnomish Cunning", "Gnomish Lineage")),
    SpeciesEntry("Goliath", listOf("Giant Ancestry", "Large Form", "Powerful Build")),
    SpeciesEntry("Halfling", listOf("Brave", "Halfling Nimbleness", "Luck", "Naturally Stealthy")),
    SpeciesEntry("Human", listOf("Resourceful", "Skillful", "Versatile")),
    SpeciesEntry("Orc", listOf("Adrenaline Rush", "Darkvision", "Relentless Endurance")),
    SpeciesEntry("Tiefling", listOf("Darkvision", "Fiendish Legacy", "Otherworldly Presence")),
    SpeciesEntry("Warforged", listOf("Construct Resilience", "Integrated Protection", "Sentry's Rest", "Specialized Design", "Tireless")),
)

val SPECIES_NAMES: List<String> = SPECIES_DATA.map { it.name }

fun speciesTraits(speciesName: String): List<String> =
    SPECIES_DATA.find { it.name.equals(speciesName, ignoreCase = true) }?.traits ?: emptyList()
