package com.audreytroutt.milhouse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spells")
data class Spell(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @androidx.room.ColumnInfo(defaultValue = "0") val characterId: Long = 0,
    val name: String,
    val level: Int, // 0 = cantrip
    val school: String,
    val castingTime: String,
    val range: String,
    val duration: String,
    val components: String, // e.g. "V, S, M"
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
