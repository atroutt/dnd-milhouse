package com.audreytroutt.milhouse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import com.audreytroutt.milhouse.ui.ability.AbilityEditScreen
import com.audreytroutt.milhouse.ui.ability.AbilityListScreen
import com.audreytroutt.milhouse.ui.action.ActionEditScreen
import com.audreytroutt.milhouse.ui.action.ActionListScreen
import com.audreytroutt.milhouse.ui.character.CharacterEditScreen
import com.audreytroutt.milhouse.ui.character.CharacterListScreen
import com.audreytroutt.milhouse.ui.guide.GuideScreen
import com.audreytroutt.milhouse.ui.note.NoteEditScreen
import com.audreytroutt.milhouse.ui.note.NoteListScreen
import com.audreytroutt.milhouse.ui.spell.SpellEditScreen
import com.audreytroutt.milhouse.ui.spell.SpellListScreen
import org.koin.compose.koinInject

sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    object Spells : BottomTab("spells", "Spells", Icons.Default.AutoStories)
    object Abilities : BottomTab("abilities", "Abilities", Icons.Default.Shield)
    object Actions : BottomTab("actions", "Actions", Icons.Default.Bolt)
    object Notes : BottomTab("notes", "Notes", Icons.Default.Note)
}

private val bottomTabs = listOf(
    BottomTab.Spells,
    BottomTab.Abilities,
    BottomTab.Actions,
    BottomTab.Notes
)

@Composable
fun AppNavigation() {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = "characters") {
        composable("characters") {
            CharacterListScreen(
                onSelectCharacter = { characterId -> rootNavController.navigate("character/$characterId") },
                onEditCharacter = { characterId ->
                    if (characterId == null) rootNavController.navigate("characters/new")
                    else rootNavController.navigate("characters/$characterId/edit")
                }
            )
        }
        composable("characters/new") {
            CharacterEditScreen(
                characterId = null,
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }
        composable(
            "characters/{characterId}/edit",
            arguments = listOf(navArgument("characterId") { type = NavType.LongType })
        ) { backStack ->
            CharacterEditScreen(
                characterId = backStack.arguments?.getLong("characterId"),
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }

        composable(
            "character/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.LongType })
        ) { backStack ->
            val characterId = backStack.arguments!!.getLong("characterId")
            CharacterTabs(
                characterId = characterId,
                onNavigateToCharacters = { rootNavController.popBackStack() },
                onNavigateToGuide = { rootNavController.navigate("guide") }
            )
        }

        composable("guide") {
            GuideScreen(onNavigateBack = { rootNavController.popBackStack() })
        }
    }
}

private val editRoutes = setOf(
    "spell/new", "spell/{id}",
    "ability/new", "ability/{id}",
    "action/new", "action/{id}",
    "note/new", "note/{id}"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterTabs(
    characterId: Long,
    onNavigateToCharacters: () -> Unit,
    onNavigateToGuide: () -> Unit
) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isEditRoute = currentDestination?.route in editRoutes

    val currentTab = bottomTabs.find { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: BottomTab.Spells

    val characterRepository: CharacterRepository = koinInject()
    var characterName by remember { mutableStateOf("") }
    LaunchedEffect(characterId) {
        characterName = characterRepository.getById(characterId)?.name ?: ""
    }

    Scaffold(
        topBar = {
            if (!isEditRoute) {
                TopAppBar(
                    title = { Text("$characterName — ${currentTab.label}") },
                    actions = {
                        IconButton(onClick = onNavigateToGuide) {
                            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "D&D Guide")
                        }
                        TextButton(onClick = onNavigateToCharacters) {
                            Icon(Icons.Default.PeopleAlt, contentDescription = null)
                            Text("Switch")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!isEditRoute) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                tabNavController.navigate(tab.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = tabNavController, startDestination = BottomTab.Spells.route) {
            composable(BottomTab.Spells.route) {
                SpellListScreen(
                    characterId = characterId,
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) tabNavController.navigate("spell/new")
                        else tabNavController.navigate("spell/$id")
                    }
                )
            }
            composable("spell/new") {
                SpellEditScreen(characterId = characterId, spellId = null, onNavigateBack = { tabNavController.popBackStack() })
            }
            composable("spell/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                SpellEditScreen(characterId = characterId, spellId = entry.arguments?.getLong("id"), onNavigateBack = { tabNavController.popBackStack() })
            }

            composable(BottomTab.Abilities.route) {
                AbilityListScreen(
                    characterId = characterId,
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) tabNavController.navigate("ability/new")
                        else tabNavController.navigate("ability/$id")
                    }
                )
            }
            composable("ability/new") {
                AbilityEditScreen(characterId = characterId, abilityId = null, onNavigateBack = { tabNavController.popBackStack() })
            }
            composable("ability/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                AbilityEditScreen(characterId = characterId, abilityId = entry.arguments?.getLong("id"), onNavigateBack = { tabNavController.popBackStack() })
            }

            composable(BottomTab.Actions.route) {
                ActionListScreen(
                    characterId = characterId,
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) tabNavController.navigate("action/new")
                        else tabNavController.navigate("action/$id")
                    }
                )
            }
            composable("action/new") {
                ActionEditScreen(characterId = characterId, actionId = null, onNavigateBack = { tabNavController.popBackStack() })
            }
            composable("action/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                ActionEditScreen(characterId = characterId, actionId = entry.arguments?.getLong("id"), onNavigateBack = { tabNavController.popBackStack() })
            }

            composable(BottomTab.Notes.route) {
                NoteListScreen(
                    characterId = characterId,
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) tabNavController.navigate("note/new")
                        else tabNavController.navigate("note/$id")
                    }
                )
            }
            composable("note/new") {
                NoteEditScreen(characterId = characterId, noteId = null, onNavigateBack = { tabNavController.popBackStack() })
            }
            composable("note/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                NoteEditScreen(characterId = characterId, noteId = entry.arguments?.getLong("id"), onNavigateBack = { tabNavController.popBackStack() })
            }
        }
    }
}
