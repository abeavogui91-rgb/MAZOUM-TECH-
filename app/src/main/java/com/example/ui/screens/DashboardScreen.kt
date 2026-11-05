package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumSuccess
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary

@Composable
fun DashboardScreen(
    userName: String,
    userRole: String,
    tasks: List<TaskEntity>,
    notes: List<NoteEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Tableau de Bord",
                onSettingsClick = { onNavigate("settings") },
                onVoiceClick = { onNavigate("voice") }
            )
        },
        containerColor = MazoumNight,
        modifier = Modifier.statusBarsPadding()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Welcome Header Card
            item {
                MazoumCard(borderColor = MazoumCyan.copy(alpha = 0.4f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MazoumElectricBlue.copy(alpha = 0.15f), MazoumNeonPurple.copy(alpha = 0.15f))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Bonjour, $userName 👋",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = MazoumTextPrimary
                                        )
                                    )
                                    Text(
                                        text = userRole,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MazoumCyan,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(MazoumSuccess.copy(alpha = 0.15f))
                                        .border(1.dp, MazoumSuccess.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(MazoumSuccess)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "IA EN LIGNE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MazoumSuccess,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "« Votre intelligence numérique, partout avec vous. »",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MazoumTextSecondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                        }
                    }
                }
            }

            // Quick Voice Launch Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(MazoumElectricBlue, MazoumNeonPurple)
                            )
                        )
                        .clickable { onNavigate("voice") }
                        .padding(16.dp)
                        .testTag("dashboard_voice_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Micro",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Assistant Vocal Immersif",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Touchez pour dialoguer oralement",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            // Core Features Grid
            item {
                Text(
                    text = "Modules Intelligents",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardModuleCard(
                            title = "Chat IA",
                            subtitle = "Dialogue & Synthèse",
                            icon = Icons.Default.Chat,
                            iconColor = MazoumCyan,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("assistant") },
                            testTag = "dash_chat_card"
                        )
                        DashboardModuleCard(
                            title = "Vision IA",
                            subtitle = "OCR, Erreurs & Docs",
                            icon = Icons.Default.Visibility,
                            iconColor = MazoumNeonPurple,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("vision") },
                            testTag = "dash_vision_card"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardModuleCard(
                            title = "Studio Créatif",
                            subtitle = "Scripts, Posts & Prompts",
                            icon = Icons.Default.Palette,
                            iconColor = MazoumElectricBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("studio") },
                            testTag = "dash_studio_card"
                        )
                        DashboardModuleCard(
                            title = "Notes & Agenda",
                            subtitle = "Tâches & Organisation",
                            icon = Icons.AutoMirrored.Filled.Notes,
                            iconColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("notes_agenda") },
                            testTag = "dash_notes_card"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardModuleCard(
                            title = "Mémoire Privée",
                            subtitle = "Faits & Préférences",
                            icon = Icons.Default.Psychology,
                            iconColor = Color(0xFFEC4899),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("memory") },
                            testTag = "dash_memory_card"
                        )
                        DashboardModuleCard(
                            title = "Contrôle Numérique",
                            subtitle = "Périphériques & Hub",
                            icon = Icons.Default.Devices,
                            iconColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("devices") },
                            testTag = "dash_devices_card"
                        )
                    }
                }
            }

            // Weather & Daily Brief Widget
            item {
                MazoumCard(borderColor = MazoumCardBorder) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Météo & Synthèse du Jour",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MazoumTextPrimary
                                    )
                                )
                            }
                            Text(
                                text = "24°C • Ensoleillé",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MazoumCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Conseil de MAZOUM TECH : Excellente journée pour avancer sur vos projets prioritaires et concevoir vos nouveaux contenus créatifs.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MazoumTextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // Recent Tasks Preview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tâches en Cours (${tasks.count { !it.isCompleted }})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MazoumTextPrimary
                        )
                    )
                    Text(
                        text = "Voir tout",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MazoumCyan,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable { onNavigate("notes_agenda") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (tasks.isEmpty()) {
                    MazoumCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucune tâche pour le moment. Créez-en une dans Notes & Agenda !",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MazoumTextMuted
                                )
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tasks.take(3).forEach { task ->
                            MazoumCard(
                                borderColor = if (task.isCompleted) MazoumCardBorder.copy(alpha = 0.3f) else MazoumCardBorder
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
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
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (task.isCompleted) MazoumTextMuted else MazoumTextPrimary
                                            )
                                        )
                                        if (task.dueDate.isNotBlank()) {
                                            Text(
                                                text = "Échéance : ${task.dueDate}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MazoumTextSecondary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick History shortcut
            item {
                MazoumCard(onClick = { onNavigate("history") }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MazoumCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Consulter l'Historique des échanges",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MazoumTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MazoumTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    MazoumCard(
        modifier = modifier.testTag(testTag),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MazoumTextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MazoumTextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
