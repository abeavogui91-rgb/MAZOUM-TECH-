package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.theme.MazoumCardSurface
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary
import com.example.util.VoiceManager

@Composable
fun VisionScreen(
    selectedBitmap: Bitmap?,
    visionResult: String?,
    isLoading: Boolean,
    analysisType: String,
    voiceManager: VoiceManager,
    onSelectImage: (Bitmap?) -> Unit,
    onSetAnalysisType: (String) -> Unit,
    onAnalyze: (String) -> Unit,
    onSaveAsNote: (String, String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var customQuery by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                onSelectImage(bitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur de chargement d'image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            MazoumTopBar(
                title = "Vision Intelligente",
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
            // Mode Selector
            Text(
                text = "Objectif d'Analyse Visuelle",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MazoumTextPrimary
                )
            )

            val modes = listOf(
                Triple("ALL", "Globale", Icons.Default.Visibility),
                Triple("OCR", "Lire le texte", Icons.Default.Description),
                Triple("ERROR", "Résoudre Erreur", Icons.Default.BugReport),
                Triple("DOCUMENT", "Document", Icons.Default.AutoAwesome),
                Triple("TRANSLATE", "Traduire", Icons.Default.Translate)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                modes.take(3).forEach { (key, label, icon) ->
                    AnalysisTypePill(
                        label = label,
                        icon = icon,
                        isSelected = analysisType == key,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetAnalysisType(key) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                modes.drop(3).forEach { (key, label, icon) ->
                    AnalysisTypePill(
                        label = label,
                        icon = icon,
                        isSelected = analysisType == key,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetAnalysisType(key) }
                    )
                }
            }

            // Image Preview or Import Box
            if (selectedBitmap != null) {
                MazoumCard(borderColor = MazoumCyan) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = selectedBitmap.asImageBitmap(),
                            contentDescription = "Image sélectionnée",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Image prête à être analysée",
                                style = MaterialTheme.typography.bodySmall.copy(color = MazoumCyan)
                            )
                            Text(
                                text = "Changer",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MazoumTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                        }
                    }
                }
            } else {
                MazoumCard(
                    borderColor = MazoumCardBorder,
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MazoumCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MazoumCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Importer une photo ou capture d'écran",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MazoumTextPrimary
                            )
                        )
                        Text(
                            text = "Sélectionnez un document, une erreur de code ou un objet",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MazoumTextSecondary
                            )
                        )
                    }
                }
            }

            // Custom Prompt Field
            OutlinedTextField(
                value = customQuery,
                onValueChange = { customQuery = it },
                placeholder = {
                    Text("Question ou consigne spécifique (facultatif)...", color = MazoumTextMuted, fontSize = 13.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vision_custom_query_input"),
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

            // Analyze Button
            MazoumGradientButton(
                text = if (isLoading) "Analyse en cours..." else "Lancer l'Analyse Vision",
                onClick = { onAnalyze(customQuery) },
                enabled = selectedBitmap != null && !isLoading,
                icon = Icons.Default.AutoAwesome,
                modifier = Modifier.fillMaxWidth(),
                testTag = "vision_analyze_button"
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
                        text = "Examen visuel et extraction en cours...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MazoumCyan)
                    )
                }
            }

            // Result Card
            if (visionResult != null) {
                MazoumCard(borderColor = MazoumNeonPurple.copy(alpha = 0.6f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Résultats de l'Analyse IA",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MazoumCyan
                                )
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Vision MAZOUM TECH", visionResult)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Résultat copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = MazoumTextSecondary, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { voiceManager.speak(visionResult) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Écouter", tint = MazoumCyan, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = {
                                        onSaveAsNote("Analyse Vision ($analysisType)", visionResult)
                                        Toast.makeText(context, "Ajouté à vos Notes !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "Enregistrer", tint = MazoumNeonPurple, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = visionResult,
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

@Composable
private fun AnalysisTypePill(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MazoumCyan.copy(alpha = 0.2f) else MazoumDarkSurface)
            .border(
                1.dp,
                if (isSelected) MazoumCyan else MazoumCardBorder.copy(alpha = 0.6f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MazoumCyan else MazoumTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) MazoumCyan else MazoumTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            )
        }
    }
}
