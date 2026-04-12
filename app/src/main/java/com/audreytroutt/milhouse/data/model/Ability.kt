package com.audreytroutt.milhouse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "abilities")
data class Ability(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val characterId: Long,
    val name: String,
    val category: String,
    val description: String,
    val usesMax: Int = 0,
    val usesRemaining: Int = 0,
    val rechargeOn: String = "None",
    val isPassive: Boolean = false
)

val ABILITY_CATEGORIES = listOf(
    "Class Feature", "Subclass Feature", "Racial Trait", "Feat", "Background Feature", "Other"
)

val RECHARGE_OPTIONS = listOf("None", "Short Rest", "Long Rest", "Dawn")
