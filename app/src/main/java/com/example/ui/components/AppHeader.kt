package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AppScreen
import com.example.model.DriverProfile
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLightBg
import com.example.ui.theme.EmeraldOriginBg
import com.example.ui.theme.EmeraldOriginText
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.theme.SurfaceWhite

@Composable
fun AppHeader(
    currentScreen: AppScreen,
    driver: DriverProfile,
    unreadNotificationsCount: Int = 3,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_beacon")
    val beaconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_header"),
        color = SurfaceWhite.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Branding & Screen Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Litoral em Movimento Brand Logo
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuB-ElUdIhbcpsnPN-6RbU9Nomz07Zli9GjCcOMiFqltIKE9PSDYy9ngI9KmDKcAuexz1L7cklLI6MnDuuxcj541MrN3iUO_L46nqOlNc2PRU0y6_k5FuEp4YHgKVilXZslVyoHGpKfuu_lD0cz8c5yGoKtZfK4zrSKzmePsmL9DEXgakGXJ8pdcpgKN3QOFoaT5-IBpas_NwmSEaN4xvxU6ZvZhkGMPbOXKykU1heGzD6Fn0scvI8gj",
                        contentDescription = "Litoral em Movimento Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currentScreen.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                letterSpacing = (-0.3).sp
                            )
                        )

                        // Pulsing online green beacon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(if (driver.isOnline) beaconScale else 1f)
                                    .clip(CircleShape)
                                    .background(if (driver.isOnline) EmeraldVibrant.copy(alpha = 0.4f) else Slate500.copy(alpha = 0.3f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (driver.isOnline) EmeraldVibrant else Slate500)
                            )
                        }
                    }

                    // Online pill & Sheets sync status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (driver.isOnline) EmeraldOriginBg else Slate100)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (driver.isOnline) "ONLINE" else "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (driver.isOnline) EmeraldOriginText else Slate500
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sheets Sync",
                                tint = EmeraldDark,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Sheets Sync",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate500
                                )
                            )
                        }
                    }
                }
            }

            // Right Actions: Notification Bell & Driver Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notification Button
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100)
                            .testTag("notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificações",
                            tint = Slate900,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    if (unreadNotificationsCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldVibrant)
                                .border(1.5.dp, SurfaceWhite, CircleShape)
                        )
                    }
                }

                // Profile Avatar with online badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, if (driver.isOnline) EmeraldVibrant else Slate300, CircleShape)
                        .clickable(onClick = onProfileClick)
                        .testTag("profile_avatar_button")
                ) {
                    AsyncImage(
                        model = driver.avatarUrl,
                        contentDescription = "Perfil ${driver.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
