package com.audreytroutt.milhouse.ui.ability

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.audreytroutt.milhouse.MilhouseApplication
import com.audreytroutt.milhouse.data.model.Ability
import com.audreytroutt.milhouse.data.model.ABILITY_CATEGORIES
import com.audreytroutt.milhouse.ui.components.FilterChipRow
import com.audreytroutt.milhouse.ui.components.SearchField
import com.audreytroutt.milhouse.viewmodel.AbilityViewModel

@Composable
fun AbilityListScreen(
    contentPadding: PaddingValues,
    onNavigateToEdit: (Long?) -> Unit,
    viewModel: AbilityViewModel = viewModel(
        factory = AbilityViewModel.factory(
            (LocalContext.current.applicationContext as MilhouseApplication).abilityRepository
        )
    )
) {
    val abilities by viewModel.abilities.collectAsState()
    val query by viewModel.query.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()

    val chips = buildList {
        add("All" to (categoryFilter == null))
        ABILITY_CATEGORIES.forEach { cat -> add(cat to (categoryFilter == cat)) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEdit(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Ability")
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
                text = "Abilities",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SearchField(
                query = query,
                onQueryChange = viewModel::setQuery,
                placeholder = "Search abilities...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            FilterChipRow(
                chips = chips,
                onChipClick = { label ->
                    if (label == "All") viewModel.setCategoryFilter(null)
                    else viewModel.setCategoryFilter(if (categoryFilter == label) null else label)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (abilities.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No abilities yet. Tap + to add one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(abilities, key = { it.id }) { ability ->
                        AbilityCard(ability = ability, onClick = { onNavigateToEdit(ability.id) })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AbilityCard(ability: Ability, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(ability.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        if (ability.isPassive) "Passive" else "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                ability.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                ability.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!ability.isPassive && ability.usesMax > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Uses: ${ability.usesRemaining}/${ability.usesMax} · Recharge: ${ability.rechargeOn}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
