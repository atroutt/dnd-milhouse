package com.audreytroutt.milhouse.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class DndCharacter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val characterClass: String = "",
    val species: String = "",
    val colorIndex: Int = 0,
    val iconIndex: Int = 0
)

val DND_CLASSES = listOf(
    "Artificer", "Barbarian", "Bard", "Blood Hunter", "Captain",
    "Cleric", "Druid", "Fighter", "Gunslinger", "Illrigger",
    "Monk", "Monster Hunter", "Paladin", "Pugilist", "Ranger",
    "Rogue", "Sorcerer", "Warlock", "Wizard"
)

val CHARACTER_COLORS = listOf(
    Color(0xFFE53935), // Red
    Color(0xFFFF6D00), // Deep Orange
    Color(0xFFF9A825), // Amber
    Color(0xFF388E3C), // Green
    Color(0xFF00838F), // Teal
    Color(0xFF1565C0), // Blue
    Color(0xFF4527A0), // Deep Purple
    Color(0xFFAD1457), // Pink
    Color(0xFF4E342E), // Brown
    Color(0xFF546E7A), // Blue Grey
)

val CHARACTER_ICONS: List<ImageVector> = listOf(
    Icons.Default.Casino,           // dice — classic D&D energy
    Icons.Default.AutoAwesome,      // sparkles — Wizard / Sorcerer
    Icons.Default.Whatshot,         // fire — Barbarian / Pyromancer
    Icons.Default.Spa,              // lotus — Druid / Ranger
    Icons.Default.SelfImprovement,  // meditation — Monk
    Icons.Default.Psychology,       // eldritch brain — Warlock / Psion
    Icons.Default.Science,          // alchemy flask — Artificer
    Icons.Default.AcUnit,           // snowflake — ice magic / weird
    Icons.Default.Cyclone,          // storm vortex — Tempest / Chaos
    Icons.Default.Anchor,           // anchor — chaotic neutral energy
)

fun DndCharacter.accentColor(): Color =
    CHARACTER_COLORS[colorIndex.coerceIn(CHARACTER_COLORS.indices)]

fun DndCharacter.avatarIcon(): ImageVector =
    CHARACTER_ICONS[iconIndex.coerceIn(CHARACTER_ICONS.indices)]
