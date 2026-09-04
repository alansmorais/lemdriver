package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DriverRepository
import com.example.model.ActiveTransfer
import com.example.model.AppScreen
import com.example.model.CompletedTrip
import com.example.model.DriverProfile
import com.example.model.EarningsSummary
import com.example.model.FleetNotification
import com.example.model.TripCategory
import com.example.model.TripItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DriverViewModel(
    private val repository: DriverRepository = DriverRepository()
) : ViewModel() {

    val availableDrivers = repository.availableDrivers

    val currentDriver: StateFlow<DriverProfile> = repository.currentDriver
    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
    val trips: StateFlow<List<TripItem>> = repository.trips
    val activeTransfer: StateFlow<ActiveTransfer?> = repository.activeTransfer
    val earnings: StateFlow<EarningsSummary> = repository.earnings
    val completedTrips: StateFlow<List<CompletedTrip>> = repository.completedTrips
    val notifications: StateFlow<List<FleetNotification>> = repository.notifications
    val lastSyncTime: StateFlow<String> = repository.lastSyncTime

    private val _currentScreen = MutableStateFlow(AppScreen.VIAGENS)
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

    private val _showIncidentModal = MutableStateFlow(false)
    val showIncidentModal: StateFlow<Boolean> = _showIncidentModal.asStateFlow()

    private val _showNotificationSheet = MutableStateFlow(false)
    val showNotificationSheet: StateFlow<Boolean> = _showNotificationSheet.asStateFlow()

    private val _showConfirmCompleteDialog = MutableStateFlow(false)
    val showConfirmCompleteDialog: StateFlow<Boolean> = _showConfirmCompleteDialog.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    val filteredTrips: StateFlow<List<TripItem>> = combine(trips, _selectedTripCategory) { allTrips, category ->
        when (category) {
            TripCategory.MINHAS -> allTrips.filter { !it.isAvailableToClaim }
            TripCategory.DISPONIVEIS -> allTrips.filter { it.isAvailableToClaim }
            TripCategory.TODAS -> allTrips
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val minhasCount: StateFlow<Int> = trips.combine(MutableStateFlow(Unit)) { allTrips, _ ->
        allTrips.count { !it.isAvailableToClaim }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val disponiveisCount: StateFlow<Int> = trips.combine(MutableStateFlow(Unit)) { allTrips, _ ->
        allTrips.count { it.isAvailableToClaim }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todasCount: StateFlow<Int> = trips.combine(MutableStateFlow(Unit)) { allTrips, _ ->
        allTrips.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addTrip(trip: TripItem) {
        repository.addTrip(trip)
        showToast("Escala ${trip.id} cadastrada com sucesso!", "add_task")
    }

    fun updateProfile(name: String, vehicleModel: String, vehiclePlate: String, phone: String, pixKey: String) {
        repository.updateProfile(name, vehicleModel, vehiclePlate, phone, pixKey)
        showToast("Perfil atualizado!", "check_circle")
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

    fun login(driverId: String, pin: String, shift: String) {
        repository.login(driverId, pin, shift)
        _currentScreen.value = AppScreen.VIAGENS
        showToast("Bem-vindo ao Portal do Motorista!", "verified")
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = AppScreen.LOGIN
        showToast("Sessão encerrada com segurança.", "logout")
    }

    fun toggleOnline() {
        repository.toggleOnlineStatus()
        val status = if (currentDriver.value.isOnline) "Você agora está Online na frota" else "Status alterado para Offline"
        showToast(status, "directions_car")
    }

    fun syncFeed() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1000)
            repository.syncWithSheets()
            _isSyncing.value = false
            showToast("Planilhas e escalas atualizadas com sucesso.", "sync")
        }
    }

    fun claimTrip(tripId: String) {
        repository.claimTrip(tripId)
        showToast("Corrida $tripId transferida para sua agenda!", "verified")
    }

    fun startTrip(tripId: String) {
        viewModelScope.launch {
            showToast("Viagem $tripId iniciada! Navegador pronto.", "navigation")
            delay(300)
            repository.startTrip(tripId)
            _currentScreen.value = AppScreen.EM_ROTA
        }
    }

    fun advanceStep(stepIndex: Int) {
        repository.updateTransferStep(stepIndex)
        val stepNames = listOf("Despacho Confirmado", "A caminho da Origem", "Passageiro Embarcado", "Chegada ao Destino")
        val label = stepNames.getOrElse(stepIndex) { "Etapa Atualizada" }
        showToast(label, "route")
    }

    fun openConfirmCompleteDialog() {
        _showConfirmCompleteDialog.value = true
    }

    fun closeConfirmCompleteDialog() {
        _showConfirmCompleteDialog.value = false
    }

    fun completeTrip() {
        _showConfirmCompleteDialog.value = false
        val completed = repository.completeActiveTransfer()
        if (completed != null) {
            showToast("Viagem ${completed.id} concluída! R$ ${String.format("%.2f", completed.fareAmount)} adicionado aos ganhos.", "check_circle")
            _currentScreen.value = AppScreen.GANHOS
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
        showToast("Central notificada: $reason", "report_problem")
    }

    fun openNotificationSheet() {
        _showNotificationSheet.value = true
    }

    fun closeNotificationSheet() {
        _showNotificationSheet.value = false
    }

    fun showToast(message: String, icon: String = "check_circle") {
        _toastMessage.value = message
        _toastIcon.value = icon
        viewModelScope.launch {
            delay(3500)
            if (_toastMessage.value == message) {
                _toastMessage.value = null
            }
        }
    }

    fun dismissToast() {
        _toastMessage.value = null
    }
}
