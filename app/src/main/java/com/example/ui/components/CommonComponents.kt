package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.NavigationItem
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
fun MazoumTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onVoiceClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = MazoumCyan
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, MazoumCyan.copy(alpha = 0.5f), CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mazoum_logo),
                        contentDescription = "Logo MAZOUM TECH",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MazoumTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = "MAZOUM TECH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MazoumCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onVoiceClick != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(MazoumElectricBlue, MazoumNeonPurple))
                        )
                        .clickable { onVoiceClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Assistant Vocal",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (onSettingsClick != null) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.testTag("top_bar_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Paramètres",
                        tint = MazoumTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun MazoumCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MazoumCardBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MazoumCardSurface.copy(alpha = 0.85f)
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.6f))
    ) {
        content()
    }
}

@Composable
fun MazoumGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "gradient_button"
) {
    val gradient = if (enabled) {
        Brush.linearGradient(listOf(MazoumCyan, MazoumElectricBlue, MazoumNeonPurple))
    } else {
        Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(gradient)
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 14.dp, horizontal = 20.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) Color(0xFF001524) else MazoumTextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color(0xFF001524) else MazoumTextMuted
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MazoumBottomBar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("main_bottom_nav_bar"),
        containerColor = MazoumDarkSurface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        val items = NavigationItem.bottomNavItems

        items.forEach { navItem ->
            val isSelected = currentRoute == navItem.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToRoute(navItem.route) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = navItem.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                modifier = Modifier.testTag(navItem.testTag),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MazoumCyan,
                    selectedTextColor = MazoumCyan,
                    unselectedIconColor = MazoumTextMuted,
                    unselectedTextColor = MazoumTextMuted,
                    indicatorColor = MazoumCardSurface
                )
            )
        }
    }
}
