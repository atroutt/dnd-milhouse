package com.audreytroutt.milhouse.ui.spell

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.audreytroutt.milhouse.data.model.SPELL_SCHOOLS
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.ui.components.DropdownField
import com.audreytroutt.milhouse.ui.components.SectionLabel
import com.audreytroutt.milhouse.viewmodel.SpellViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellEditScreen(
    characterId: Long,
    spellId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: SpellViewModel = koinViewModel(key = "spell-$characterId") { parametersOf(characterId) }
) {
    val existingSpell by viewModel.editSpell.collectAsState()
    var loaded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("0") }
    var school by remember { mutableStateOf(SPELL_SCHOOLS.first()) }
    var castingTime by remember { mutableStateOf("1 action") }
    var range by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var components by remember { mutableStateOf("V, S") }
    var materialComponents by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var higherLevels by remember { mutableStateOf("") }
    var classes by remember { mutableStateOf("") }
    var isConcentration by remember { mutableStateOf(false) }
    var isRitual by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(spellId) {
        if (spellId != null) viewModel.loadSpell(spellId)
    }

    LaunchedEffect(existingSpell) {
        val spell = existingSpell
        if (!loaded && spell != null && spellId != null) {
            name = spell.name
            level = spell.level.toString()
            school = spell.school
            castingTime = spell.castingTime
            range = spell.range
            duration = spell.duration
            components = spell.components
            materialComponents = spell.materialComponents
            description = spell.description
            higherLevels = spell.higherLevels
            classes = spell.classes
            isConcentration = spell.isConcentration
            isRitual = spell.isRitual
            isPrepared = spell.isPrepared
            loaded = true
        }
    }

    DisposableEffect(Unit) { onDispose { viewModel.clearEditSpell() } }

    val isNewSpell = spellId == null

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Spell") },
            text = { Text("Delete \"$name\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    existingSpell?.let { viewModel.deleteSpell(it) }
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
                title = { Text(if (isNewSpell) "New Spell" else "Edit Spell") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isNewSpell) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(
                        onClick = {
                            val lvl = level.toIntOrNull()?.coerceIn(0, 9) ?: 0
                            val spell = Spell(
                                id = existingSpell?.id ?: 0L,
                                characterId = characterId,
                                name = name.trim(),
                                level = lvl,
                                school = school,
                                castingTime = castingTime.trim(),
                                range = range.trim(),
                                duration = duration.trim(),
                                components = components.trim(),
                                materialComponents = materialComponents.trim(),
                                description = description.trim(),
                                higherLevels = higherLevels.trim(),
                                classes = classes.trim(),
                                isConcentration = isConcentration,
                                isRitual = isRitual,
                                isPrepared = isPrepared
                            )
                            viewModel.saveSpell(spell)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Spell Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = level,
                    onValueChange = { if (it.length <= 1 && it.all(Char::isDigit)) level = it },
                    label = { Text("Level (0-9)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                DropdownField(
                    label = "School",
                    value = school,
                    options = SPELL_SCHOOLS,
                    onValueChange = { school = it },
                    modifier = Modifier.weight(2f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = castingTime,
                    onValueChange = { castingTime = it },
                    label = { Text("Casting Time") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                OutlinedTextField(
                    value = range,
                    onValueChange = { range = it },
                    label = { Text("Range") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                OutlinedTextField(
                    value = components,
                    onValueChange = { components = it },
                    label = { Text("Components") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            }

            OutlinedTextField(
                value = materialComponents,
                onValueChange = { materialComponents = it },
                label = { Text("Material Components") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = classes,
                onValueChange = { classes = it },
                label = { Text("Classes") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Wizard, Sorcerer") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = higherLevels,
                onValueChange = { higherLevels = it },
                label = { Text("At Higher Levels") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            SectionLabel("Properties")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LabeledSwitch("Concentration", isConcentration) { isConcentration = it }
                LabeledSwitch("Ritual", isRitual) { isRitual = it }
                LabeledSwitch("Prepared", isPrepared) { isPrepared = it }
            }
        }
    }
}

@Composable
private fun LabeledSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
