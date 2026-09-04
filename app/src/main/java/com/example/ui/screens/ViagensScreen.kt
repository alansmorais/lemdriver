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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBg
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
    onAcceptTrip: (String) -> Unit = {},
    onDeclineTrip: (TripItem) -> Unit = {},
    onClaimTrip: (String) -> Unit,
    onTripDetails: (TripItem) -> Unit,
    onOpenSheetsConfig: () -> Unit = {},
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
        label = "sync_angle"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("viagens_screen"),
        color = SlateBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Driver Cockpit Card (Operational Identity)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Driver Initials Avatar
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceWhite.copy(alpha = 0.15f))
                                        .border(2.dp, EmeraldVibrant, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = driver.initials,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = SurfaceWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Column {
                                    Text(
                                        text = driver.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = SurfaceWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${driver.vehicleModel} • ${driver.vehiclePlate}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Slate300,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AmberHighlight.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Turno ${driver.shift}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = AmberHighlight,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Sync Sheets Button
                            Button(
                                onClick = onSyncFeed,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurfaceWhite.copy(alpha = 0.12f),
                                    contentColor = SurfaceWhite
                                ),
                                modifier = Modifier.testTag("sync_sheets_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sincronizar",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(if (isSyncing) rotationAngle else 0f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSyncing) "Sincronizando..." else "Sincronizar",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Spreadsheet Connection Status Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceWhite.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldVibrant)
                                )
                                Text(
                                    text = "Planilha Google Sheets Conectada",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate300,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Text(
                                text = "Última atualização: $lastSyncTime",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate300,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Category Segmented Tabs (Minhas / Disponíveis / Todas)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabs = listOf(
                            Triple(TripCategory.MINHAS, "Minhas Escalas", minhasCount),
                            Triple(TripCategory.DISPONIVEIS, "Disponíveis", disponiveisCount),
                            Triple(TripCategory.TODAS, "Todas da Frota", todasCount)
                        )

                        tabs.forEach { (category, label, count) ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                    .clickable { onCategorySelected(category) }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_${category.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Slate900 else Slate500,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (isSelected) BrandBlue else Slate200)
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) SurfaceWhite else Slate500,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Trips List
            if (trips.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceWhite,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Slate100),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = Slate500,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Text(
                                text = "Nenhuma escala agendada",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )

                            Text(
                                text = "Não há viagens cadastradas nesta categoria. Conecte sua planilha ou aguarde despachos da central.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = onSyncFeed,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryContainer,
                                    contentColor = SurfaceWhite
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSyncing) "Sincronizando..." else "Sincronizar Planilha",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            } else {
                items(trips, key = { it.id }) { trip ->
                    TripCard(
                        trip = trip,
                        onStartTrip = { onStartTrip(trip.id) },
                        onAcceptTrip = { onAcceptTrip(trip.id) },
                        onDeclineTrip = { onDeclineTrip(trip) },
                        onClaimTrip = { onClaimTrip(trip.id) },
                        onTripDetails = { onTripDetails(trip) },
                        onOpenWhatsApp = {
                            val cleanNumber = trip.passengerPhone.replace("+", "").replace(" ", "").replace("-", "")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNumber"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TripCard(
    trip: TripItem,
    onStartTrip: () -> Unit,
    onAcceptTrip: () -> Unit,
    onDeclineTrip: () -> Unit,
    onClaimTrip: () -> Unit,
    onTripDetails: () -> Unit,
    onOpenWhatsApp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTripDetails)
            .testTag("trip_card_${trip.id}"),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Code ID, Category, Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = trip.id,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberHighlight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = trip.transferType,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate500,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Status Badge
                val (statusBg, statusTextColor) = when (trip.status) {
                    TripStatusType.CONFIRMADO -> Pair(EmeraldOriginBg, EmeraldOriginText)
                    TripStatusType.LIVRE_NA_FROTA -> Pair(BrandBlueBg, BrandBlue)
                    TripStatusType.PENDENTE_ACEITE -> Pair(AmberPendingBg, AmberPendingText)
                    TripStatusType.AGENDADO -> Pair(AmberPendingBg, AmberPendingText)
                    TripStatusType.EM_ANDAMENTO -> Pair(EmeraldLightBg, EmeraldDark)
                    TripStatusType.CONCLUIDO -> Pair(Slate100, Slate500)
                    TripStatusType.RECUSADO -> Pair(DestRedBg, DestRedText)
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = trip.statusBadgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusTextColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Schedule & Time Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = trip.timeLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                )
            }

            // Origin -> Destination Timeline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateBg)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Origin
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(EmeraldOriginBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldOriginText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Column {
                        Text(
                            text = trip.origin.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate900
                            )
                        )
                        Text(
                            text = trip.origin.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Destination
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(DestRedBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "B",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DestRedText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Column {
                        Text(
                            text = trip.destination.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate900
                            )
                        )
                        Text(
                            text = trip.destination.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Passenger Info (Initials badge & Name + WhatsApp button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trip.passengerInitials,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column {
                        Text(
                            text = trip.passengerName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Slate500,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${trip.passengersCount} PAX",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Luggage,
                                    contentDescription = null,
                                    tint = Slate500,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = trip.luggageInfo,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }

                // WhatsApp Action Button
                IconButton(
                    onClick = onOpenWhatsApp,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldLightBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp Passageiro",
                        tint = EmeraldDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Spreadsheet Notes if any
            trip.notes?.let { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Obs Planilha: $note",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate700,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Payout Row & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (trip.remainingAmount > 0) "Saldo no Embarque (${trip.paymentMethod})" else "Valor Total (${trip.paymentMethod})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "R$ ${String.format("%.2f", if (trip.remainingAmount > 0) trip.remainingAmount else trip.payoutAmount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900,
                            fontSize = 18.sp
                        )
                    )
                }

                if (trip.isAvailableToClaim) {
                    Button(
                        onClick = onClaimTrip,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = SurfaceWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "Assumir Corrida",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else if (trip.isAssignedToMe && !trip.isAcceptedByDriver) {
                    // Step 2: Accept or Decline assigned ride
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onDeclineTrip,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DestRedText),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Recusar", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        Button(
                            onClick = onAcceptTrip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldVibrant,
                                contentColor = SurfaceWhite
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Aceitar", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onDeclineTrip,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Recusar", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                        }

                        Button(
                            onClick = onStartTrip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberVibrant,
                                contentColor = Slate900
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Iniciar",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
