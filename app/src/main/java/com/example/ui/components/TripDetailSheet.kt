package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TripItem
import com.example.model.TripStatusType
import com.example.ui.theme.AmberDeep
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
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailSheet(
    trip: TripItem?,
    onDismiss: () -> Unit,
    onStartTrip: (String) -> Unit,
    onClaimTrip: (String) -> Unit
) {
    if (trip == null) return
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 48.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Slate300)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("trip_detail_sheet")
        ) {
            // Header
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
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
                                    color = Slate900
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(BrandBlueBg)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = trip.transferType,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BrandBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = trip.timeLabel,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = Slate500)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Route Node Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Slate100)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Origin
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(EmeraldOriginBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trip.origin.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldOriginText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Column {
                        Text(
                            text = trip.origin.categoryTag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldOriginText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = trip.origin.title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                        Text(
                            text = trip.origin.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                // Dotted track connector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 11.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(20.dp)
                            .background(Slate300)
                    )
                    Text(
                        text = trip.distanceInfo,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }

                // Destination
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DestRedBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trip.destination.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DestRedText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Column {
                        Text(
                            text = trip.destination.categoryTag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DestRedText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = trip.destination.title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                        Text(
                            text = trip.destination.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Specs (Passageiros, Malas, Inclusos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                        Text(text = "${trip.passengersCount} passageiros", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Luggage, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                        Text(text = trip.luggageInfo, style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                    }
                }

                if (trip.specialPerk != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberVibrant, modifier = Modifier.size(14.dp))
                        Text(text = trip.specialPerk, style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 11.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Passenger Box & WhatsApp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate100)
                    .padding(12.dp),
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
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                        Text(
                            text = trip.passengerSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${trip.passengerPhone.replace("+", "")}"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = EmeraldLightBg,
                        contentColor = EmeraldDark
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payout Section & CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = trip.payoutLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "R$ ${String.format("%.2f", trip.payoutAmount)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = EmeraldVibrant,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                if (trip.isAvailableToClaim) {
                    Button(
                        onClick = {
                            onClaimTrip(trip.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberVibrant, contentColor = Slate900),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Text("Assumir esta Corrida", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                } else {
                    Button(
                        onClick = {
                            onStartTrip(trip.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900, contentColor = SurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = AmberVibrant, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Iniciar Corrida", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
