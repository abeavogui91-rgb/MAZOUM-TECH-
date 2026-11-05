package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.HistoryItemEntity
import com.example.ui.components.MazoumBottomBar
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumTopBar
import com.example.ui.model.Screen
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumElectricBlue
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    history: List<HistoryItemEntity>,
    onDeleteHistoryItem: (Long) -> Unit,
    onNavigate: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.FRENCH)

    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Historique d'Activité",
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
                .padding(horizontal = 16.dp)
        ) {
            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MazoumTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Votre historique est vide pour le moment.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MazoumTextMuted)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        MazoumCard {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val icon: ImageVector = when (item.type) {
                                        "CHAT" -> Icons.Default.Chat
                                        "VOICE" -> Icons.Default.Mic
                                        "VISION" -> Icons.Default.Visibility
                                        "STUDIO" -> Icons.Default.Palette
                                        else -> Icons.Default.History
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MazoumCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = MazoumCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.size(12.dp))

                                    Column {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MazoumTextPrimary
                                            )
                                        )
                                        Text(
                                            text = item.preview,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MazoumTextSecondary,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = dateFormat.format(Date(item.timestamp)),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MazoumTextMuted,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                IconButton(onClick = { onDeleteHistoryItem(item.id) }) {
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
}
