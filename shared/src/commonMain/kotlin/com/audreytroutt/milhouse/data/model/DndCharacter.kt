package com.audreytroutt.milhouse.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DndCharacter(
    val id: Long = 0,
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
    Icons.Default.Casino,
    Icons.Default.AutoAwesome,
    Icons.Default.Whatshot,
    Icons.Default.Spa,
    Icons.Default.SelfImprovement,
    Icons.Default.Psychology,
    Icons.Default.Science,
    Icons.Default.AcUnit,
    Icons.Default.Cyclone,
    Icons.Default.Anchor,
    Icons.Default.Shield,
    Icons.Default.Bolt,
    Icons.Default.Pets,
    Icons.Default.Flare,
    Icons.Default.Favorite,
    Icons.Default.Flight,
)

fun DndCharacter.accentColor(): Color =
    CHARACTER_COLORS[colorIndex.coerceIn(CHARACTER_COLORS.indices)]

fun DndCharacter.avatarIcon(): ImageVector =
    CHARACTER_ICONS[iconIndex.coerceIn(CHARACTER_ICONS.indices)]
