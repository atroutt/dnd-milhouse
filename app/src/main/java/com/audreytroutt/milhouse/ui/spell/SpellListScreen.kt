package com.audreytroutt.milhouse.ui.spell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.audreytroutt.milhouse.MilhouseApplication
import com.audreytroutt.milhouse.data.model.Spell
import com.audreytroutt.milhouse.ui.components.FilterChipRow
import com.audreytroutt.milhouse.ui.components.LevelBadge
import com.audreytroutt.milhouse.ui.components.SearchField
import com.audreytroutt.milhouse.viewmodel.SpellFilter
import com.audreytroutt.milhouse.viewmodel.SpellViewModel

@Composable
fun SpellListScreen(
    contentPadding: PaddingValues,
    onNavigateToEdit: (Long?) -> Unit,
    viewModel: SpellViewModel = viewModel(
        factory = SpellViewModel.factory(
            (LocalContext.current.applicationContext as MilhouseApplication).spellRepository
        )
    )
) {
    val spells by viewModel.spells.collectAsState()
    val filter by viewModel.filter.collectAsState()

    val levelChips = buildList {
        add("All" to (filter.levelFilter == null && !filter.preparedOnly))
        add("Cantrips" to (filter.levelFilter == 0))
        (1..9).forEach { lvl -> add("Level $lvl" to (filter.levelFilter == lvl)) }
        add("Prepared" to filter.preparedOnly)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEdit(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Spell")
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(scaffoldPadding)
        ) {
            Text(
                text = "Spells",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (spells.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No spells yet. Tap + to add one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    }
}

@Composable
private fun SpellCard(
    spell: Spell,
    onClick: () -> Unit,
    onTogglePrepared: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(spell.name, style = MaterialTheme.typography.titleMedium)
                    LevelBadge(spell.level)
                    if (spell.isConcentration) {
                        Text("C", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary)
                    }
                    if (spell.isRitual) {
                        Text("R", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary)
                    }
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
