package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DriverProfile
import com.example.model.EarningsSummary
import com.example.model.TripCategory
import com.example.model.TripItem
import com.example.model.TripStatusType
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberPendingBg
import com.example.ui.theme.AmberPendingText
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBg
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.DestRedBg
import com.example.ui.theme.DestRedText
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLightBg
import com.example.ui.theme.EmeraldOriginBg
import com.example.ui.theme.EmeraldOriginText
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBg
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceWhite

@Composable
fun ViagensScreen(
    driver: DriverProfile,
    earnings: EarningsSummary,
    trips: List<TripItem>,
    selectedCategory: TripCategory,
    minhasCount: Int,
    disponiveisCount: Int,
    todasCount: Int,
    lastSyncTime: String,
    isSyncing: Boolean,
    onCategorySelected: (TripCategory) -> Unit,
    onSyncFeed: () -> Unit,
    onStartTrip: (String) -> Unit,
    onClaimTrip: (String) -> Unit,
    onTripDetails: (TripItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sync_spin"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(horizontal = 16.dp)
            .testTag("viagens_feed_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Driver Greeting Cockpit Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("driver_cockpit_banner"),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Slate100)
                                ) {
                                    AsyncImage(
                                        model = driver.avatarUrl,
                                        contentDescription = "Foto ${driver.name}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldVibrant)
                                        .border(2.dp, SurfaceWhite, CircleShape)
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Olá, ${driver.name.split(" ").first()}!",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Motorista Verificado",
                                        tint = AmberVibrant,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = Slate400,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${driver.vehicleModel.split(" ").take(2).joinToString(" ")} • ",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                    )
                                    Text(
                                        text = if (driver.isOnline) "Online" else "Offline",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (driver.isOnline) EmeraldDark else Slate500,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // Sincronizar Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100)
                                .clickable(onClick = onSyncFeed)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("sync_feed_button"),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Sincronizar",
                                        tint = BrandBlue,
                                        modifier = Modifier
                                            .size(15.dp)
                                            .rotate(if (isSyncing) rotationAngle else 0f)
                                    )
                                    Text(
                                        text = if (isSyncing) "Sincronizando..." else "Atualizar",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BrandBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    text = lastSyncTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate400,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }
                        }
                    }

                    // Quick Metagroup Metric Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100.copy(alpha = 0.8f))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "GANHOS HOJE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "R$ ${String.format("%.2f", driver.earningsToday)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Slate200))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "VIAGENS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "${driver.completedToday} concluídas",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Slate900,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Slate200))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AVALIAÇÃO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberVibrant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = String.format("%.2f", driver.rating),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Slate900,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Segmented Feed Switcher Tabs
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceContainerHigh.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple(TripCategory.MINHAS, minhasCount, "tab_minhas"),
                        Triple(TripCategory.DISPONIVEIS, disponiveisCount, "tab_disponiveis"),
                        Triple(TripCategory.TODAS, todasCount, "tab_todas")
                    )

                    tabs.forEach { (category, count, tag) ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                .clickable { onCategorySelected(category) }
                                .padding(vertical = 8.dp, horizontal = 6.dp)
                                .testTag(tag),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) Slate900 else Slate500
                                    )
                                )

                                val badgeBg = when {
                                    category == TripCategory.DISPONIVEIS -> AmberVibrant
                                    isSelected -> PrimaryContainer
                                    else -> Slate200
                                }
                                val badgeText = when {
                                    category == TripCategory.DISPONIVEIS -> Slate900
                                    isSelected -> SurfaceWhite
                                    else -> Slate500
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(badgeBg)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = badgeText,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trip Cards
        items(trips, key = { it.id }) { trip ->
            TripCardItem(
                trip = trip,
                onStartTrip = { onStartTrip(trip.id) },
                onClaimTrip = { onClaimTrip(trip.id) },
                onViewDetails = { onTripDetails(trip) },
                onWhatsApp = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${trip.passengerPhone.replace("+", "")}"))
                    context.startActivity(intent)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun TripCardItem(
    trip: TripItem,
    onStartTrip: () -> Unit,
    onClaimTrip: () -> Unit,
    onViewDetails: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stripeColor = when {
        trip.isAvailableToClaim -> AmberHighlight
        trip.status == TripStatusType.CONFIRMADO -> AmberVibrant
        else -> Slate300
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trip_card_${trip.id}"),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        shadowElevation = 1.5.dp
    ) {
        Column {
            // Top Accent Stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(stripeColor)
            )

            Column(modifier = Modifier.padding(14.dp)) {
                // Header: ID, Type, Schedule, Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val icon = when {
                            trip.isAvailableToClaim -> Icons.Default.Bolt
                            trip.transferType.contains("Noturno") -> Icons.Default.Hotel
                            else -> Icons.Default.FlightTakeoff
                        }
                        val iconTint = when {
                            trip.isAvailableToClaim -> AmberDeep
                            trip.transferType.contains("Noturno") -> Slate500
                            else -> BrandBlue
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = trip.id,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900,
                                        fontSize = 16.sp
                                    )
                                )

                                val typeBg = if (trip.isAvailableToClaim) EmeraldOriginBg else BrandBlueBg
                                val typeText = if (trip.isAvailableToClaim) EmeraldOriginText else BrandBlueDark

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(typeBg)
                                        .padding(horizontal = 7.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = trip.transferType,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = typeText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(top = 1.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = trip.timeLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate500,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Status Badge
                    val statusBg = when (trip.status) {
                        TripStatusType.CONFIRMADO -> AmberPendingBg
                        TripStatusType.LIVRE_NA_FROTA -> BrandBlueBg
                        else -> Slate100
                    }
                    val statusText = when (trip.status) {
                        TripStatusType.CONFIRMADO -> AmberPendingText
                        TripStatusType.LIVRE_NA_FROTA -> BrandBlueDark
                        else -> Slate500
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(statusBg)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (trip.status == TripStatusType.CONFIRMADO) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AmberDeep)
                                )
                            }
                            Text(
                                text = trip.statusBadgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = statusText,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Route Visual Timeline
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate100)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Origin
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(EmeraldOriginBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = trip.origin.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldOriginText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trip.origin.categoryTag.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldOriginText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = trip.origin.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = trip.origin.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // Vector Track
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(16.dp)
                                .background(Slate300)
                        )
                        Text(
                            text = trip.distanceInfo,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate500,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }

                    // Destination
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(DestRedBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = trip.destination.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DestRedText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trip.destination.categoryTag.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DestRedText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = trip.destination.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = trip.destination.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Passenger & Baggage Specs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = Slate500, modifier = Modifier.size(15.dp))
                            Text(text = "${trip.passengersCount} passageiros", style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 12.sp))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Luggage, contentDescription = null, tint = Slate500, modifier = Modifier.size(15.dp))
                            Text(text = trip.luggageInfo, style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 12.sp))
                        }
                    }

                    if (trip.specialPerk != null) {
                        if (trip.specialPerk.contains("Pedágios")) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = trip.specialPerk,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BrandBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberVibrant, modifier = Modifier.size(14.dp))
                                Text(text = trip.specialPerk, style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 11.sp))
                            }
                        }
                    }
                }

                // Passenger Contact Box (if assigned)
                if (!trip.isAvailableToClaim && trip.status == TripStatusType.CONFIRMADO) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = trip.passengerInitials,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SurfaceWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = trip.passengerName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = trip.passengerSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate500,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldLightBg)
                                .clickable(onClick = onWhatsApp)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "WhatsApp",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                // Payout Metric & Primary Driver Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = trip.payoutLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate500,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "R$ ${String.format("%.2f", trip.payoutAmount)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = EmeraldDark,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (trip.isAvailableToClaim) {
                        Button(
                            onClick = onClaimTrip,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberVibrant,
                                contentColor = Slate900
                            ),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("claim_trip_${trip.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Assumir esta Corrida",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    } else if (trip.status == TripStatusType.CONFIRMADO) {
                        Button(
                            onClick = onStartTrip,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Slate900,
                                contentColor = SurfaceWhite
                            ),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("start_trip_${trip.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = AmberVibrant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Iniciar Corrida",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    } else {
                        Button(
                            onClick = onViewDetails,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Slate100,
                                contentColor = Slate900
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("details_trip_${trip.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ver Detalhes",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
