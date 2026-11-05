package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.model.ChatMessage
import com.example.ui.model.MessageSender
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCardSurface
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumElectricBlue
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary
import com.example.ui.viewmodel.AssistantViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Écran 'Assistant conversationnel' développé avec Jetpack Compose.
 *
 * Structure :
 * - Zone supérieure : En-tête futuriste MAZOUM TECH avec statut de l'IA et bouton de réinitialisation.
 * - Zone centrale haute (poids 1f) : [LazyColumn] affichant les bulles de messages (utilisateur et IA) avec défilement automatique.
 * - Suggestions rapides (carrousel horizontal) lorsque le chat débute.
 * - Zone inférieure : barre de saisie avec [OutlinedTextField], dictée vocale et bouton d'envoi [Icons.AutoMirrored.Filled.Send],
 *   intégrée avec [imePadding] pour un comportement parfait au clavier.
 *
 * Connecté directement au [AssistantViewModel].
 */
@Composable
fun AssistantConversationnelScreen(
    viewModel: AssistantViewModel = viewModel(),
    onNavigateBack: (() -> Unit)? = null,
    onNavigate: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val listState = rememberLazyListState()

    // Gestion de la synthèse vocale locale (TTS)
    var isSpeaking by remember { mutableStateOf(false) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.FRENCH
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun speak(text: String) {
        tts?.stop()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "assistant_tts")
        isSpeaking = true
    }

    fun stopSpeaking() {
        tts?.stop()
        isSpeaking = false
    }

    // Lanceur de dictée vocale (Speech-to-Text)
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                val current = inputText
                val combined = if (current.isBlank()) spoken else "$current $spoken"
                viewModel.onInputTextChanged(combined)
            }
        }
    }

    // Défilement automatique vers le dernier message généré
    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = MazoumNight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            // En-tête futuriste de l'assistant conversationnel
            AssistantHeader(
                onBackClick = onNavigateBack,
                onClearClick = {
                    stopSpeaking()
                    viewModel.clearMessages()
                    Toast.makeText(context, "Conversation réinitialisée", Toast.LENGTH_SHORT).show()
                },
                onVoiceClick = { onNavigate?.invoke("voice") },
                isLoading = isLoading
            )

            // Suggestions de prompts rapides
            if (messages.size <= 2) {
                val promptSuggestions = listOf(
                    "Explique-moi un concept tech complexe",
                    "Rédige un e-mail professionnel et percutant",
                    "Aide-moi à organiser mes projets du jour",
                    "Quelles sont les meilleures pratiques IA ?"
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(promptSuggestions) { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MazoumDarkSurface)
                                .border(1.dp, MazoumCardBorder, RoundedCornerShape(16.dp))
                                .clickable { viewModel.sendMessage(suggestion) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MazoumCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MazoumTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Zone d'affichage des messages en haut (LazyColumn occupant tout l'espace disponible)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("chat_messages_list"),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    AssistantMessageBubble(
                        message = message,
                        isSpeaking = isSpeaking,
                        onSpeak = { speak(it) },
                        onStopSpeak = { stopSpeaking() },
                        onCopy = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("MAZOUM TECH", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Réponse copiée", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (isLoading) {
                    item {
                        AssistantTypingIndicator()
                    }
                }
            }

            // Champ de saisie et bouton d'envoi en bas
            AssistantInputBar(
                inputText = inputText,
                isLoading = isLoading,
                onTextChanged = { viewModel.onInputTextChanged(it) },
                onSend = { viewModel.sendMessage() },
                onMicClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH.toString())
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictez votre message à MAZOUM TECH...")
                    }
                    try {
                        speechLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Dictée vocale indisponible", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

/**
 * En-tête de l'écran avec informations d'état de l'assistant et boutons d'action.
 */
@Composable
private fun AssistantHeader(
    onBackClick: (() -> Unit)?,
    onClearClick: () -> Unit,
    onVoiceClick: (() -> Unit)?,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MazoumDarkSurface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = MazoumTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MazoumElectricBlue, MazoumCyan))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF070D1C),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Assistant conversationnel",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MazoumTextPrimary
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isLoading) MazoumCyan else Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isLoading) "MAZOUM TECH répond..." else "En ligne • Gemini 1.5 Flash",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MazoumTextSecondary
                    )
                )
            }
        }

        if (onVoiceClick != null) {
            IconButton(
                onClick = onVoiceClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mode vocal",
                    tint = MazoumCyan
                )
            }
        }

        IconButton(
            onClick = onClearClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Effacer la conversation",
                tint = MazoumTextMuted
            )
        }
    }
}

/**
 * Bulle de message affichant les échanges de l'utilisateur et les réponses de l'IA.
 */
@Composable
private fun AssistantMessageBubble(
    message: ChatMessage,
    isSpeaking: Boolean,
    onSpeak: (String) -> Unit,
    onStopSpeak: () -> Unit,
    onCopy: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // En-tête du message
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            if (!isUser) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MazoumCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "MAZOUM TECH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MazoumCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = formattedTime,
                fontSize = 10.sp,
                color = MazoumTextMuted
            )
        }

        // Bulle de contenu
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.85f else 0.94f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.linearGradient(listOf(Color(0xFF00388F), Color(0xFF0052FF)))
                    } else {
                        Brush.linearGradient(listOf(MazoumCardSurface, MazoumDarkSurface))
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) MazoumCyan.copy(alpha = 0.5f) else MazoumCardBorder,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MazoumTextPrimary,
                    lineHeight = 21.sp
                )
            )
        }

        // Barre d'actions sous les réponses de l'IA (Copier, Synthèse vocale)
        if (!isUser && message.text.isNotBlank()) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onCopy(message.text) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copier le texte",
                        tint = MazoumTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (isSpeaking) onStopSpeak() else onSpeak(message.text)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isSpeaking) "Arrêter la voix" else "Lire la réponse",
                        tint = if (isSpeaking) MazoumNeonPurple else MazoumTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Indicateur visuel d'attente pendant la génération de la réponse par le modèle.
 */
@Composable
private fun AssistantTypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            color = MazoumCyan,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "MAZOUM TECH analyse et rédige la réponse...",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MazoumTextSecondary,
                fontSize = 12.sp
            )
        )
    }
}

/**
 * Barre de saisie inférieure intégrant le champ texte, la dictée vocale et le bouton d'envoi.
 */
@Composable
private fun AssistantInputBar(
    inputText: String,
    isLoading: Boolean,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    val canSend = inputText.isNotBlank() && !isLoading

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MazoumDarkSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onTextChanged,
            placeholder = {
                Text(
                    text = "Posez votre question à MAZOUM TECH...",
                    color = MazoumTextMuted,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .testTag("chat_input_field"),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MazoumCyan,
                unfocusedBorderColor = MazoumCardBorder,
                focusedTextColor = MazoumTextPrimary,
                unfocusedTextColor = MazoumTextPrimary,
                focusedContainerColor = MazoumNight,
                unfocusedContainerColor = MazoumNight
            ),
            shape = RoundedCornerShape(22.dp),
            trailingIcon = {
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictée vocale",
                        tint = MazoumCyan
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (canSend) {
                        Brush.linearGradient(listOf(MazoumCyan, MazoumElectricBlue))
                    } else {
                        Brush.linearGradient(listOf(Color(0xFF26334D), Color(0xFF162032)))
                    }
                )
                .clickable(enabled = canSend) { onSend() }
                .testTag("chat_send_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Envoyer le message",
                tint = if (canSend) Color(0xFF001524) else MazoumTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
