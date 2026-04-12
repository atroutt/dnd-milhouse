package com.audreytroutt.milhouse.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
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
    Icons.Default.Shield,               // Fighter / Paladin
    Icons.Default.AutoStories,          // Wizard / Scholar
    Icons.Default.Bolt,                 // Sorcerer
    Icons.Default.Star,                 // Paladin / Holy
    Icons.Default.Favorite,             // Cleric / Life
    Icons.Default.LocalFireDepartment,  // Barbarian
    Icons.Default.MusicNote,            // Bard
    Icons.Default.Visibility,           // Warlock / Rogue
    Icons.Default.Pets,                 // Ranger / Druid
    Icons.Default.FlashOn,              // Monk
)

fun DndCharacter.accentColor(): Color =
    CHARACTER_COLORS[colorIndex.coerceIn(CHARACTER_COLORS.indices)]

fun DndCharacter.avatarIcon(): ImageVector =
    CHARACTER_ICONS[iconIndex.coerceIn(CHARACTER_ICONS.indices)]
