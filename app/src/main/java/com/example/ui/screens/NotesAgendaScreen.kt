package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.MazoumBottomBar
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumTopBar
import com.example.ui.model.Screen
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCardSurface
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumElectricBlue
import com.example.ui.theme.MazoumError
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumSuccess
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary
import java.util.Locale

@Composable
fun NotesAgendaScreen(
    notes: List<NoteEntity>,
    tasks: List<TaskEntity>,
    onAddNote: (title: String, content: String, category: String) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onAddTask: (title: String, description: String, dueDate: String, priority: String, category: String) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onNavigate: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state for new note
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }
    var newNoteCategory by remember { mutableStateOf("Général") }

    // Dialog state for new task
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDueDate by remember { mutableStateOf("Aujourd'hui") }
    var newTaskPriority by remember { mutableStateOf("Normale") }

    // Speech to text for dictating note
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                newNoteContent = if (newNoteContent.isBlank()) spoken else "$newNoteContent $spoken"
            }
        }
    }

    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Notes & Organisation",
                onBackClick = { onNavigate("home") },
                onSettingsClick = { onNavigate("settings") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddNoteDialog = true else showAddTaskDialog = true
                },
                containerColor = MazoumCyan,
                contentColor = Color(0xFF001524),
                shape = CircleShape,
                modifier = Modifier.testTag("notes_agenda_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter")
            }
        },
        containerColor = MazoumNight,
        modifier = Modifier.statusBarsPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MazoumDarkSurface,
                contentColor = MazoumCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MazoumCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Notes & Dictées (${notes.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Tâches & Agenda (${tasks.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (selectedTab == 0) "Rechercher une note..." else "Rechercher une tâche...",
                        color = MazoumTextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MazoumCyan)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MazoumCyan,
                    unfocusedBorderColor = MazoumCardBorder,
                    focusedTextColor = MazoumTextPrimary,
                    unfocusedTextColor = MazoumTextPrimary,
                    focusedContainerColor = MazoumDarkSurface,
                    unfocusedContainerColor = MazoumDarkSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Content
            if (selectedTab == 0) {
                val filteredNotes = notes.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
                }

                if (filteredNotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune note trouvée. Touchez le bouton '+' pour en rédiger ou en dicter une !",
                            color = MazoumTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredNotes, key = { it.id }) { note ->
                            MazoumCard {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MazoumCyan
                                            )
                                        )
                                        IconButton(
                                            onClick = { onDeleteNote(note) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Supprimer",
                                                tint = MazoumTextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = note.content,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MazoumTextPrimary,
                                            lineHeight = 19.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Catégorie : ${note.category}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MazoumTextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                val filteredTasks = tasks.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }

                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune tâche enregistrée. Touchez '+' pour programmer votre prochaine action !",
                            color = MazoumTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            MazoumCard(
                                borderColor = if (task.isCompleted) MazoumCardBorder.copy(alpha = 0.3f) else MazoumCardBorder
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { onToggleTask(task) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MazoumCyan,
                                            uncheckedColor = MazoumTextMuted
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (task.isCompleted) MazoumTextMuted else MazoumTextPrimary
                                            )
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Échéance : ${task.dueDate}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MazoumTextSecondary
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = task.priority,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = when (task.priority) {
                                                        "Urgente" -> MazoumError
                                                        "Haute" -> Color(0xFFF59E0B)
                                                        else -> MazoumCyan
                                                    },
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteTask(task) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = MazoumTextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Note Dialog
        if (showAddNoteDialog) {
            AlertDialog(
                onDismissRequest = { showAddNoteDialog = false },
                containerColor = MazoumDarkSurface,
                title = {
                    Text(text = "Nouvelle Note ou Dictée", color = MazoumTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newNoteTitle,
                            onValueChange = { newNoteTitle = it },
                            placeholder = { Text("Titre de la note", color = MazoumTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MazoumCyan,
                                unfocusedBorderColor = MazoumCardBorder,
                                focusedTextColor = MazoumTextPrimary,
                                unfocusedTextColor = MazoumTextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = newNoteContent,
                            onValueChange = { newNoteContent = it },
                            placeholder = { Text("Contenu ou dictez à voix haute...", color = MazoumTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MazoumCyan,
                                unfocusedBorderColor = MazoumCardBorder,
                                focusedTextColor = MazoumTextPrimary,
                                unfocusedTextColor = MazoumTextPrimary
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MazoumElectricBlue.copy(alpha = 0.2f))
                                .clickable {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH.toString())
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictez le contenu...")
                                    }
                                    try {
                                        speechLauncher.launch(intent)
                                    } catch (e: Exception) {}
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = MazoumCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Activer la dictée vocale", color = MazoumCyan, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newNoteTitle.isNotBlank() || newNoteContent.isNotBlank()) {
                                onAddNote(
                                    if (newNoteTitle.isBlank()) "Note sans titre" else newNoteTitle,
                                    newNoteContent,
                                    newNoteCategory
                                )
                                newNoteTitle = ""
                                newNoteContent = ""
                                showAddNoteDialog = false
                            }
                        }
                    ) {
                        Text("Enregistrer", color = MazoumCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddNoteDialog = false }) {
                        Text("Annuler", color = MazoumTextSecondary)
                    }
                }
            )
        }

        // Add Task Dialog
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                containerColor = MazoumDarkSurface,
                title = {
                    Text(text = "Nouvelle Tâche & Rappel", color = MazoumTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            placeholder = { Text("Titre de la tâche", color = MazoumTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MazoumCyan,
                                unfocusedBorderColor = MazoumCardBorder,
                                focusedTextColor = MazoumTextPrimary,
                                unfocusedTextColor = MazoumTextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = newTaskDueDate,
                            onValueChange = { newTaskDueDate = it },
                            placeholder = { Text("Échéance (ex: Demain 14h, Vendredi)", color = MazoumTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MazoumCyan,
                                unfocusedBorderColor = MazoumCardBorder,
                                focusedTextColor = MazoumTextPrimary,
                                unfocusedTextColor = MazoumTextPrimary
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Normale", "Haute", "Urgente").forEach { prio ->
                                val isSelected = newTaskPriority == prio
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MazoumCyan.copy(alpha = 0.2f) else MazoumNight)
                                        .border(1.dp, if (isSelected) MazoumCyan else MazoumCardBorder, RoundedCornerShape(8.dp))
                                        .clickable { newTaskPriority = prio }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = prio,
                                        fontSize = 11.sp,
                                        color = if (isSelected) MazoumCyan else MazoumTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                onAddTask(newTaskTitle, "", newTaskDueDate, newTaskPriority, "Personnel")
                                newTaskTitle = ""
                                showAddTaskDialog = false
                            }
                        }
                    ) {
                        Text("Ajouter", color = MazoumCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) {
                        Text("Annuler", color = MazoumTextSecondary)
                    }
                }
            )
        }
    }
}
