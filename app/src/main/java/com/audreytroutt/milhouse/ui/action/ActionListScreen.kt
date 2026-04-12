package com.audreytroutt.milhouse.ui.action

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
import com.audreytroutt.milhouse.data.model.ACTION_TYPES
import com.audreytroutt.milhouse.data.model.DndAction
import com.audreytroutt.milhouse.ui.components.ActionTypeBadge
import com.audreytroutt.milhouse.ui.components.FilterChipRow
import com.audreytroutt.milhouse.ui.components.SearchField
import com.audreytroutt.milhouse.viewmodel.ActionViewModel

@Composable
fun ActionListScreen(
    contentPadding: PaddingValues,
    onNavigateToEdit: (Long?) -> Unit,
    viewModel: ActionViewModel = viewModel(
        factory = ActionViewModel.factory(
            (LocalContext.current.applicationContext as MilhouseApplication).actionRepository
        )
    )
) {
    val actions by viewModel.actions.collectAsState()
    val query by viewModel.query.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()

    val chips = buildList {
        add("All" to (typeFilter == null))
        ACTION_TYPES.forEach { type -> add(type to (typeFilter == type)) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEdit(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Action")
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
                text = "Actions",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SearchField(
                query = query,
                onQueryChange = viewModel::setQuery,
                placeholder = "Search actions...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            FilterChipRow(
                chips = chips,
                onChipClick = { label ->
                    if (label == "All") viewModel.setTypeFilter(null)
                    else viewModel.setTypeFilter(if (typeFilter == label) null else label)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (actions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No actions yet. Tap + to add one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(actions, key = { it.id }) { action ->
                        ActionCard(action = action, onClick = { onNavigateToEdit(action.id) })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(action: DndAction, onClick: () -> Unit) {
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
                Text(action.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                ActionTypeBadge(action.actionType)
            }
            if (action.damage.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    buildString {
                        append(action.damage)
                        if (action.damageType.isNotBlank()) append(" ${action.damageType}")
                        if (action.toHit.isNotBlank()) append(" · +${action.toHit} to hit")
                        if (action.range.isNotBlank()) append(" · ${action.range}")
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
