package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CompletedTrip
import com.example.model.DriverProfile
import com.example.model.EarningsSummary
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBg
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
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBg
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceWhite

@Composable
fun GanhosScreen(
    driver: DriverProfile,
    earnings: EarningsSummary,
    completedTrips: List<CompletedTrip>,
    selectedTimeframe: String,
    onSelectTimeframe: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(horizontal = 16.dp)
            .testTag("ganhos_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(2.dp))

            // Timeframe Segmented Switcher
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceContainerHigh.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val periods = listOf("Semanal", "Quinzenal", "Mensal")
                    periods.forEach { period ->
                        val isSelected = selectedTimeframe == period
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                .clickable { onSelectTimeframe(period) }
                                .padding(vertical = 8.dp)
                                .testTag("timeframe_$period"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Slate900 else Slate500
                                )
                            )
                        }
                    }
                }
            }
        }

        // Hero Financial Card (Dark Theme)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_earnings_card"),
                shape = RoundedCornerShape(18.dp),
                color = PrimaryContainer,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL LÍQUIDO (${selectedTimeframe.uppercase()})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberHighlight,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldDark.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = EmeraldVibrant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "+18% vs anterior",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldVibrant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "R$ ${String.format("%.2f", earnings.weeklyTotal)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = SurfaceWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                    )

                    // Goal Progress
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${earnings.tripsCompletedWeekly} viagens realizadas",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                            )
                            Text(
                                text = "Meta: ${earnings.goalPercentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AmberHighlight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        LinearProgressIndicator(
                            progress = { earnings.goalPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AmberVibrant,
                            trackColor = Slate700,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    // Secondary Payout Breakdown Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900.copy(alpha = 0.6f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "RECEBIDO NO LOCAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = "R$ ${String.format("%.2f", earnings.receivedOnSite)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = SurfaceWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Slate700))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "ACERTO FROTA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = "R$ ${String.format("%.2f", earnings.fleetSettlement)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = AmberHighlight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // Driver Spotlight Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate100)
                    ) {
                        AsyncImage(
                            model = driver.avatarUrl,
                            contentDescription = "Foto ${driver.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = driver.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = AmberVibrant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "${driver.vehicleModel} • ${driver.vehiclePlate}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberVibrant, modifier = Modifier.size(14.dp))
                            Text(
                                text = "${String.format("%.2f", driver.rating)} (${driver.completedTripsCount} corridas)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate700,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmberHighlight.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Destaque",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberDeep,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        // Histórico Recente Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTÓRICO RECENTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate500,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = "Ver Todos",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BrandBlue,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onShowToast("Histórico completo disponível na planilha.") }
                )
            }
        }

        items(completedTrips, key = { it.id }) { trip ->
            CompletedTripItem(trip = trip)
        }

        // PIX Key & Settlement Info
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                            Text(
                                text = "CHAVE PIX CADASTRADA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Slate100)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("PIX", driver.pixKey))
                                    onShowToast("Chave PIX copiada!")
                                }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Slate700, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Copiar",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate700,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = driver.pixKey,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Slate900,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = "Os repasses das corridas faturadas pela frota ocorrem todas as terças-feiras.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate500,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // WhatsApp Suporte Financeiro
        item {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/5513999999999"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_financial_support"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldLightBg,
                    contentColor = EmeraldDark
                )
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Central de Repasses & Financeiro", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun CompletedTripItem(
    trip: CompletedTrip,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("completed_trip_${trip.id}"),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldLightBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = trip.passengerName,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            ),
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldOriginBg)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = trip.statusText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldOriginText,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "${trip.origin} → ${trip.destination}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate500,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(top = 1.dp)
                    )

                    Text(
                        text = trip.timeLabel,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Text(
                text = "R$ ${String.format("%.2f", trip.fareAmount)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDark
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
