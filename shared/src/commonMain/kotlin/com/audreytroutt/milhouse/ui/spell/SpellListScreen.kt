package com.audreytroutt.milhouse.ui.spell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.ui.components.FilterChipRow
import com.audreytroutt.milhouse.ui.components.LevelBadge
import com.audreytroutt.milhouse.ui.components.SearchField
import com.audreytroutt.milhouse.viewmodel.ImportState
import com.audreytroutt.milhouse.viewmodel.SpellViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SpellListScreen(
    characterId: Long,
    contentPadding: PaddingValues,
    onNavigateToEdit: (Long?) -> Unit,
    viewModel: SpellViewModel = koinViewModel(key = "spell-$characterId") { parametersOf(characterId) }
) {
    val spells by viewModel.spells.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val importState by viewModel.importState.collectAsState()
    val allClasses by viewModel.allClasses.collectAsState()
    val totalSpellCount by viewModel.totalSpellCount.collectAsState()

    val levelChips = buildList {
        add("Prepared" to filter.preparedOnly)
        add("All" to (filter.levelFilter == null && !filter.preparedOnly))
        add("Cantrips" to (filter.levelFilter == 0))
        (1..9).forEach { lvl -> add("Level $lvl" to (filter.levelFilter == lvl)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Spells",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (val state = importState) {
                is ImportState.Loading -> {
                    val progress = if (state.total > 0) state.fetched.toFloat() / state.total else 0f
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Text(
                            text = if (state.total > 0) "Importing SRD spells… ${state.fetched} / ${state.total}" else "Fetching spell list…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        if (state.total > 0) {
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        } else {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                is ImportState.Done -> {
                    Text(
                        text = "${spells.size} SRD spells imported.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                is ImportState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Import failed: ${state.message}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { viewModel.dismissImportError() }) { Text("Dismiss") }
                        }
                    }
                }
                else -> {}
            }

            SearchField(
                query = filter.query,
                onQueryChange = viewModel::setQuery,
                placeholder = "Search spells...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            FilterChipRow(
                chips = levelChips,
                onChipClick = { label ->
                    when (label) {
                        "All" -> { viewModel.setLevelFilter(null); viewModel.setPreparedOnly(false) }
                        "Cantrips" -> {
                            viewModel.setLevelFilter(if (filter.levelFilter == 0) null else 0)
                            viewModel.setPreparedOnly(false)
                        }
                        "Prepared" -> { viewModel.setLevelFilter(null); viewModel.setPreparedOnly(!filter.preparedOnly) }
                        else -> {
                            val lvl = label.removePrefix("Level ").toIntOrNull()
                            viewModel.setLevelFilter(if (filter.levelFilter == lvl) null else lvl)
                            viewModel.setPreparedOnly(false)
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (allClasses.isNotEmpty()) {
                val classChips = buildList {
                    add("All Classes" to (filter.classFilter == null))
                    allClasses.forEach { cls -> add(cls to (filter.classFilter == cls)) }
                }
                FilterChipRow(
                    chips = classChips,
                    onChipClick = { label ->
                        if (label == "All Classes") viewModel.setClassFilter(null)
                        else viewModel.setClassFilter(if (filter.classFilter == label) null else label)
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (spells.isEmpty() && totalSpellCount == 0 && importState == ImportState.Idle) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No spells yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.importSrdSpells() }) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Import SRD Spells")
                        }
                        Text(
                            "Downloads ~319 spells from dnd5eapi.co",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else if (spells.isEmpty() && filter.preparedOnly && importState == ImportState.Idle) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No prepared spells.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tap the bookmark icon on any spell to prepare it.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (spells.isEmpty() && importState is ImportState.Loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(spells, key = { it.id }) { spell ->
                        SpellCard(
                            spell = spell,
                            onClick = { onNavigateToEdit(spell.id) },
                            onTogglePrepared = { viewModel.togglePrepared(spell) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onNavigateToEdit(null) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Spell")
        }
    }
}

@Composable
private fun SpellCard(spell: Spell, onClick: () -> Unit, onTogglePrepared: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(spell.name, style = MaterialTheme.typography.titleMedium)
                    LevelBadge(spell.level)
                    if (spell.isConcentration) Text("C", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    if (spell.isRitual) Text("R", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "${spell.school} · ${spell.castingTime} · ${spell.range}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    spell.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onTogglePrepared) {
                Icon(
                    imageVector = if (spell.isPrepared) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (spell.isPrepared) "Mark unprepared" else "Mark prepared",
                    tint = if (spell.isPrepared) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
