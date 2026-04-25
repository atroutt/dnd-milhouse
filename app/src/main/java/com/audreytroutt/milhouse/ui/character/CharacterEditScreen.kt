package com.audreytroutt.milhouse.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.audreytroutt.milhouse.MilhouseApplication
import com.audreytroutt.milhouse.data.model.CHARACTER_COLORS
import com.audreytroutt.milhouse.data.model.CHARACTER_ICONS
import com.audreytroutt.milhouse.data.model.DND_CLASSES
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.SPECIES_NAMES
import com.audreytroutt.milhouse.data.model.SpeciesTrait
import com.audreytroutt.milhouse.data.model.speciesTraits
import com.audreytroutt.milhouse.ui.components.DropdownField
import com.audreytroutt.milhouse.ui.components.SectionLabel
import com.audreytroutt.milhouse.viewmodel.CharacterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditScreen(
    characterId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: CharacterViewModel = viewModel(
        factory = CharacterViewModel.factory(
            (LocalContext.current.applicationContext as MilhouseApplication).characterRepository,
            (LocalContext.current.applicationContext as MilhouseApplication).abilityRepository,
            (LocalContext.current.applicationContext as MilhouseApplication).actionRepository
        )
    )
) {
    val app = LocalContext.current.applicationContext as MilhouseApplication
    var existingCharacter by remember { mutableStateOf<DndCharacter?>(null) }
    var loaded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var characterClass by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var speciesExpanded by remember { mutableStateOf(false) }
    var colorIndex by remember { mutableStateOf(if (characterId == null) CHARACTER_COLORS.indices.random() else 0) }
    var iconIndex by remember { mutableStateOf(if (characterId == null) CHARACTER_ICONS.indices.random() else 0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val speciesSuggestions = remember(species) {
        if (species.isBlank()) emptyList()
        else SPECIES_NAMES.filter { it.contains(species, ignoreCase = true) }
    }

    LaunchedEffect(characterId) {
        if (characterId != null) {
            val c = app.characterRepository.getById(characterId)
            existingCharacter = c
            if (c != null && !loaded) {
                name = c.name
                characterClass = c.characterClass
                species = c.species
                colorIndex = c.colorIndex
                iconIndex = c.iconIndex
                loaded = true
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Character") },
            text = { Text("Delete \"$name\"? All of their spells, abilities, actions, and notes will also be permanently deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    existingCharacter?.let { viewModel.deleteCharacter(it) }
                    onNavigateBack()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (characterId == null) "New Character" else "Edit Character") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (characterId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(
                        onClick = {
                            val character = DndCharacter(
                                id = existingCharacter?.id ?: 0L,
                                name = name.trim(),
                                characterClass = characterClass.trim(),
                                species = species.trim(),
                                colorIndex = colorIndex,
                                iconIndex = iconIndex
                            )
                            val traits: List<SpeciesTrait> = if (existingCharacter == null) speciesTraits(species.trim()) else emptyList()
                            viewModel.saveCharacter(character, traits)
                            onNavigateBack()
                        },
                        enabled = name.isNotBlank()
                    ) { Text("Save") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live avatar preview
            val previewColor = CHARACTER_COLORS[colorIndex.coerceIn(CHARACTER_COLORS.indices)]
            val previewIcon = CHARACTER_ICONS[iconIndex.coerceIn(CHARACTER_ICONS.indices)]
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(previewColor)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(previewIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            DropdownField(
                label = "Class",
                value = characterClass.ifBlank { "Select class" },
                options = DND_CLASSES,
                onValueChange = { characterClass = it }
            )

            ExposedDropdownMenuBox(
                expanded = speciesExpanded && speciesSuggestions.isNotEmpty(),
                onExpandedChange = { speciesExpanded = it }
            ) {
                OutlinedTextField(
                    value = species,
                    onValueChange = {
                        species = it
                        speciesExpanded = true
                    },
                    label = { Text("Species") },
                    placeholder = { Text("e.g. Human, Elf, Dwarf…") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                ExposedDropdownMenu(
                    expanded = speciesExpanded && speciesSuggestions.isNotEmpty(),
                    onDismissRequest = { speciesExpanded = false }
                ) {
                    speciesSuggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion) },
                            onClick = {
                                species = suggestion
                                speciesExpanded = false
                            }
                        )
                    }
                }
            }

            SectionLabel("Color")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                itemsIndexed(CHARACTER_COLORS) { index, color ->
                    ColorSwatch(
                        color = color,
                        selected = index == colorIndex,
                        onClick = { colorIndex = index }
                    )
                }
            }

            SectionLabel("Icon")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                itemsIndexed(CHARACTER_ICONS) { index, icon ->
                    val swatchColor = CHARACTER_COLORS[colorIndex.coerceIn(CHARACTER_COLORS.indices)]
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (index == iconIndex) swatchColor else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (index == iconIndex) 2.dp else 0.dp,
                                color = if (index == iconIndex) swatchColor else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { iconIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (index == iconIndex) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
