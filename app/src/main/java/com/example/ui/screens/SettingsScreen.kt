package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.MazoumBottomBar
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumTopBar
import com.example.ui.model.Screen
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCardSurface
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumError
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumSuccess
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary

@Composable
fun SettingsScreen(
    userName: String,
    userRole: String,
    isVoiceFeedbackEnabled: Boolean,
    voiceSpeed: Float,
    onToggleVoiceFeedback: (Boolean) -> Unit,
    onUpdateVoiceSpeed: (Float) -> Unit,
    onResetAllData: () -> Unit,
    onEditProfile: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }
    var currentSpeed by remember { mutableFloatStateOf(voiceSpeed) }

    var micPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        micPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "Permission Microphone autorisée", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            MazoumTopBar(title = "Sécurité & Paramètres")
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
            // Profile Card
            item {
                MazoumCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MazoumCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MazoumCyan, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MazoumTextPrimary
                                    )
                                )
                                Text(
                                    text = userRole,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MazoumTextSecondary
                                    )
                                )
                            }
                        }

                        IconButton(onClick = onEditProfile) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MazoumCyan)
                        }
                    }
                }
            }

            // Voice settings
            item {
                Text(
                    text = "Synthèse Vocale & Audio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary
                    )
                )
            }

            item {
                MazoumCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Lecture vocale automatique",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MazoumTextPrimary
                                    )
                                )
                                Text(
                                    text = "MAZOUM TECH lit automatiquement ses réponses à voix haute.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MazoumTextSecondary)
                                )
                            }
                            Switch(
                                checked = isVoiceFeedbackEnabled,
                                onCheckedChange = onToggleVoiceFeedback,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF001524),
                                    checkedTrackColor = MazoumCyan,
                                    uncheckedThumbColor = MazoumTextMuted,
                                    uncheckedTrackColor = MazoumDarkSurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Vitesse d'élocution : ${String.format("%.1fx", currentSpeed)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MazoumCyan, fontWeight = FontWeight.SemiBold)
                        )

                        Slider(
                            value = currentSpeed,
                            onValueChange = {
                                currentSpeed = it
                                onUpdateVoiceSpeed(it)
                            },
                            valueRange = 0.7f..1.5f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = MazoumCyan,
                                activeTrackColor = MazoumCyan,
                                inactiveTrackColor = MazoumDarkSurface
                            )
                        )
                    }
                }
            }

            // Permissions Status
            item {
                Text(
                    text = "Sécurité & Permissions Système",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary
                    )
                )
            }

            item {
                MazoumCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PermissionRow(
                            title = "Microphone (Reconnaissance Vocale)",
                            subtitle = if (micPermissionGranted) "Autorisé" else "Non autorisé (Touchez pour activer)",
                            icon = Icons.Default.Mic,
                            isGranted = micPermissionGranted,
                            onClick = {
                                if (!micPermissionGranted) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        )

                        PermissionRow(
                            title = "Connexion Réseau & IA",
                            subtitle = "Actif (Accès Firebase AI Logic)",
                            icon = Icons.Default.Wifi,
                            isGranted = true
                        )

                        PermissionRow(
                            title = "Notifications & Rappels",
                            subtitle = "Actif pour vos échéances d'agenda",
                            icon = Icons.Default.Notifications,
                            isGranted = true
                        )
                    }
                }
            }

            // Economic Model & Ethics Banner
            item {
                MazoumCard(borderColor = MazoumNeonPurple.copy(alpha = 0.5f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MazoumCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Modèle Économique & Confidentialité",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MazoumTextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 100% Gratuit au lancement.\n• Aucun abonnement Premium obligatoire.\n• Aucune publicité intrusive.\n• Clés API protégées et jamais exposées aux tiers.\n• Aucune écoute cachée sans action utilisateur.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MazoumTextSecondary,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }
            }

            // Danger Zone: Reset All Data
            item {
                MazoumCard(borderColor = MazoumError.copy(alpha = 0.5f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Zone de Sécurité Locale",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MazoumError
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Efface définitivement toutes vos notes, tâches, mémoires IA et l'historique sur cet appareil.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MazoumTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MazoumError.copy(alpha = 0.2f))
                                .border(1.dp, MazoumError, RoundedCornerShape(10.dp))
                                .clickable { showResetDialog = true }
                                .padding(vertical = 10.dp, horizontal = 16.dp)
                                .testTag("settings_reset_data_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MazoumError, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Effacer toutes les données locales", color = MazoumError, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Version info
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MAZOUM TECH • Version 1.0.0 (MVP)",
                        style = MaterialTheme.typography.labelSmall.copy(color = MazoumTextMuted)
                    )
                    Text(
                        text = "« Votre intelligence numérique, partout avec vous. »",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MazoumCyan.copy(alpha = 0.7f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                containerColor = MazoumDarkSurface,
                title = {
                    Text("Confirmer la réinitialisation ?", color = MazoumTextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "Cette opération est irréversible. Toutes vos notes, tâches, mémoires et historiques seront effacés de la base de données locale.",
                        color = MazoumTextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onResetAllData()
                            showResetDialog = false
                            Toast.makeText(context, "Toutes les données ont été réinitialisées", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Confirmer l'effacement", color = MazoumError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Annuler", color = MazoumTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isGranted: Boolean,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) MazoumCyan else MazoumTextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MazoumTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isGranted) MazoumSuccess else Color(0xFFF59E0B)
                    )
                )
            }
        }

        if (isGranted) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Actif", tint = MazoumSuccess, modifier = Modifier.size(18.dp))
        }
    }
}
