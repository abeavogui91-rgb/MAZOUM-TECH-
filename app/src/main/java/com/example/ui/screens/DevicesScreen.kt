package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MazoumBottomBar
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumTopBar
import com.example.ui.model.ConnectedDevice
import com.example.ui.model.Screen
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumSuccess
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary

@Composable
fun DevicesScreen(
    devices: List<ConnectedDevice>,
    onToggleDevice: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var devicePendingConfirmation by remember { mutableStateOf<ConnectedDevice?>(null) }

    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Contrôle Numérique & Hub",
                onBackClick = { onNavigate("home") },
                onSettingsClick = { onNavigate("settings") }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                MazoumCard {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MazoumCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Architecture Sécurisée & Permissions",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MazoumTextPrimary
                                )
                            )
                            Text(
                                text = "Toute action sur un appareil externe exige votre confirmation préalable explicite et respecte les restrictions du système.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MazoumTextSecondary,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Périphériques & Passerelles (${devices.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary
                    )
                )
            }

            items(devices, key = { it.id }) { device ->
                val icon: ImageVector = when (device.type) {
                    "Smartphone" -> Icons.Default.PhoneAndroid
                    "PC Windows" -> Icons.Default.Computer
                    "Domotique" -> Icons.Default.Home
                    else -> Icons.Default.Language
                }

                MazoumCard(borderColor = if (device.isConnected) MazoumCyan.copy(alpha = 0.5f) else MazoumCardBorder) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (device.isConnected) MazoumCyan.copy(alpha = 0.15f) else MazoumDarkSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (device.isConnected) MazoumCyan else MazoumTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = device.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MazoumTextPrimary
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (device.isConnected) MazoumSuccess else MazoumTextMuted)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (device.isConnected) "Connecté • ${device.lastSync}" else "Déconnecté • En attente",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (device.isConnected) MazoumSuccess else MazoumTextMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (device.id != "1") {
                            Switch(
                                checked = device.isConnected,
                                onCheckedChange = {
                                    if (!device.isConnected) {
                                        devicePendingConfirmation = device
                                    } else {
                                        onToggleDevice(device.id)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF001524),
                                    checkedTrackColor = MazoumCyan,
                                    uncheckedThumbColor = MazoumTextMuted,
                                    uncheckedTrackColor = MazoumDarkSurface
                                )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MazoumCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Actuel", color = MazoumCyan, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // Security confirmation dialog
        if (devicePendingConfirmation != null) {
            AlertDialog(
                onDismissRequest = { devicePendingConfirmation = null },
                containerColor = MazoumDarkSurface,
                title = {
                    Text("Autoriser la passerelle ?", color = MazoumTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "Souhaitez-vous activer la liaison sécurisée avec « ${devicePendingConfirmation?.name} » ? Toutes les requêtes transiteront sous chiffrement local.",
                        color = MazoumTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            devicePendingConfirmation?.id?.let { onToggleDevice(it) }
                            devicePendingConfirmation = null
                            Toast.makeText(context, "Passerelle activée avec succès", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Confirmer l'accès", color = MazoumCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { devicePendingConfirmation = null }) {
                        Text("Refuser", color = MazoumTextSecondary)
                    }
                }
            )
        }
    }
}
