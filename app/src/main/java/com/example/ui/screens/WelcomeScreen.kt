package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumGradientButton
import com.example.ui.theme.MazoumCardBorder
import com.example.ui.theme.MazoumCyan
import com.example.ui.theme.MazoumDarkSurface
import com.example.ui.theme.MazoumElectricBlue
import com.example.ui.theme.MazoumNeonPurple
import com.example.ui.theme.MazoumNight
import com.example.ui.theme.MazoumTextMuted
import com.example.ui.theme.MazoumTextPrimary
import com.example.ui.theme.MazoumTextSecondary

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MazoumNight, Color(0xFF091226), MazoumDarkSurface)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(MazoumNeonPurple.copy(alpha = 0.15f))
                        .border(1.dp, MazoumNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(30.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MazoumCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "L'ASSISTANT PERSONNEL NOUVELLE GÉNÉRATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MazoumCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Logo Orb
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(MazoumCyan.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                        .border(2.dp, Brush.linearGradient(listOf(MazoumCyan, MazoumNeonPurple)), CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mazoum_logo),
                        contentDescription = "MAZOUM TECH",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "MAZOUM TECH",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = MazoumTextPrimary,
                        letterSpacing = 1.5.sp
                    )
                )

                Text(
                    text = "« Votre intelligence numérique, partout avec vous. »",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MazoumCyan,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // Capabilities highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureHighlightRow(
                        icon = Icons.Default.Mic,
                        title = "Assistant Vocal & Conversationnel",
                        description = "Dialogue intelligent en français, lecture vocale des réponses et dictée."
                    )
                    FeatureHighlightRow(
                        icon = Icons.Default.Visibility,
                        title = "Vision Intelligente",
                        description = "Lecture OCR, explication d'erreurs, traduction et analyse de documents."
                    )
                    FeatureHighlightRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Studio de Création IA",
                        description = "Générateur de scripts vidéo, posts réseaux, prompts et contenus pro."
                    )
                    FeatureHighlightRow(
                        icon = Icons.Default.Psychology,
                        title = "Mémoire & Gestion Privée",
                        description = "Gestion de vos notes, tâches, agenda et mémoire locale sécurisée."
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MazoumGradientButton(
                    text = "Découvrir MAZOUM TECH",
                    onClick = onStartClick,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "welcome_get_started_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MazoumTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Gratuit • Données privées et protégées",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MazoumTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureHighlightRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    MazoumCard(borderColor = MazoumCardBorder) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MazoumCyan.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MazoumCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MazoumTextSecondary,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
