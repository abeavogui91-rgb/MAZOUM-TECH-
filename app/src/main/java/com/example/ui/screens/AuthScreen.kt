package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MazoumCard
import com.example.ui.components.MazoumGradientButton
import com.example.ui.components.MazoumTopBar
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

@Composable
fun AuthScreen(
    currentName: String,
    currentRole: String,
    onSaveProfile: (String, String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    var nameInput by remember { mutableStateOf(if (currentName == "Explorateur") "" else currentName) }
    var selectedRole by remember { mutableStateOf(currentRole) }

    val roles = listOf(
        "Créateur de contenus",
        "Entrepreneur & Dirigeant",
        "Étudiant & Chercheur",
        "Développeur & Tech",
        "Particulier & Polyvalent"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MazoumNight)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        MazoumTopBar(
            title = "Configuration du Profil",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Personnalisez votre Assistant",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MazoumTextPrimary
                )
            )

            Text(
                text = "MAZOUM TECH adapte son vocabulaire, ses recommandations et son style à votre identité et vos objectifs professionnels.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MazoumTextSecondary,
                    lineHeight = 20.sp
                )
            )

            // Name field
            MazoumCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comment souhaitez-vous être appelé(e) ?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MazoumCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        placeholder = { Text("Ex: Alexandre, Sarah, Mamoudou...", color = MazoumTextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MazoumCyan)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_input"),
                        singleLine = true,
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

            // Role selection
            MazoumCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Work, contentDescription = null, tint = MazoumNeonPurple, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Votre profil principal :",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MazoumTextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        roles.forEach { role ->
                            val isSelected = selectedRole == role
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MazoumElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) MazoumCyan else MazoumCardBorder.copy(alpha = 0.5f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedRole = role }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = role,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) MazoumCyan else MazoumTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sélectionné",
                                        tint = MazoumCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Privacy pledge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MazoumCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Vos informations restent locales et sous votre contrôle absolu.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MazoumTextMuted,
                        fontSize = 12.sp
                    )
                )
            }

            MazoumGradientButton(
                text = "Enregistrer et accéder au Tableau de bord",
                onClick = {
                    val finalName = if (nameInput.isBlank()) "Explorateur" else nameInput.trim()
                    onSaveProfile(finalName, selectedRole)
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "auth_save_button"
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
