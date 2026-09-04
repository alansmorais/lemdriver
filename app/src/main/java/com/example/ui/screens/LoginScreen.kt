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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DriverProfile
import com.example.ui.components.AddDriverDialog
import com.example.ui.components.ChangePasswordDialog
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBg
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLightBg
import com.example.ui.theme.EmeraldOriginBg
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SlateBg
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceWhite

@Composable
fun LoginScreen(
    availableDrivers: List<DriverProfile>,
    lastSyncTime: String = "",
    isSyncing: Boolean = false,
    onLogin: (driverId: String, pin: String, shift: String) -> Unit,
    onSyncFeed: () -> Unit = {},
    onChangePin: (driverId: String, currentPin: String, newPin: String) -> Boolean = { _, _, _ -> true },
    onRegisterNewDriver: (name: String, phone: String, vehicleModel: String, vehiclePlate: String, pixKey: String) -> DriverProfile = { n, p, v, pl, px ->
        DriverProfile(
            id = "drv-new",
            name = n,
            phone = p,
            vehicleModel = v,
            vehiclePlate = pl,
            pixKey = px
        )
    },
    onPlayTestSound: () -> Unit = {},
    onOpenSheetsConfig: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDriver by remember(availableDrivers) {
        mutableStateOf(availableDrivers.firstOrNull())
    }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("2026") }
    var isPinVisible by remember { mutableStateOf(false) }
    var selectedShift by remember { mutableStateOf("Manhã") }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showAddDriverDialog by remember { mutableStateOf(false) }

    // Synchronize immediately upon opening the screen so any new driver is fetched automatically!
    LaunchedEffect(Unit) {
        onSyncFeed()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_beacon_login")
    val beaconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_scale_login"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        color = SlateBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Server Live Header with instant sync trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(beaconScale)
                                .clip(CircleShape)
                                .background(EmeraldVibrant.copy(alpha = 0.4f))
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EmeraldVibrant)
                        )
                    }
                    Text(
                        text = if (isSyncing) "Sincronizando..." else "Servidor Ao Vivo",
                        style = MaterialTheme.typography.labelMedium.copy(color = Slate500)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Sync Button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandBlueBg)
                            .clickable { onSyncFeed() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = BrandBlue
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sincronizar",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "SINCRONIZAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Server Live Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldLightBg)
                            .clickable { onOpenSheetsConfig() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SERVER LIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Executive Transport Banner (Dark Card with Official LEM Logo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(PrimaryContainer)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.ui.components.LemBrandLogo(
                        size = 80.dp,
                        showTypography = true,
                        isDarkTheme = true
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceWhite.copy(alpha = 0.10f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PORTAL DO MOTORISTA • DESPACHO LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberHighlight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Form Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceWhite,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Driver Selector Row Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Motorista Cadastrado",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )

                        Text(
                            text = "${availableDrivers.size} motorista(s) no sistema",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Driver Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100)
                                .clickable { dropdownExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 13.dp)
                                .testTag("driver_select_dropdown"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = selectedDriver?.let { "${it.name} (${it.vehicleModel})" } ?: "Selecione seu perfil na frota...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (selectedDriver != null) Slate900 else Slate500,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Slate500
                            )
                        }

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.background(SurfaceWhite)
                        ) {
                            availableDrivers.forEach { driver ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(driver.name, fontWeight = FontWeight.Bold, color = Slate900)
                                            Text("${driver.vehicleModel} • ${driver.vehiclePlate}", color = Slate500, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        selectedDriver = driver
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Quick Actions Row: Add Driver + First Access/Change PIN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // First Access / Change PIN button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BrandBlueBg,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (selectedDriver != null) {
                                        showChangePasswordDialog = true
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "1º Acesso / Mudar Senha",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BrandBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Add Driver button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate100,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showAddDriverDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = Slate700,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+ Novo Motorista",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Slate700,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Passcode Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Código de Acesso do Turno",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                            Text(
                                text = "PIN 4 Dígitos",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AmberDeep,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }

                        OutlinedTextField(
                            value = pinCode,
                            onValueChange = { if (it.length <= 8) pinCode = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pin_code_input"),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("••••", color = Slate500) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Pin, contentDescription = null, tint = Slate500)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                    Icon(
                                        imageVector = if (isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Alternar visibilidade",
                                        tint = Slate500
                                    )
                                }
                            },
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate100,
                                unfocusedContainerColor = Slate100,
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Text(
                            text = "💡 1º acesso: 4 últimos dígitos do celular ou 2026. Admin SU possui acesso total mestre.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Shift Selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SELECIONE O TURNO",
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
                            val shifts = listOf(
                                Triple("Manhã", Icons.Default.WbSunny, "manha"),
                                Triple("Tarde", Icons.Default.WbTwilight, "tarde"),
                                Triple("Noite", Icons.Default.DarkMode, "noite")
                            )

                            shifts.forEach { (name, icon, tag) ->
                                val isSelected = selectedShift == name
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Slate200 else Slate100)
                                        .clickable { selectedShift = name }
                                        .padding(vertical = 10.dp)
                                        .testTag("shift_btn_$tag"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = name,
                                            tint = if (isSelected) Slate900 else Slate500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Slate900 else Slate500
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Login Action Button
                    Button(
                        onClick = {
                            selectedDriver?.let {
                                onLogin(it.id, pinCode, selectedShift)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberVibrant,
                            contentColor = Slate900
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Entrar no Portal",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Admin SU and Audio Notification Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Admin SU: Acesso Livre Mestre",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                    }

                    // Test Sound Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate200)
                            .clickable { onPlayTestSound() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = Slate700,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Testar Som",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate700,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Central de Despacho Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = EmeraldLightBg
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
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
                                .background(SurfaceWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Central de Despacho",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            )
                            Text(
                                text = "Dúvidas de escala ou planilha?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/5512988506597"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldOriginBg,
                            contentColor = EmeraldDark
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Developer Credit Footer (AlanSMSolutions.com)
            com.example.ui.components.DeveloperCreditFooter(isDarkTheme = false)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Dialogs
    if (showChangePasswordDialog && selectedDriver != null) {
        val drv = selectedDriver!!
        val cleanPhoneDigits = drv.phone.filter { it.isDigit() }
        val defaultHint = if (cleanPhoneDigits.length >= 4) cleanPhoneDigits.takeLast(4) else "2026"

        ChangePasswordDialog(
            driver = drv,
            defaultPinHint = defaultHint,
            onDismiss = { showChangePasswordDialog = false },
            onConfirmChange = { currentPin, newPin ->
                val success = onChangePin(drv.id, currentPin, newPin)
                if (success) {
                    pinCode = newPin
                }
                success
            }
        )
    }

    if (showAddDriverDialog) {
        AddDriverDialog(
            onDismiss = { showAddDriverDialog = false },
            onSaveDriver = { name, phone, vehicleModel, vehiclePlate, pixKey ->
                val newDriver = onRegisterNewDriver(name, phone, vehicleModel, vehiclePlate, pixKey)
                selectedDriver = newDriver
                newDriver
            }
        )
    }
}
