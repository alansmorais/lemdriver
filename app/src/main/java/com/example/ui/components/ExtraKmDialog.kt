package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveTransfer
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberPendingBg
import com.example.ui.theme.AmberPendingText
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBg
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLightBg
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SurfaceWhite

@Composable
fun ExtraKmDialog(
    activeTransfer: ActiveTransfer,
    onApplyKm: (km: Double, rate: Double, reason: String?) -> Unit,
    onApplyOtherExtra: (amount: Double, reason: String) -> Unit,
    onClearExtras: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedKmInput by remember { mutableStateOf("") }
    var selectedRateInput by remember { mutableStateOf("3.50") }
    var reasonInput by remember { mutableStateOf("") }
    var extraStopAmount by remember { mutableDoubleStateOf(0.0) }
    var waitTimeAmount by remember { mutableDoubleStateOf(0.0) }

    val rate = selectedRateInput.toDoubleOrNull() ?: 3.50
    val kmValue = selectedKmInput.toDoubleOrNull() ?: 0.0
    val calculatedKmExtra = kmValue * rate
    val calculatedOtherExtras = extraStopAmount + waitTimeAmount
    val totalAdditional = calculatedKmExtra + calculatedOtherExtras
    val projectedTotalFare = activeTransfer.baseFareToCollect + activeTransfer.totalExtras + totalAdditional

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("extra_km_dialog"),
        title = {
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
                            .clip(CircleShape)
                            .background(AmberPendingBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddRoad,
                            contentDescription = null,
                            tint = AmberVibrant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cobrar KM Extra / Adicionais",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "Voucher ${activeTransfer.code.ifEmpty { activeTransfer.tripId }}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = Slate500,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Adicione quilometragem excedente ou serviços adicionais solicitados pelo passageiro durante o trajeto.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
                )

                // Quick KM Selector Buttons
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ATALHOS DE QUILOMETRAGEM EXTRA (+KM)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val quickKms = listOf(5, 10, 15, 20, 30)
                        quickKms.forEach { km ->
                            val isSelected = selectedKmInput == km.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AmberVibrant else Slate100)
                                    .clickable {
                                        selectedKmInput = km.toString()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+$km km",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) SurfaceWhite else Slate900,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                // Custom KM and Rate row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = selectedKmInput,
                        onValueChange = { selectedKmInput = it },
                        label = { Text("KM Rodados") },
                        placeholder = { Text("Ex: 12") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberVibrant,
                            unfocusedBorderColor = Slate200
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("km_input_field")
                    )

                    OutlinedTextField(
                        value = selectedRateInput,
                        onValueChange = { selectedRateInput = it },
                        label = { Text("R$/KM") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberVibrant,
                            unfocusedBorderColor = Slate200
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("rate_input_field")
                    )
                }

                // Quick Presets: Parada Extra & Espera
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "OUTROS SERVIÇOS ADICIONAIS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Parada extra button
                        val hasStop = extraStopAmount > 0
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (hasStop) BrandBlueBg else Slate100,
                            border = if (hasStop) androidx.compose.foundation.BorderStroke(1.5.dp, BrandBlue) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    extraStopAmount = if (hasStop) 0.0 else 25.0
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Parada Extra",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasStop) BrandBlue else Slate900
                                    )
                                )
                                Text(
                                    text = "+ R$ 25,00",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (hasStop) BrandBlue else Slate500
                                    )
                                )
                            }
                        }

                        // Tempo de espera button
                        val hasWait = waitTimeAmount > 0
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (hasWait) BrandBlueBg else Slate100,
                            border = if (hasWait) androidx.compose.foundation.BorderStroke(1.5.dp, BrandBlue) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    waitTimeAmount = if (hasWait) 0.0 else 30.0
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Espera Excedente",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasWait) BrandBlue else Slate900
                                    )
                                )
                                Text(
                                    text = "+ R$ 30,00",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (hasWait) BrandBlue else Slate500
                                    )
                                )
                            }
                        }
                    }
                }

                // Reason / Notes
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("Motivo / Observação (opcional)") },
                    placeholder = { Text("Ex: Desvio para pegar bagagem extra") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberVibrant,
                        unfocusedBorderColor = Slate200
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Current & Projected Breakdown Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Saldo Base no Embarque:",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                            Text(
                                text = "R$ ${String.format("%.2f", activeTransfer.baseFareToCollect)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate900
                                )
                            )
                        }

                        if (activeTransfer.totalExtras > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Extras Já Aplicados:",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                                Text(
                                    text = "+ R$ ${String.format("%.2f", activeTransfer.totalExtras)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = AmberHighlight
                                    )
                                )
                            }
                        }

                        if (totalAdditional > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Novo Adicional a Somar:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AmberHighlight
                                    )
                                )
                                Text(
                                    text = "+ R$ ${String.format("%.2f", totalAdditional)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AmberHighlight
                                    )
                                )
                            }
                        }

                        HorizontalDivider(
                            color = Slate200,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Novo Total a Cobrar:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Text(
                                text = "R$ ${String.format("%.2f", projectedTotalFare)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDark
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (kmValue > 0) {
                        onApplyKm(kmValue, rate, reasonInput.ifBlank { null })
                    }
                    if (calculatedOtherExtras > 0) {
                        val reasonParts = mutableListOf<String>()
                        if (extraStopAmount > 0) reasonParts.add("Parada Extra")
                        if (waitTimeAmount > 0) reasonParts.add("Tempo de Espera")
                        onApplyOtherExtra(calculatedOtherExtras, reasonParts.joinToString(" + "))
                    }
                    if (kmValue == 0.0 && calculatedOtherExtras == 0.0) {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldVibrant,
                    contentColor = SurfaceWhite
                ),
                modifier = Modifier.testTag("apply_extras_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PriceCheck,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Aplicar ao Saldo", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            if (activeTransfer.totalExtras > 0) {
                OutlinedButton(
                    onClick = onClearExtras,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate500)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpar Extras", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
                }
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(18.dp)
    )
}
