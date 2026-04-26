package com.audreytroutt.milhouse.ui.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.audreytroutt.milhouse.data.model.Note
import com.audreytroutt.milhouse.ui.components.SearchField
import com.audreytroutt.milhouse.util.formatDate
import com.audreytroutt.milhouse.viewmodel.NoteViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NoteListScreen(
    characterId: Long,
    contentPadding: PaddingValues,
    onNavigateToEdit: (Long?) -> Unit,
    viewModel: NoteViewModel = koinViewModel(key = "note-$characterId") { parametersOf(characterId) }
) {
    val notes by viewModel.notes.collectAsState()
    val query by viewModel.query.collectAsState()
    val tagFilter by viewModel.tagFilter.collectAsState()
    val allTags by viewModel.allTags.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SearchField(
                query = query,
                onQueryChange = viewModel::setQuery,
                placeholder = "Search notes...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            if (allTags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        FilterChip(
                            selected = tagFilter == null,
                            onClick = { viewModel.setTagFilter(null) },
                            label = { Text("All") }
                        )
                    }
                    items(allTags) { tag ->
                        FilterChip(
                            selected = tagFilter == tag,
                            onClick = { viewModel.setTagFilter(if (tagFilter == tag) null else tag) },
                            label = { Text(tag) }
                        )
                    }
                }
            }
            if (notes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notes yet. Tap + to add one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(note = note, onClick = { onNavigateToEdit(note.id) })
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { onNavigateToEdit(null) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Note")
        }
    }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(note.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(
                    formatDate(note.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                note.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (note.tagList().isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    note.tagList().take(4).forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
