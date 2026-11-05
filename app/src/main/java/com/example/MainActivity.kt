package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.MazoumBottomBar
import com.example.ui.navigation.NavigationItem
import com.example.ui.navigation.SubRoutes
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.NotesAgendaScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.screens.VoiceScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTechTheme
import com.example.ui.viewmodel.MazoumViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory App Check configuration with intent extra for AI Logic
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MazoumTechTheme {
                MazoumApp()
            }
        }
    }
}

@Composable
fun MazoumApp(
    viewModel: MazoumViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationItem.Home.route

    val userName by viewModel.userName.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val isMemoryEnabled by viewModel.isMemoryEnabled.collectAsState()
    val isVoiceFeedbackEnabled by viewModel.isVoiceFeedbackEnabled.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()

    val notes by viewModel.notes.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val history by viewModel.history.collectAsState()
    val devices by viewModel.devices.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()

    val voiceStatus by viewModel.voiceStatus.collectAsState()
    val voiceResponse by viewModel.voiceResponse.collectAsState()

    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val visionResult by viewModel.visionResult.collectAsState()
    val isVisionLoading by viewModel.isVisionLoading.collectAsState()
    val visionAnalysisType by viewModel.visionAnalysisType.collectAsState()

    val studioTool by viewModel.studioTool.collectAsState()
    val studioResult by viewModel.studioResult.collectAsState()
    val isStudioLoading by viewModel.isStudioLoading.collectAsState()

    // 5 primary top-level routes matching the Bottom Navigation Bar:
    // Home, AI Assistant, Vision, Studio, Settings
    val primaryBottomNavRoutes = setOf(
        NavigationItem.Home.route,
        NavigationItem.Assistant.route,
        NavigationItem.Vision.route,
        NavigationItem.Studio.route,
        NavigationItem.Settings.route
    )
    val shouldShowBottomBar = currentRoute in primaryBottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MazoumNight,
        bottomBar = {
            if (shouldShowBottomBar) {
                MazoumBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SubRoutes.WELCOME) {
                WelcomeScreen(
                    onStartClick = { navController.navigate(SubRoutes.AUTH) }
                )
            }

            composable(SubRoutes.AUTH) {
                AuthScreen(
                    currentName = userName,
                    currentRole = userRole,
                    onSaveProfile = { name, role ->
                        viewModel.updateProfile(name, role)
                        navController.navigate(NavigationItem.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavigationItem.Home.route) {
                DashboardScreen(
                    userName = userName,
                    userRole = userRole,
                    tasks = tasks,
                    notes = notes,
                    onToggleTask = { viewModel.toggleTask(it) },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(NavigationItem.Assistant.route) {
                ChatScreen(
                    messages = chatMessages,
                    isLoading = isChatLoading,
                    voiceManager = viewModel.voiceManager,
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onSaveAsNote = { title, content -> viewModel.addNote(title, content, "Assistant IA") },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(NavigationItem.Vision.route) {
                VisionScreen(
                    selectedBitmap = selectedBitmap,
                    visionResult = visionResult,
                    isLoading = isVisionLoading,
                    analysisType = visionAnalysisType,
                    voiceManager = viewModel.voiceManager,
                    onSelectImage = { viewModel.setSelectedImage(it) },
                    onSetAnalysisType = { viewModel.setVisionAnalysisType(it) },
                    onAnalyze = { query -> viewModel.analyzeSelectedImage(query) },
                    onSaveAsNote = { title, content -> viewModel.addNote(title, content, "Vision IA") },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(NavigationItem.Studio.route) {
                StudioScreen(
                    currentTool = studioTool,
                    studioResult = studioResult,
                    isLoading = isStudioLoading,
                    voiceManager = viewModel.voiceManager,
                    onSetTool = { viewModel.setStudioTool(it) },
                    onGenerate = { topic, platform, tone, details ->
                        viewModel.generateStudioContent(topic, platform, tone, details)
                    },
                    onSaveAsNote = { title, content -> viewModel.addNote(title, content, "Studio IA") },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(NavigationItem.Settings.route) {
                SettingsScreen(
                    userName = userName,
                    userRole = userRole,
                    isVoiceFeedbackEnabled = isVoiceFeedbackEnabled,
                    voiceSpeed = voiceSpeed,
                    onToggleVoiceFeedback = { viewModel.toggleVoiceFeedback(it) },
                    onUpdateVoiceSpeed = { viewModel.updateVoiceSpeed(it) },
                    onResetAllData = { viewModel.resetAllData() },
                    onEditProfile = { navController.navigate(SubRoutes.AUTH) },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(SubRoutes.VOICE) {
                VoiceScreen(
                    voiceStatus = voiceStatus,
                    voiceResponse = voiceResponse,
                    voiceManager = viewModel.voiceManager,
                    onVoiceQuery = { viewModel.processVoiceQuery(it) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(SubRoutes.NOTES_AGENDA) {
                NotesAgendaScreen(
                    notes = notes,
                    tasks = tasks,
                    onAddNote = { title, content, cat -> viewModel.addNote(title, content, cat) },
                    onDeleteNote = { viewModel.deleteNote(it) },
                    onAddTask = { title, desc, due, prio, cat -> viewModel.addTask(title, desc, due, prio, cat) },
                    onToggleTask = { viewModel.toggleTask(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(SubRoutes.MEMORY) {
                MemoryScreen(
                    isMemoryEnabled = isMemoryEnabled,
                    memories = memories,
                    onToggleMemory = { viewModel.toggleMemory(it) },
                    onAddMemory = { cat, content -> viewModel.addMemoryItem(cat, content) },
                    onDeleteMemory = { viewModel.deleteMemoryItem(it) },
                    onClearAllMemory = { viewModel.clearAllMemory() },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(SubRoutes.HISTORY) {
                HistoryScreen(
                    history = history,
                    onDeleteHistoryItem = { viewModel.deleteHistory(it) },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(SubRoutes.DEVICES) {
                DevicesScreen(
                    devices = devices,
                    onToggleDevice = { viewModel.toggleDevice(it) },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        }
    }
}
