package com.audreytroutt.milhouse.data.model

data class DndAction(
    val id: Long = 0,
    val characterId: Long = 0,
    val name: String,
    val actionType: String,
    val description: String,
    val damage: String = "",
    val damageType: String = "",
    val toHit: String = "",
    val range: String = "",
    val savingThrow: String = ""
)

val ACTION_TYPES = listOf(
    "Action", "Bonus Action", "Reaction", "Free Action",
    "Legendary Action", "Lair Action"
)

val DAMAGE_TYPES = listOf(
    "Acid", "Bludgeoning", "Cold", "Fire", "Force", "Lightning",
    "Necrotic", "Piercing", "Poison", "Psychic", "Radiant",
    "Slashing", "Thunder", ""
)
