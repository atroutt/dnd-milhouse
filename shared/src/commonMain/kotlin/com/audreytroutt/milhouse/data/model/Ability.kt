package com.audreytroutt.milhouse.data.model

data class Ability(
    val id: Long = 0,
    val characterId: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val usesMax: Int = 0,
    val usesRemaining: Int = 0,
    val rechargeOn: String = "None",
    val isPassive: Boolean = false
)

val ABILITY_CATEGORIES = listOf(
    "Class Feature", "Subclass Feature", "Species Trait", "Feat", "Background Feature", "Other"
)

val RECHARGE_OPTIONS = listOf("None", "Short Rest", "Long Rest", "Dawn")
