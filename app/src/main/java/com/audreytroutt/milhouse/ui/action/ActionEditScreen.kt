package com.audreytroutt.milhouse.ui.action

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.audreytroutt.milhouse.MilhouseApplication
import com.audreytroutt.milhouse.data.model.ACTION_TYPES
import com.audreytroutt.milhouse.data.model.DAMAGE_TYPES
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.ui.components.DropdownField
import com.audreytroutt.milhouse.ui.components.SectionLabel
import com.audreytroutt.milhouse.viewmodel.ActionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionEditScreen(
    characterId: Long,
    actionId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: ActionViewModel = viewModel(
        key = "action-$characterId",
        factory = ActionViewModel.factory(
            (LocalContext.current.applicationContext as MilhouseApplication).actionRepository,
            characterId
        )
    )
) {
    val existingAction by viewModel.editAction.collectAsState()
    var loaded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf(ACTION_TYPES.first()) }
    var description by remember { mutableStateOf("") }
    var damage by remember { mutableStateOf("") }
    var damageType by remember { mutableStateOf("") }
    var toHit by remember { mutableStateOf("") }
    var range by remember { mutableStateOf("") }
    var savingThrow by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionId) {
        if (actionId != null) viewModel.loadAction(actionId)
    }

    LaunchedEffect(existingAction) {
        val action = existingAction
        if (!loaded && action != null && actionId != null) {
            name = action.name
            actionType = action.actionType
            description = action.description
            damage = action.damage
            damageType = action.damageType
            toHit = action.toHit
            range = action.range
            savingThrow = action.savingThrow
            loaded = true
        }
    }

    DisposableEffect(Unit) { onDispose { viewModel.clearEditAction() } }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Action") },
            text = { Text("Delete \"$name\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    existingAction?.let { viewModel.deleteAction(it) }
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
                title = { Text(if (actionId == null) "New Action" else "Edit Action") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (actionId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(
                        onClick = {
                            val action = DndAction(
                                id = existingAction?.id ?: 0L,
                                characterId = characterId,
                                name = name.trim(),
                                actionType = actionType,
                                description = description.trim(),
                                damage = damage.trim(),
                                damageType = damageType,
                                toHit = toHit.trim(),
                                range = range.trim(),
                                savingThrow = savingThrow.trim()
                            )
                            viewModel.saveAction(action)
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
                label = { Text("Action Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            DropdownField(
                label = "Action Type",
                value = actionType,
                options = ACTION_TYPES,
                onValueChange = { actionType = it }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            SectionLabel("Attack / Damage (optional)")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = damage,
                    onValueChange = { damage = it },
                    label = { Text("Damage Dice") },
                    placeholder = { Text("e.g. 2d6") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                DropdownField(
                    label = "Damage Type",
                    value = damageType.ifEmpty { "—" },
                    options = DAMAGE_TYPES.map { it.ifEmpty { "—" } },
                    onValueChange = { damageType = if (it == "—") "" else it },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = toHit,
                    onValueChange = { toHit = it },
                    label = { Text("To Hit Bonus") },
                    placeholder = { Text("e.g. +5") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = range,
                    onValueChange = { range = it },
                    label = { Text("Range") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = savingThrow,
                onValueChange = { savingThrow = it },
                label = { Text("Saving Throw") },
                placeholder = { Text("e.g. DC 14 DEX") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
