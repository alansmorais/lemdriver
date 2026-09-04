package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.model.AppScreen
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.components.ConfirmCompleteDialog
import com.example.ui.components.DeclineRideDialog
import com.example.ui.components.ExtraKmDialog
import com.example.ui.components.IncidentReportModal
import com.example.ui.components.NotificationSheet
import com.example.ui.components.SheetsConfigDialog
import com.example.ui.components.ToastNotification
import com.example.ui.components.TripDetailSheet
import com.example.ui.screens.EmRotaScreen
import com.example.ui.screens.GanhosScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PerfilScreen
import com.example.ui.screens.ViagensScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBg
import com.example.viewmodel.DriverViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DriverViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                DriverApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DriverApp(
    viewModel: DriverViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentDriver by viewModel.currentDriver.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val availableDrivers by viewModel.availableDrivers.collectAsState()
    val trips by viewModel.filteredTrips.collectAsState()
    val selectedCategory by viewModel.selectedTripCategory.collectAsState()
    val minhasCount by viewModel.minhasCount.collectAsState()
    val disponiveisCount by viewModel.disponiveisCount.collectAsState()
    val todasCount by viewModel.todasCount.collectAsState()
    val activeTransfer by viewModel.activeTransfer.collectAsState()
    val earnings by viewModel.earnings.collectAsState()
    val completedTrips by viewModel.completedTrips.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val toastIcon by viewModel.toastIcon.collectAsState()
    val selectedTripForDetails by viewModel.selectedTripForDetails.collectAsState()
    val tripToDecline by viewModel.tripToDecline.collectAsState()
    val showExtraKmDialog by viewModel.showExtraKmDialog.collectAsState()
    val showSheetsConfigDialog by viewModel.showSheetsConfigDialog.collectAsState()
    val showIncidentModal by viewModel.showIncidentModal.collectAsState()
    val showNotificationSheet by viewModel.showNotificationSheet.collectAsState()
    val showConfirmCompleteDialog by viewModel.showConfirmCompleteDialog.collectAsState()
    val backendConfig by viewModel.backendConfig.collectAsState()

    val showBottomNav = isLoggedIn && currentScreen != AppScreen.LOGIN

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (showBottomNav) {
                AppHeader(
                    currentScreen = currentScreen,
                    driver = currentDriver,
                    unreadNotificationsCount = notifications.size,
                    onNotificationClick = { viewModel.openNotificationSheet() },
                    onProfileClick = { viewModel.navigateTo(AppScreen.PERFIL) }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                AppBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateBg)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.LOGIN -> LoginScreen(
                        availableDrivers = availableDrivers,
                        lastSyncTime = lastSyncTime,
                        isSyncing = isSyncing,
                        onLogin = { id, pin, shift -> viewModel.login(id, pin, shift) },
                        onSyncFeed = { viewModel.syncFeed() },
                        onChangePin = { id, currentPin, newPin -> viewModel.changeDriverPin(id, currentPin, newPin) },
                        onRegisterNewDriver = { name, phone, vehicle, plate, pix -> viewModel.registerNewDriver(name, phone, vehicle, plate, pix) },
                        onPlayTestSound = { viewModel.playTestSound() },
                        onOpenSheetsConfig = { viewModel.openSheetsConfigDialog() }
                    )

                    AppScreen.VIAGENS -> ViagensScreen(
                        driver = currentDriver,
                        earnings = earnings,
                        trips = trips,
                        selectedCategory = selectedCategory,
                        minhasCount = minhasCount,
                        disponiveisCount = disponiveisCount,
                        todasCount = todasCount,
                        lastSyncTime = lastSyncTime,
                        isSyncing = isSyncing,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        onSyncFeed = { viewModel.syncFeed() },
                        onStartTrip = { viewModel.startTrip(it) },
                        onAcceptTrip = { viewModel.acceptTrip(it) },
                        onDeclineTrip = { viewModel.promptDeclineTrip(it) },
                        onClaimTrip = { viewModel.claimTrip(it) },
                        onTripDetails = { viewModel.openTripDetails(it) },
                        onOpenSheetsConfig = { viewModel.openSheetsConfigDialog() }
                    )

                    AppScreen.EM_ROTA -> EmRotaScreen(
                        activeTransfer = activeTransfer,
                        onAdvanceStep = { viewModel.advanceStep(it) },
                        onCompleteTrip = { viewModel.openConfirmCompleteDialog() },
                        onOpenExtraKmDialog = { viewModel.openExtraKmDialog() },
                        onReportIncident = { viewModel.openIncidentModal() },
                        onNavigateBackToFeed = { viewModel.navigateTo(AppScreen.VIAGENS) }
                    )

                    AppScreen.GANHOS -> GanhosScreen(
                        driver = currentDriver,
                        earnings = earnings,
                        completedTrips = completedTrips,
                        selectedTimeframe = selectedTimeframe,
                        onSelectTimeframe = { viewModel.selectTimeframe(it) },
                        onShowToast = { viewModel.showToast(it, "check_circle") }
                    )

                    AppScreen.PERFIL -> PerfilScreen(
                        driver = currentDriver,
                        onToggleOnline = { viewModel.toggleOnline() },
                        onLogout = { viewModel.logout() },
                        onChangePin = { id, currentPin, newPin -> viewModel.changeDriverPin(id, currentPin, newPin) },
                        onPlayTestSound = { viewModel.playTestSound() }
                    )
                }
            }

            // Floating Toast Notification
            ToastNotification(
                message = toastMessage,
                iconType = toastIcon,
                onDismiss = { viewModel.dismissToast() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Modals & Bottom Sheets
            if (showIncidentModal) {
                IncidentReportModal(
                    onDismiss = { viewModel.closeIncidentModal() },
                    onSubmit = { reason -> viewModel.submitIncident(reason) }
                )
            }

            if (showNotificationSheet) {
                NotificationSheet(
                    notifications = notifications,
                    onDismiss = { viewModel.closeNotificationSheet() }
                )
            }

            if (selectedTripForDetails != null) {
                TripDetailSheet(
                    trip = selectedTripForDetails,
                    onDismiss = { viewModel.closeTripDetails() },
                    onStartTrip = { viewModel.startTrip(it) },
                    onClaimTrip = { viewModel.claimTrip(it) }
                )
            }

            if (showConfirmCompleteDialog && activeTransfer != null) {
                ConfirmCompleteDialog(
                    amount = activeTransfer?.fareToCollect ?: 0.0,
                    passengerName = activeTransfer?.passengerName ?: "",
                    onConfirm = { viewModel.completeTrip() },
                    onDismiss = { viewModel.closeConfirmCompleteDialog() }
                )
            }

            // Decline Trip Modal
            tripToDecline?.let { trip ->
                DeclineRideDialog(
                    trip = trip,
                    onConfirmDecline = { reason -> viewModel.confirmDeclineTrip(reason) },
                    onDismiss = { viewModel.dismissDeclineTrip() }
                )
            }

            // Extra KM & Extras Modal
            if (showExtraKmDialog && activeTransfer != null) {
                ExtraKmDialog(
                    activeTransfer = activeTransfer!!,
                    onApplyKm = { km, rate, reason ->
                        viewModel.addExtraKm(km, rate, reason)
                    },
                    onApplyOtherExtra = { amount, reason ->
                        viewModel.addOtherExtras(amount, reason)
                    },
                    onClearExtras = {
                        viewModel.clearExtras()
                    },
                    onDismiss = { viewModel.closeExtraKmDialog() }
                )
            }

            // Google Sheets Integration Config Dialog
            if (showSheetsConfigDialog) {
                SheetsConfigDialog(
                    currentUrl = viewModel.getWebAppUrl(),
                    backendConfig = backendConfig,
                    onSaveUrlAndSync = { newUrl ->
                        viewModel.setWebAppUrl(newUrl)
                    },
                    onDismiss = { viewModel.closeSheetsConfigDialog() }
                )
            }
        }
    }
}
