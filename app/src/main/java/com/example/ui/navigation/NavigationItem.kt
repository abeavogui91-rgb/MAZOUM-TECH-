package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    data object Home : NavigationItem("home", "Accueil", Icons.Default.Home, "bottom_nav_home")
    data object Assistant : NavigationItem("assistant", "Assistant IA", Icons.Default.AutoAwesome, "bottom_nav_assistant")
    data object Vision : NavigationItem("vision", "Vision", Icons.Default.Visibility, "bottom_nav_vision")
    data object Studio : NavigationItem("studio", "Studio", Icons.Default.Palette, "bottom_nav_studio")
    data object Settings : NavigationItem("settings", "Paramètres", Icons.Default.Settings, "bottom_nav_settings")

    companion object {
        val bottomNavItems: List<NavigationItem>
            get() = listOf(Home, Assistant, Vision, Studio, Settings)
    }
}

object SubRoutes {
    const val WELCOME = "welcome"
    const val AUTH = "auth"
    const val VOICE = "voice"
    const val NOTES_AGENDA = "notes_agenda"
    const val MEMORY = "memory"
    const val HISTORY = "history"
    const val DEVICES = "devices"
}
