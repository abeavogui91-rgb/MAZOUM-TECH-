package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MazoumBottomBar
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumGradientButton
import com.example.ui.components.MazoumTopBar
import com.example.ui.model.Screen
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumElectricBlue
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary
import com.example.util.VoiceManager

@Composable
fun StudioScreen(
    currentTool: String,
    studioResult: String?,
    isLoading: Boolean,
    voiceManager: VoiceManager,
    onSetTool: (String) -> Unit,
    onGenerate: (topic: String, platform: String, tone: String, details: String) -> Unit,
    onSaveAsNote: (String, String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var topicInput by remember { mutableStateOf("") }
    var platformInput by remember { mutableStateOf("TikTok & Reels") }
    var toneInput by remember { mutableStateOf("Captivant & Dynamique") }
    var detailsInput by remember { mutableStateOf("") }

    val tools = listOf(
        Triple("SCRIPT", "Scripts Vidéo", Icons.Default.Movie),
        Triple("TEXT", "Rédacteur Pro", Icons.Default.Description),
        Triple("PROMPT", "Générateur Prompts", Icons.Default.Lightbulb),
        Triple("SOCIAL", "Réseaux Sociaux", Icons.Default.Campaign),
        Triple("VOICEOVER", "Voix-off Audio", Icons.Default.RecordVoiceOver)
    )

    val tones = listOf(
        "Captivant & Dynamique",
        "Professionnel & Convaincant",
        "Pédagogique & Clair",
        "Inspirant & Émouvant",
        "Humoristique & Décalé"
    )

    val platforms = listOf(
        "TikTok & Reels",
        "YouTube (Format Long)",
        "LinkedIn",
        "Instagram & Threads",
        "X (ex-Twitter)",
        "Blog & Newsletter"
    )

    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Studio de Création IA",
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tool selector
            Text(
                text = "Outils de Création",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MazoumTextPrimary
                )
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tools) { (key, label, icon) ->
                    val isSelected = currentTool == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MazoumCyan.copy(alpha = 0.2f) else MazoumDarkSurface)
                            .border(
                                1.dp,
                                if (isSelected) MazoumCyan else MazoumCardBorder.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSetTool(key) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) MazoumCyan else MazoumTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) MazoumCyan else MazoumTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // Input Form Card
            MazoumCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Sujet ou idée principale :",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MazoumCyan
                        )
                    )

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        placeholder = {
                            Text("Ex: Les 5 secrets de l'IA pour doubler sa productivité...", color = MazoumTextMuted, fontSize = 13.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_topic_input"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MazoumCyan,
                            unfocusedBorderColor = MazoumCardBorder,
                            focusedTextColor = MazoumTextPrimary,
                            unfocusedTextColor = MazoumTextPrimary,
                            focusedContainerColor = MazoumDarkSurface,
                            unfocusedContainerColor = MazoumDarkSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "2. Plateforme ou format cible :",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MazoumTextPrimary
                        )
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platforms) { p ->
                            val isSelected = platformInput == p
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MazoumElectricBlue.copy(alpha = 0.25f) else MazoumDarkSurface)
                                    .border(1.dp, if (isSelected) MazoumElectricBlue else MazoumCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { platformInput = p }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = p,
                                    fontSize = 11.sp,
                                    color = if (isSelected) MazoumCyan else MazoumTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Text(
                        text = "3. Tonalité souhaitée :",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MazoumTextPrimary
                        )
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tones) { t ->
                            val isSelected = toneInput == t
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MazoumNeonPurple.copy(alpha = 0.25f) else MazoumDarkSurface)
                                    .border(1.dp, if (isSelected) MazoumNeonPurple else MazoumCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { toneInput = t }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 11.sp,
                                    color = if (isSelected) MazoumNeonPurple else MazoumTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Text(
                        text = "4. Consignes ou détails complémentaires (facultatif) :",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MazoumTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = detailsInput,
                        onValueChange = { detailsInput = it },
                        placeholder = {
                            Text("Ex: Inclure un appel à l'action pour s'abonner, format rapide...", color = MazoumTextMuted, fontSize = 13.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MazoumCyan,
                            unfocusedBorderColor = MazoumCardBorder,
                            focusedTextColor = MazoumTextPrimary,
                            unfocusedTextColor = MazoumTextPrimary,
                            focusedContainerColor = MazoumDarkSurface,
                            unfocusedContainerColor = MazoumDarkSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Generate Button
            MazoumGradientButton(
                text = if (isLoading) "Création en cours..." else "Générer le Contenu Studio",
                onClick = {
                    onGenerate(topicInput, platformInput, toneInput, detailsInput)
                },
                enabled = topicInput.isNotBlank() && !isLoading,
                icon = Icons.Default.AutoAwesome,
                modifier = Modifier.fillMaxWidth(),
                testTag = "studio_generate_button"
            )

            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = MazoumCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Création du contenu haute valeur...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MazoumCyan)
                    )
                }
            }

            // Result Display
            if (studioResult != null) {
                MazoumCard(borderColor = MazoumNeonPurple) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Contenu Généré",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MazoumCyan
                                )
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Studio MAZOUM TECH", studioResult)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Contenu copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = MazoumTextSecondary, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { voiceManager.speak(studioResult) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Écouter la voix-off", tint = MazoumCyan, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        onSaveAsNote("Studio : $topicInput", studioResult)
                                        Toast.makeText(context, "Sauvegardé dans vos Notes !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "Sauvegarder", tint = MazoumNeonPurple, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = studioResult,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MazoumTextPrimary,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
