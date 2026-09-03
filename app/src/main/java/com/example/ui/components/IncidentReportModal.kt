package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.AmberPendingBg
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.DestRedBg
import com.example.ui.theme.DestRedText
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLightBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.theme.SurfaceWhite

data class IncidentOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val bgTint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportModal(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val options = listOf(
        IncidentOption(
            title = "Trânsito Intenso",
            subtitle = "Congestionamento na serra ou rodovia",
            icon = Icons.Default.Traffic,
            iconTint = AmberVibrant,
            bgTint = AmberPendingBg
        ),
        IncidentOption(
            title = "Acidente na Via",
            subtitle = "Interdição parcial ou total de faixa",
            icon = Icons.Default.CarCrash,
            iconTint = DestRedText,
            bgTint = DestRedBg
        ),
        IncidentOption(
            title = "Pneu / Falha Mecânica",
            subtitle = "Problema técnico no veículo da frota",
            icon = Icons.Default.Build,
            iconTint = BrandBlue,
            bgTint = Slate100
        ),
        IncidentOption(
            title = "Parada Solicitada",
            subtitle = "A pedido do passageiro executivo",
            icon = Icons.Default.PersonPinCircle,
            iconTint = EmeraldDark,
            bgTint = EmeraldLightBg
        )
    )

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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("incident_report_modal")
        ) {
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberPendingBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = AmberDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Registrar Ocorrência",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = Slate500
                    )
                }
            }

            Text(
                text = "Selecione o motivo para notificar o suporte operacional e o cliente instantaneamente:",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate500,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate100)
                            .clickable { onSubmit(option.title) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(option.bgTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                tint = option.iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
