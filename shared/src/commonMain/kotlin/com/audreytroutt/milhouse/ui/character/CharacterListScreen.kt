package com.audreytroutt.milhouse.ui.character

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audreytroutt.milhouse.data.model.DndCharacter
import com.audreytroutt.milhouse.data.model.accentColor
import com.audreytroutt.milhouse.data.model.avatarIcon
import com.audreytroutt.milhouse.shared.generated.resources.Res
import com.audreytroutt.milhouse.shared.generated.resources.ic_launcher
import com.audreytroutt.milhouse.shared.generated.resources.milhouse_photo
import com.audreytroutt.milhouse.viewmodel.CharacterViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterListScreen(
    onSelectCharacter: (Long) -> Unit,
    onEditCharacter: (Long?) -> Unit,
    viewModel: CharacterViewModel = koinViewModel()
) {
    val characters by viewModel.characters.collectAsState()

    var iconTapCount by remember { mutableIntStateOf(0) }
    var showEasterEgg by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Characters") },
                actions = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_launcher),
                        contentDescription = "Milhouse",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                iconTapCount++
                                if (iconTapCount >= 5) {
                                    showEasterEgg = true
                                    iconTapCount = 0
                                }
                            }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEditCharacter(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Character")
            }
        }
    ) { padding ->
        if (characters.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No characters yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onEditCharacter(null) }) {
                        Text("Create First Character")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(characters, key = { it.id }) { character ->
                    CharacterCard(
                        character = character,
                        onClick = { onSelectCharacter(character.id) },
                        onEdit = { onEditCharacter(character.id) }
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showEasterEgg,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { showEasterEgg = false },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(Res.drawable.milhouse_photo),
                    contentDescription = "The real Milhouse",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
                Text(
                    "The real Milhouse 🐱",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Tap anywhere to dismiss",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CharacterCard(
    character: DndCharacter,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    val color = character.accentColor()
    val icon = character.avatarIcon()

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    character.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                val subtitle = listOf(character.characterClass, character.species)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit character",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
