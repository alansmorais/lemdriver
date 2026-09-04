package com.example.data

import com.example.model.ActiveTransfer
import com.example.model.CompletedTrip
import com.example.model.DriverProfile
import com.example.model.EarningsSummary
import com.example.model.FleetNotification
import com.example.model.RoutePoint
import com.example.model.TripItem
import com.example.model.TripStatusType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DriverRepository {

    // Drivers registered in the fleet system
    val availableDrivers = listOf(
        DriverProfile(
            id = "motorista_1",
            name = "Motorista da Frota",
            vehicleModel = "Veículo Executivo",
            vehiclePlate = "FROTA-SP",
            isOnline = true,
            shift = "Manhã",
            earningsToday = 0.00,
            completedToday = 0,
            phone = "",
            pixKey = ""
        )
    )

    private val _currentDriver = MutableStateFlow(availableDrivers[0])
    val currentDriver: StateFlow<DriverProfile> = _currentDriver.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _trips = MutableStateFlow<List<TripItem>>(emptyList())
    val trips: StateFlow<List<TripItem>> = _trips.asStateFlow()

    private val _activeTransfer = MutableStateFlow<ActiveTransfer?>(null)
    val activeTransfer: StateFlow<ActiveTransfer?> = _activeTransfer.asStateFlow()

    private val _earnings = MutableStateFlow(
        EarningsSummary(
            weeklyTotal = 0.00,
            tripsCompletedWeekly = 0,
            receivedOnSite = 0.00,
            fleetSettlement = 0.00,
            todayTotal = 0.00,
            todayCompletedCount = 0
        )
    )
    val earnings: StateFlow<EarningsSummary> = _earnings.asStateFlow()

    private val _completedTrips = MutableStateFlow<List<CompletedTrip>>(emptyList())
    val completedTrips: StateFlow<List<CompletedTrip>> = _completedTrips.asStateFlow()

    private val _notifications = MutableStateFlow<List<FleetNotification>>(emptyList())
    val notifications: StateFlow<List<FleetNotification>> = _notifications.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("Sincronizado")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    fun login(driverId: String, pin: String, shift: String): Boolean {
        val driver = availableDrivers.find { it.id == driverId } ?: availableDrivers[0]
        _currentDriver.value = driver.copy(shift = shift)
        _isLoggedIn.value = true
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun updateProfile(name: String, vehicleModel: String, vehiclePlate: String, phone: String, pixKey: String) {
        _currentDriver.update {
            it.copy(
                name = name,
                vehicleModel = vehicleModel,
                vehiclePlate = vehiclePlate,
                phone = phone,
                pixKey = pixKey
            )
        }
    }

    fun setShift(shift: String) {
        _currentDriver.update { it.copy(shift = shift) }
    }

    fun toggleOnlineStatus() {
        _currentDriver.update { it.copy(isOnline = !it.isOnline) }
    }

    fun syncWithSheets() {
        _lastSyncTime.value = "Agora mesmo"
    }

    fun addTrip(trip: TripItem) {
        _trips.update { listOf(trip) + it }
    }

    fun claimTrip(tripId: String) {
        _trips.update { list ->
            list.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(
                        isAvailableToClaim = false,
                        driverId = _currentDriver.value.id,
                        status = TripStatusType.CONFIRMADO,
                        statusBadgeText = "CONFIRMADO",
                        payoutLabel = "A Receber"
                    )
                } else trip
            }
        }
    }

    fun startTrip(tripId: String) {
        val trip = _trips.value.find { it.id == tripId } ?: return
        _activeTransfer.value = ActiveTransfer(
            tripId = trip.id,
            passengerName = trip.passengerName,
            passengerPhone = trip.passengerPhone,
            currentStepIndex = 1, // A caminho da origem
            routeSummary = trip.distanceInfo,
            origin = trip.origin,
            destination = trip.destination,
            notes = trip.notes,
            fareToCollect = trip.payoutAmount,
            paymentMode = trip.paymentMethod
        )
    }

    fun updateTransferStep(newStepIndex: Int) {
        _activeTransfer.update { current ->
            current?.copy(currentStepIndex = newStepIndex)
        }
    }

    fun completeActiveTransfer(): CompletedTrip? {
        val current = _activeTransfer.value ?: return null
        val completed = CompletedTrip(
            id = current.tripId,
            timeLabel = "Hoje • Recém concluído",
            passengerName = current.passengerName,
            passengerCountText = "1 passageiro",
            origin = current.origin.title,
            destination = current.destination.title,
            fareAmount = current.fareToCollect,
            paymentMethod = current.paymentMode,
            statusText = "Concluído"
        )
        _completedTrips.update { listOf(completed) + it }
        _earnings.update { e ->
            val isSite = current.paymentMode.contains("PIX", ignoreCase = true) || current.paymentMode.contains("Dinheiro", ignoreCase = true)
            e.copy(
                weeklyTotal = e.weeklyTotal + current.fareToCollect,
                tripsCompletedWeekly = e.tripsCompletedWeekly + 1,
                receivedOnSite = if (isSite) e.receivedOnSite + current.fareToCollect else e.receivedOnSite,
                fleetSettlement = if (!isSite) e.fleetSettlement + current.fareToCollect else e.fleetSettlement,
                todayTotal = e.todayTotal + current.fareToCollect,
                todayCompletedCount = e.todayCompletedCount + 1
            )
        }
        _currentDriver.update { d ->
            d.copy(
                earningsToday = d.earningsToday + current.fareToCollect,
                completedToday = d.completedToday + 1
            )
        }
        // Remove from pending trips list
        _trips.update { list -> list.filterNot { it.id == current.tripId } }
        _activeTransfer.value = null
        return completed
    }
}
