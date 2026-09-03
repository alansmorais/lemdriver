package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ActiveTransfer
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberPendingBg
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBg
import com.example.ui.theme.BrandBlueBorder
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
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBg
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceWhite

@Composable
fun EmRotaScreen(
    activeTransfer: ActiveTransfer?,
    onAdvanceStep: (Int) -> Unit,
    onCompleteTrip: () -> Unit,
    onReportIncident: () -> Unit,
    onNavigateBackToFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_step")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "step_scale"
    )

    if (activeTransfer == null) {
        // Empty State: No active route
        Surface(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBg)
                .padding(24.dp)
                .testTag("em_rota_empty_screen"),
            color = SlateBg
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(BrandBlueBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nenhuma viagem em rota ativa",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                )

                Text(
                    text = "Acesse a aba 'Viagens' para iniciar ou assumir um transfer da escala.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate500,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Button(
                    onClick = onNavigateBackToFeed,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900, contentColor = SurfaceWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver Grade de Viagens", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("em_rota_active_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        // Hero Timer Card (Dark Gradient Cockpit Card)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("active_timer_banner"),
            shape = RoundedCornerShape(18.dp),
            color = PrimaryContainer,
            shadowElevation = 2.dp
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberVibrant.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = AmberVibrant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${activeTransfer.remainingMinutes} min restantes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite,
                                    fontSize = 17.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(AmberVibrant)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "AO VIVO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate900,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "${activeTransfer.remainingDistanceKm} km • Chegada prevista às ${activeTransfer.estimatedArrival}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate300,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceWhite.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Atualizar Rota",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Etapas do Transfer (Interactive 4 Steps Timeline)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
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
                        text = "ETAPAS DO TRANSFER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandBlueBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        val stepLabel = when (activeTransfer.currentStepIndex) {
                            0 -> "1 de 4 • Despacho"
                            1 -> "2 de 4 • A caminho"
                            2 -> "3 de 4 • Em trânsito"
                            else -> "4 de 4 • Concluído"
                        }
                        Text(
                            text = stepLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandBlueDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // 4-Step Interactive Nodes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val stepNames = listOf("Despacho", "A caminho", "Embarcado", "Concluído")

                    stepNames.forEachIndexed { index, name ->
                        val isDone = index < activeTransfer.currentStepIndex
                        val isCurrent = index == activeTransfer.currentStepIndex

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAdvanceStep(index) }
                                .testTag("step_node_$index")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .scale(if (isCurrent) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> EmeraldDark
                                            isCurrent -> BrandBlue
                                            else -> Slate200
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isDone -> Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = SurfaceWhite,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    isCurrent -> Icon(
                                        imageVector = Icons.Default.AirlineSeatReclineNormal,
                                        contentDescription = null,
                                        tint = SurfaceWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    else -> Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = Slate500,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) BrandBlueDark else if (isDone) Slate900 else Slate500
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // GPS Map Navigation Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Column {
                // Map Image Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    AsyncImage(
                        model = activeTransfer.mapImageUrl,
                        contentDescription = "Mapa da Rota",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Badges
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceWhite.copy(alpha = 0.95f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AltRoute,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = activeTransfer.routeSummary,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate900,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldOriginBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = activeTransfer.trafficCondition,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldOriginText,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Origin -> Destination Addresses
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldOriginBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeTransfer.origin.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldOriginText,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(26.dp)
                                    .background(Slate300)
                            )
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(DestRedBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeTransfer.destination.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DestRedText,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = activeTransfer.origin.categoryTag.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate500,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = activeTransfer.origin.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                )
                                Text(
                                    text = activeTransfer.origin.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }

                            Column {
                                Text(
                                    text = activeTransfer.destination.categoryTag.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DestRedText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = activeTransfer.destination.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                )
                                Text(
                                    text = activeTransfer.destination.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }
                    }

                    // Waze & Google Maps Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://waze.com/ul?q=${Uri.encode(activeTransfer.destination.title)}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_open_waze"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Slate100)
                        ) {
                            Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Abrir no Waze", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(activeTransfer.destination.title)}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_open_maps"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Slate100)
                        ) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Google Maps", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                        }
                    }
                }
            }
        }

        // Passenger Details & Dispatcher Notes Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Passenger Header & Quick Contact Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Slate100)
                        ) {
                            AsyncImage(
                                model = activeTransfer.passengerAvatarUrl,
                                contentDescription = "Passageira",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = activeTransfer.passengerName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Passageiro VIP",
                                    tint = AmberVibrant,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = activeTransfer.passengerSubtitle.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${activeTransfer.passengerPhone}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate100)
                                .testTag("btn_call_passenger")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Ligar", tint = Slate900, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${activeTransfer.passengerPhone.replace("+", "")}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldLightBg)
                                .testTag("btn_whatsapp_passenger")
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = EmeraldDark, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Central Alert Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberHighlight.copy(alpha = 0.2f))
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = AmberDeep,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "AVISO DA CENTRAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AmberDeep,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = activeTransfer.centralAlert,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate900,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                // Payment Destination Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VALOR A RECEBER NO DESTINO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate500,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "R$ ${String.format("%.2f", activeTransfer.fareToCollect)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = EmeraldDark,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceWhite)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = AmberDeep, modifier = Modifier.size(13.dp))
                                Text(
                                    text = activeTransfer.paymentMode,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate700,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = activeTransfer.paymentNote,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 10.sp),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Action CTAs
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCompleteTrip,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldDark,
                    contentColor = SurfaceWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_complete_trip")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Concluir Viagem & Confirmar Pagamento",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            OutlinedButton(
                onClick = onReportIncident,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceWhite,
                    contentColor = Slate700
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_report_incident")
            ) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AmberDeep, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reportar Ocorrência / Trânsito", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
