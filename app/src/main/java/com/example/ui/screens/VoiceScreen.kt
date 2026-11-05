package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumTopBar
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
import java.util.Locale

@Composable
fun VoiceScreen(
    voiceStatus: String,
    voiceResponse: String,
    voiceManager: VoiceManager,
    onVoiceQuery: (String) -> Unit,
    onBack: () -> Unit
) {
    val isSpeaking by voiceManager.isSpeaking.collectAsState()
    var manualInput by remember { mutableStateOf("") }

    // Speech to text launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                onVoiceQuery(spoken)
            }
        }
    }

    fun startListening() {
        voiceManager.stop()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH.toString())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez à MAZOUM TECH...")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            // If device has no speech intent, allow typing
        }
    }

    // Infinite breathing animation for the neural orb
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (isSpeaking) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 600 else 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MazoumNight, Color(0xFF061026), MazoumDarkSurface)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        MazoumTopBar(
            title = "Assistant Vocal Immersif",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Status message
            Text(
                text = if (isSpeaking) "MAZOUM TECH s'exprime..." else voiceStatus,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSpeaking) MazoumCyan else MazoumTextSecondary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cyber Neural Orb
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                (if (isSpeaking) MazoumCyan else MazoumNeonPurple).copy(alpha = 0.35f),
                                MazoumElectricBlue.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        width = if (isSpeaking) 3.dp else 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(MazoumCyan, MazoumElectricBlue, MazoumNeonPurple)
                        ),
                        shape = CircleShape
                    )
                    .clickable { startListening() }
                    .padding(14.dp)
                    .testTag("voice_orb_button"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mazoum_logo),
                    contentDescription = "Neural Orb",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )

                if (isSpeaking) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MazoumCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "En lecture",
                                style = MaterialTheme.typography.labelSmall.copy(color = MazoumCyan)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spoken Response Display
            if (voiceResponse.isNotBlank()) {
                MazoumCard(
                    borderColor = MazoumCyan.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dernière réponse :",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MazoumCyan
                                )
                            )
                            if (isSpeaking) {
                                IconButton(
                                    onClick = { voiceManager.stop() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = "Arrêter la voix",
                                        tint = MazoumTextPrimary
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { voiceManager.speak(voiceResponse) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Réécouter",
                                        tint = MazoumCyan
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = voiceResponse,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MazoumTextPrimary,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Suggested Voice Queries
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Suggestions rapides à formuler :",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MazoumTextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                val suggestions = listOf(
                    "Présente-toi et donne tes fonctionnalités",
                    "Donne-moi une idée de script vidéo tendance",
                    "Rédige un message percutant pour LinkedIn",
                    "Quelles sont mes tâches aujourd'hui ?"
                )

                suggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MazoumDarkSurface)
                            .border(1.dp, MazoumCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onVoiceQuery(suggestion) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = MazoumCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "« $suggestion »",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MazoumTextPrimary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Bar (Speech Button + Manual Type Input)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualInput,
                        onValueChange = { manualInput = it },
                        placeholder = { Text("Ou saisissez une question...", color = MazoumTextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_manual_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MazoumCyan,
                            unfocusedBorderColor = MazoumCardBorder,
                            focusedTextColor = MazoumTextPrimary,
                            unfocusedTextColor = MazoumTextPrimary,
                            focusedContainerColor = MazoumDarkSurface,
                            unfocusedContainerColor = MazoumDarkSurface
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (manualInput.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MazoumCyan)
                                .clickable {
                                    onVoiceQuery(manualInput.trim())
                                    manualInput = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Envoyer",
                                tint = Color(0xFF001524),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(MazoumCyan, MazoumElectricBlue))
                                )
                                .clickable { startListening() }
                                .testTag("voice_listen_mic_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Parler",
                                tint = Color(0xFF001524),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
