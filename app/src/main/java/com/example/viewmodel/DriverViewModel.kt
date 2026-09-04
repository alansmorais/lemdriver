package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DriverRepository
import com.example.model.ActiveTransfer
import com.example.model.AppScreen
import com.example.model.BackendConfig
import com.example.model.CompletedTrip
import com.example.model.DriverProfile
import com.example.model.EarningsSummary
import com.example.model.FleetNotification
import com.example.model.TripCategory
import com.example.model.TripItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DriverViewModel(
    private val repository: DriverRepository = DriverRepository()
) : ViewModel() {

    val availableDrivers: StateFlow<List<DriverProfile>> = repository.availableDrivers
    val backendConfig: StateFlow<BackendConfig> = repository.backendConfig

    val currentDriver: StateFlow<DriverProfile> = repository.currentDriver
    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
    val trips: StateFlow<List<TripItem>> = repository.trips
    val activeTransfer: StateFlow<ActiveTransfer?> = repository.activeTransfer
    val earnings: StateFlow<EarningsSummary> = repository.earnings
    val completedTrips: StateFlow<List<CompletedTrip>> = repository.completedTrips
    val notifications: StateFlow<List<FleetNotification>> = repository.notifications
    val lastSyncTime: StateFlow<String> = repository.lastSyncTime

    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedTripCategory = MutableStateFlow(TripCategory.MINHAS)
    val selectedTripCategory: StateFlow<TripCategory> = _selectedTripCategory.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("Semanal")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _toastIcon = MutableStateFlow("check_circle")
    val toastIcon: StateFlow<String> = _toastIcon.asStateFlow()

    private val _selectedTripForDetails = MutableStateFlow<TripItem?>(null)
    val selectedTripForDetails: StateFlow<TripItem?> = _selectedTripForDetails.asStateFlow()

    private val _tripToDecline = MutableStateFlow<TripItem?>(null)
    val tripToDecline: StateFlow<TripItem?> = _tripToDecline.asStateFlow()

    private val _showExtraKmDialog = MutableStateFlow(false)
    val showExtraKmDialog: StateFlow<Boolean> = _showExtraKmDialog.asStateFlow()

    private val _showSheetsConfigDialog = MutableStateFlow(false)
    val showSheetsConfigDialog: StateFlow<Boolean> = _showSheetsConfigDialog.asStateFlow()

    private val _showIncidentModal = MutableStateFlow(false)
    val showIncidentModal: StateFlow<Boolean> = _showIncidentModal.asStateFlow()

    private val _showNotificationSheet = MutableStateFlow(false)
    val showNotificationSheet: StateFlow<Boolean> = _showNotificationSheet.asStateFlow()

    private val _showConfirmCompleteDialog = MutableStateFlow(false)
    val showConfirmCompleteDialog: StateFlow<Boolean> = _showConfirmCompleteDialog.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        // Automatically sync on launch
        viewModelScope.launch {
            repository.syncWithGoogleSheets()
        }
    }

    val filteredTrips: StateFlow<List<TripItem>> = combine(trips, _selectedTripCategory, currentDriver) { allTrips, category, driver ->
        when (category) {
            TripCategory.MINHAS -> allTrips.filter { it.isAssignedToMe || it.assignedDriverName?.contains(driver.name, ignoreCase = true) == true }
            TripCategory.DISPONIVEIS -> allTrips.filter { it.isAvailableToClaim || it.assignedDriverName.isNullOrBlank() }
            TripCategory.TODAS -> allTrips
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val minhasCount: StateFlow<Int> = combine(trips, currentDriver) { allTrips, driver ->
        allTrips.count { it.isAssignedToMe || it.assignedDriverName?.contains(driver.name, ignoreCase = true) == true }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val disponiveisCount: StateFlow<Int> = trips.combine(MutableStateFlow(Unit)) { allTrips, _ ->
        allTrips.count { it.isAvailableToClaim || it.assignedDriverName.isNullOrBlank() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todasCount: StateFlow<Int> = trips.combine(MutableStateFlow(Unit)) { allTrips, _ ->
        allTrips.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun getWebAppUrl(): String = repository.getWebAppUrl()

    fun setWebAppUrl(url: String) {
        repository.setWebAppUrl(url)
        viewModelScope.launch {
            syncFeed()
        }
    }

    fun openSheetsConfigDialog() {
        _showSheetsConfigDialog.value = true
    }

    fun closeSheetsConfigDialog() {
        _showSheetsConfigDialog.value = false
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectCategory(category: TripCategory) {
        _selectedTripCategory.value = category
    }

    fun selectTimeframe(timeframe: String) {
        _selectedTimeframe.value = timeframe
    }

    // Step 1: Login with Driver Name + PIN
    fun login(driverNameOrId: String, pin: String, shift: String) {
        val success = repository.login(driverNameOrId, pin, shift)
        if (success) {
            _currentScreen.value = AppScreen.VIAGENS
            showToast("Bem-vindo(a), ${currentDriver.value.name}! Escalas conectadas.", "verified")
        } else {
            showToast("PIN inválido. Tente os 4 últimos dígitos do celular ou 2026.", "error")
        }
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = AppScreen.LOGIN
        showToast("Sessão finalizada com sucesso.", "logout")
    }

    fun toggleOnline() {
        repository.toggleOnlineStatus()
        val status = if (currentDriver.value.isOnline) "Status: Online na frota Chevrolet Spin" else "Status: Offline / Indisponível"
        showToast(status, "directions_car")
    }

    fun syncFeed() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncWithGoogleSheets()
            _isSyncing.value = false
            if (result.isSuccess) {
                showToast("Servidor sincronizado! ${result.getOrNull()} reservas carregadas.", "cloud_done")
            } else {
                showToast("Conexão ativa com o sistema LEM Motoristas.", "sync")
            }
        }
    }

    // Step 2: Accept or Decline assigned ride
    fun acceptTrip(tripId: String) {
        viewModelScope.launch {
            repository.acceptTrip(tripId)
            showToast("Corrida aceita com sucesso! Pronto para iniciar.", "check_circle")
        }
    }

    fun promptDeclineTrip(trip: TripItem) {
        _tripToDecline.value = trip
    }

    fun dismissDeclineTrip() {
        _tripToDecline.value = null
    }

    fun confirmDeclineTrip(reason: String) {
        val trip = _tripToDecline.value ?: return
        _tripToDecline.value = null
        viewModelScope.launch {
            repository.declineTrip(trip.id, reason)
            showToast("Corrida ${trip.code.ifEmpty { trip.id }} recusada e devolvida ao despacho.", "cancel")
        }
    }

    fun claimTrip(tripId: String) {
        repository.claimTrip(tripId)
        showToast("Corrida assumida com sucesso!", "verified")
    }

    // Step 3: Start trip & In Route
    fun startTrip(tripId: String) {
        viewModelScope.launch {
            showToast("Iniciando transfer... GPS pronto.", "navigation")
            repository.startTrip(tripId)
            _currentScreen.value = AppScreen.EM_ROTA
        }
    }

    fun advanceStep(stepIndex: Int) {
        repository.updateTransferStep(stepIndex)
        val stepNames = listOf("Despacho Aceito", "A caminho do Embarque", "Passageiro a Bordo", "Em Rota ao Destino", "Chegada / Finalizar")
        val label = stepNames.getOrElse(stepIndex) { "Etapa Atualizada" }
        showToast(label, "route")
    }

    // Extra KM & Additional fees
    fun openExtraKmDialog() {
        _showExtraKmDialog.value = true
    }

    fun closeExtraKmDialog() {
        _showExtraKmDialog.value = false
    }

    fun addExtraKm(additionalKm: Double, ratePerKm: Double = 3.50, reason: String? = null) {
        repository.addExtraKm(additionalKm, ratePerKm, reason)
        _showExtraKmDialog.value = false
        val totalExtraValue = additionalKm * ratePerKm
        showToast("+${String.format("%.1f", additionalKm)} KM Extra adicionado (+R$ ${String.format("%.2f", totalExtraValue)})", "add_circle")
    }

    fun addOtherExtras(amount: Double, reason: String) {
        repository.addOtherExtras(amount, reason)
        _showExtraKmDialog.value = false
        showToast("+R$ ${String.format("%.2f", amount)} extra adicionado ($reason)", "price_check")
    }

    fun clearExtras() {
        repository.clearExtras()
        _showExtraKmDialog.value = false
        showToast("Valores extras resetados ao valor original.", "restart_alt")
    }

    // Step 4: Complete trip
    fun openConfirmCompleteDialog() {
        _showConfirmCompleteDialog.value = true
    }

    fun closeConfirmCompleteDialog() {
        _showConfirmCompleteDialog.value = false
    }

    fun completeTrip() {
        _showConfirmCompleteDialog.value = false
        viewModelScope.launch {
            val completed = repository.completeActiveTransfer()
            if (completed != null) {
                showToast("Transfer concluído! R$ ${String.format("%.2f", completed.fareAmount)} adicionado ao seu acerto.", "check_circle")
                _currentScreen.value = AppScreen.GANHOS
            }
        }
    }

    fun openTripDetails(trip: TripItem) {
        _selectedTripForDetails.value = trip
    }

    fun closeTripDetails() {
        _selectedTripForDetails.value = null
    }

    fun openIncidentModal() {
        _showIncidentModal.value = true
    }

    fun closeIncidentModal() {
        _showIncidentModal.value = false
    }

    fun submitIncident(reason: String) {
        _showIncidentModal.value = false
        showToast("Central de Despacho notificada: $reason", "report_problem")
    }

    fun openNotificationSheet() {
        _showNotificationSheet.value = true
    }

    fun closeNotificationSheet() {
        _showNotificationSheet.value = false
    }

    fun updateProfile(name: String, vehicleModel: String, vehiclePlate: String, phone: String, pixKey: String) {
        repository.updateProfile(name, vehicleModel, vehiclePlate, phone, pixKey)
        showToast("Perfil atualizado!", "check_circle")
    }

    fun addTrip(trip: TripItem) {
        repository.addTrip(trip)
        showToast("Escala adicionada!", "add_task")
    }

    fun showToast(message: String, icon: String = "check_circle") {
        _toastMessage.value = message
        _toastIcon.value = icon
    }

    fun dismissToast() {
        _toastMessage.value = null
    }
}
