package com.audreytroutt.milhouse.ui.ability

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.audreytroutt.milhouse.MilhouseApplication
import com.audreytroutt.milhouse.data.model.ABILITY_CATEGORIES
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.RECHARGE_OPTIONS
import com.audreytroutt.milhouse.ui.components.DropdownField
import com.audreytroutt.milhouse.ui.components.SectionLabel
import com.audreytroutt.milhouse.viewmodel.AbilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbilityEditScreen(
    characterId: Long,
    abilityId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AbilityViewModel = viewModel(
        key = "ability-$characterId",
        factory = AbilityViewModel.factory(
            (LocalContext.current.applicationContext as MilhouseApplication).abilityRepository,
            characterId
        )
    )
) {
    val existingAbility by viewModel.editAbility.collectAsState()
    var loaded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ABILITY_CATEGORIES.first()) }
    var description by remember { mutableStateOf("") }
    var isPassive by remember { mutableStateOf(false) }
    var usesMax by remember { mutableStateOf("0") }
    var usesRemaining by remember { mutableStateOf("0") }
    var rechargeOn by remember { mutableStateOf(RECHARGE_OPTIONS.first()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(abilityId) {
        if (abilityId != null) viewModel.loadAbility(abilityId)
    }

    LaunchedEffect(existingAbility) {
        val ability = existingAbility
        if (!loaded && ability != null && abilityId != null) {
            name = ability.name
            category = ability.category
            description = ability.description
            isPassive = ability.isPassive
            usesMax = ability.usesMax.toString()
            usesRemaining = ability.usesRemaining.toString()
            rechargeOn = ability.rechargeOn
            loaded = true
        }
    }

    DisposableEffect(Unit) { onDispose { viewModel.clearEditAbility() } }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Ability") },
            text = { Text("Delete \"$name\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    existingAbility?.let { viewModel.deleteAbility(it) }
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
                title = { Text(if (abilityId == null) "New Ability" else "Edit Ability") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (abilityId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(
                        onClick = {
                            val ability = Ability(
                                id = existingAbility?.id ?: 0L,
                                characterId = characterId,
                                name = name.trim(),
                                category = category,
                                description = description.trim(),
                                isPassive = isPassive,
                                usesMax = if (isPassive) 0 else usesMax.toIntOrNull() ?: 0,
                                usesRemaining = if (isPassive) 0 else usesRemaining.toIntOrNull() ?: 0,
                                rechargeOn = if (isPassive) "None" else rechargeOn
                            )
                            viewModel.saveAbility(ability)
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
                label = { Text("Ability Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            DropdownField(
                label = "Category",
                value = category,
                options = ABILITY_CATEGORIES,
                onValueChange = { category = it }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            SectionLabel("Usage")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Passive", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = isPassive, onCheckedChange = { isPassive = it })
            }

            if (!isPassive) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = usesMax,
                        onValueChange = { usesMax = it.filter(Char::isDigit) },
                        label = { Text("Max Uses") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = usesRemaining,
                        onValueChange = { usesRemaining = it.filter(Char::isDigit) },
                        label = { Text("Remaining") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                DropdownField(
                    label = "Recharge On",
                    value = rechargeOn,
                    options = RECHARGE_OPTIONS,
                    onValueChange = { rechargeOn = it }
                )
            }
        }
    }
}
