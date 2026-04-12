package com.audreytroutt.milhouse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Note
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
import com.audreytroutt.milhouse.ui.ability.AbilityEditScreen
import com.audreytroutt.milhouse.ui.ability.AbilityListScreen
import com.audreytroutt.milhouse.ui.action.ActionEditScreen
import com.audreytroutt.milhouse.ui.action.ActionListScreen
import com.audreytroutt.milhouse.ui.note.NoteEditScreen
import com.audreytroutt.milhouse.ui.note.NoteListScreen
import com.audreytroutt.milhouse.ui.spell.SpellEditScreen
import com.audreytroutt.milhouse.ui.spell.SpellListScreen

sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    object Spells : BottomTab("spells", "Spells", Icons.Default.AutoStories)
    object Abilities : BottomTab("abilities", "Abilities", Icons.Default.Shield)
    object Actions : BottomTab("actions", "Actions", Icons.Default.Bolt)
    object Notes : BottomTab("notes", "Notes", Icons.Default.Note)
}

val bottomTabs = listOf(
    BottomTab.Spells,
    BottomTab.Abilities,
    BottomTab.Actions,
    BottomTab.Notes
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomTabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Spells.route
        ) {
            // Spells
            composable(BottomTab.Spells.route) {
                SpellListScreen(
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) navController.navigate("spell/new")
                        else navController.navigate("spell/$id")
                    }
                )
            }
            composable("spell/new") {
                SpellEditScreen(spellId = null, onNavigateBack = { navController.popBackStack() })
            }
            composable(
                "spell/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                SpellEditScreen(
                    spellId = backStackEntry.arguments?.getLong("id"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Abilities
            composable(BottomTab.Abilities.route) {
                AbilityListScreen(
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) navController.navigate("ability/new")
                        else navController.navigate("ability/$id")
                    }
                )
            }
            composable("ability/new") {
                AbilityEditScreen(abilityId = null, onNavigateBack = { navController.popBackStack() })
            }
            composable(
                "ability/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                AbilityEditScreen(
                    abilityId = backStackEntry.arguments?.getLong("id"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Actions
            composable(BottomTab.Actions.route) {
                ActionListScreen(
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) navController.navigate("action/new")
                        else navController.navigate("action/$id")
                    }
                )
            }
            composable("action/new") {
                ActionEditScreen(actionId = null, onNavigateBack = { navController.popBackStack() })
            }
            composable(
                "action/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                ActionEditScreen(
                    actionId = backStackEntry.arguments?.getLong("id"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Notes
            composable(BottomTab.Notes.route) {
                NoteListScreen(
                    contentPadding = innerPadding,
                    onNavigateToEdit = { id ->
                        if (id == null) navController.navigate("note/new")
                        else navController.navigate("note/$id")
                    }
                )
            }
            composable("note/new") {
                NoteEditScreen(noteId = null, onNavigateBack = { navController.popBackStack() })
            }
            composable(
                "note/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                NoteEditScreen(
                    noteId = backStackEntry.arguments?.getLong("id"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
