package com.audreytroutt.milhouse.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Spell(
    val id: Long = 0,
    val characterId: Long = 0,
    val name: String,
    val level: Int,
    val school: String,
    val castingTime: String,
    val range: String,
    val duration: String,
    val components: String,
    val materialComponents: String = "",
    val description: String,
    val higherLevels: String = "",
    val classes: String = "",
    val isConcentration: Boolean = false,
    val isRitual: Boolean = false,
    val isPrepared: Boolean = false
)

val SPELL_SCHOOLS = listOf(
    "Abjuration", "Conjuration", "Divination", "Enchantment",
    "Evocation", "Illusion", "Necromancy", "Transmutation"
)
