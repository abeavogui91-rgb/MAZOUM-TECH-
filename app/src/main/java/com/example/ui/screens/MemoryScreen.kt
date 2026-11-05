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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MemoryEntity
import com.example.ui.components.MazoumBottomBar
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumGradientButton
import com.example.ui.components.MazoumTopBar
import com.example.ui.model.Screen
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCardSurface
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumError
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary

@Composable
fun MemoryScreen(
    isMemoryEnabled: Boolean,
    memories: List<MemoryEntity>,
    onToggleMemory: (Boolean) -> Unit,
    onAddMemory: (category: String, content: String) -> Unit,
    onDeleteMemory: (MemoryEntity) -> Unit,
    onClearAllMemory: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var newFactCategory by remember { mutableStateOf("Préférence") }
    var newFactContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Mémoire Personnelle IA",
                onBackClick = { onNavigate("home") },
                onSettingsClick = { onNavigate("settings") }
            )
        },
        containerColor = MazoumNight,
        modifier = Modifier.statusBarsPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Enable / Disable Memory Switch Card
            MazoumCard(borderColor = if (isMemoryEnabled) MazoumCyan else MazoumCardBorder) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mémoire Active",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MazoumTextPrimary
                            )
                        )
                        Text(
                            text = if (isMemoryEnabled) {
                                "L'assistant contextualise ses réponses avec vos informations sauvegardées."
                            } else {
                                "Mémoire en pause : l'assistant répond sans utiliser vos données antérieures."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MazoumTextSecondary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = isMemoryEnabled,
                        onCheckedChange = { onToggleMemory(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF001524),
                            checkedTrackColor = MazoumCyan,
                            uncheckedThumbColor = MazoumTextMuted,
                            uncheckedTrackColor = MazoumDarkSurface
                        )
                    )
                }
            }

            // Explanatory info
            MazoumCard {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MazoumCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sécurité garantie : vos faits mémorisés sont stockés localement sur votre téléphone. Vous pouvez en effacer n'importe lequel d'un geste.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MazoumTextSecondary,
                            lineHeight = 17.sp
                        )
                    )
                }
            }

            // Add Memory & Clear All Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Faits Retenus (${memories.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary
                    )
                )

                Row {
                    if (memories.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Tout effacer",
                                tint = MazoumError
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MazoumCyan.copy(alpha = 0.2f))
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MazoumCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter", color = MazoumCyan, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Memory items list
            if (memories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MazoumTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aucune mémoire enregistrée pour le moment.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MazoumTextMuted)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(memories, key = { it.id }) { memory ->
                        MazoumCard {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MazoumNeonPurple.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = memory.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MazoumNeonPurple,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = memory.content,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MazoumTextPrimary,
                                            lineHeight = 19.sp
                                        )
                                    )
                                }
                                IconButton(onClick = { onDeleteMemory(memory) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Oublier ce fait",
                                        tint = MazoumTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Memory Fact Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = MazoumDarkSurface,
                title = {
                    Text("Apprendre à l'IA", color = MazoumTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Catégorie :",
                            color = MazoumTextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Préférence", "Projet", "Objectif", "Identité").forEach { cat ->
                                val isSelected = newFactCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MazoumCyan.copy(alpha = 0.2f) else MazoumNight)
                                        .border(1.dp, if (isSelected) MazoumCyan else MazoumCardBorder, RoundedCornerShape(6.dp))
                                        .clickable { newFactCategory = cat }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MazoumCyan else MazoumTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = newFactContent,
                            onValueChange = { newFactContent = it },
                            placeholder = { Text("Ex: Toujours répondre en français avec des puces claires...", color = MazoumTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MazoumCyan,
                                unfocusedBorderColor = MazoumCardBorder,
                                focusedTextColor = MazoumTextPrimary,
                                unfocusedTextColor = MazoumTextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newFactContent.isNotBlank()) {
                                onAddMemory(newFactCategory, newFactContent.trim())
                                newFactContent = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Enregistrer dans la mémoire", color = MazoumCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Annuler", color = MazoumTextSecondary)
                    }
                }
            )
        }

        // Confirm Clear All Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                containerColor = MazoumDarkSurface,
                title = {
                    Text("Effacer toute la mémoire ?", color = MazoumTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "Cette action supprimera définitivement tous les faits appris par l'assistant.",
                        color = MazoumTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onClearAllMemory()
                            showClearDialog = false
                        }
                    ) {
                        Text("Tout effacer", color = MazoumError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Annuler", color = MazoumTextSecondary)
                    }
                }
            )
        }
    }
}
