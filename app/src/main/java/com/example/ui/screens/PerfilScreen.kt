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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DriverProfile
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberVibrant
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
fun PerfilScreen(
    driver: DriverProfile,
    onToggleOnline: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("perfil_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        // Driver Hero Profile Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
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
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (driver.isOnline) EmeraldVibrant else Slate500)
                            .border(3.dp, SurfaceWhite, CircleShape)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = driver.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                fontSize = 18.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = AmberVibrant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${driver.vehicleModel} • Placa ${driver.vehiclePlate}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Slate500,
                            fontSize = 13.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberVibrant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${String.format("%.2f", driver.rating)} • ${driver.completedTripsCount} viagens concluídas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Slate700,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Online/Offline Status Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (driver.isOnline) EmeraldLightBg else Slate100)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (driver.isOnline) EmeraldDark else Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (driver.isOnline) "Disponível na Frota (Online)" else "Indisponível (Offline)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (driver.isOnline) EmeraldDark else Slate900
                                )
                            )
                            Text(
                                text = if (driver.isOnline) "Recebendo despachos em tempo real" else "Escala pausada",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = driver.isOnline,
                        onCheckedChange = { onToggleOnline() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SurfaceWhite,
                            checkedTrackColor = EmeraldVibrant
                        )
                    )
                }
            }
        }

        // Fleet Standard Checklist (Padrão de Atendimento Executivo)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AmberDeep, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Padrão de Atendimento Frota",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    )
                }

                val standards = listOf(
                    "Traje executivo / camisa polo oficial Litoral",
                    "Veículo higienizado e ar-condicionado calibrado",
                    "Água mineral e Wi-Fi de bordo disponíveis",
                    "Waze / Google Maps atualizados com trânsito em tempo real"
                )

                standards.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(EmeraldOriginBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldOriginText, modifier = Modifier.size(12.dp))
                        }
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate700,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Turno & Detalhes Operacionais
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DADOS CADASTRAIS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate500,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Turno Ativo", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                    Text(driver.shift, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Telefone WhatsApp", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                    Text(driver.phone, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Base Operacional", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                    Text("Santos / Baixada Santista", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                }
            }
        }

        // Logout Action
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_logout"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DestRedBg,
                contentColor = DestRedText
            )
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Trocar de Motorista / Encerrar Turno", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
