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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackendConfig
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.BrandBlue
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
fun SheetsConfigDialog(
    currentUrl: String,
    backendConfig: BackendConfig,
    onSaveUrlAndSync: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var urlInput by remember { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("sheets_config_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EmeraldLightBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Conexão com Servidor Central",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "Litoral em Movimento • Servidor Live",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Official Backend Info Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = backendConfig.companyName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                        )
                        Text(
                            text = "Frota Oficial: ${backendConfig.fleetModel}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                        )
                        Text(
                            text = "Chave PIX Oficial: ${backendConfig.officialPixKey}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AmberHighlight)
                        )
                        Text(
                            text = "Suporte WhatsApp: ${backendConfig.contactWhatsapp}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                        )
                    }
                }

                Text(
                    text = "URL DO SERVIDOR OPERACIONAL (ENDPOINT LIVE)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate500,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                )

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    placeholder = { Text("https://.../exec") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Slate500)
                    },
                    trailingIcon = {
                        if (urlInput.isNotBlank()) {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(urlInput))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar URL",
                                    tint = Slate500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldVibrant,
                        unfocusedBorderColor = Slate200
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sheets_url_input")
                )

                Text(
                    text = "Endpoint seguro do servidor para despacho em tempo real, escalas e controle operacional de passageiros.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate500,
                        fontSize = 11.sp
                    )
                )

                // WhatsApp Support Button
                OutlinedButton(
                    onClick = {
                        val cleanPhone = backendConfig.contactWhatsapp.filter { it.isDigit() }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/55$cleanPhone?text=Ol%C3%A1%20Central%20LEM%2C%20preciso%20de%20suporte%20com%20a%20conexao%20do%20aplicativo"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Suporte Operacional WhatsApp",
                        style = MaterialTheme.typography.labelMedium.copy(color = Slate900)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveUrlAndSync(urlInput)
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldVibrant,
                    contentColor = SurfaceWhite
                ),
                modifier = Modifier.testTag("save_and_sync_sheets_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salvar & Sincronizar", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Fechar", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(18.dp)
    )
}
