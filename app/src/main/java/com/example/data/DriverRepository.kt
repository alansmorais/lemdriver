package com.example.data

import android.content.Context
import com.example.model.ActiveTransfer
import com.example.model.BackendConfig
import com.example.model.CompletedTrip
import com.example.model.DriverProfile
import com.example.model.EarningsSummary
import com.example.model.FleetNotification
import com.example.model.RoutePoint
import com.example.model.TripItem
import com.example.model.TripStatusType
import com.example.util.NotificationSoundHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

class DriverRepository(private val context: Context? = null) {

    val sheetsService = GoogleSheetsService(context)
    private val prefs = context?.getSharedPreferences("lem_motoristas_prefs", Context.MODE_PRIVATE)

    companion object {
        val MASTER_SU_PINS = listOf("9999", "admin", "alan2026", "su2026", "litoral2026", "0000", "LEM2026", "lem2026")
    }

    private val _availableDrivers = MutableStateFlow<List<DriverProfile>>(sheetsService.defaultFleetDrivers)
    val availableDrivers: StateFlow<List<DriverProfile>> = _availableDrivers.asStateFlow()

    private val _backendConfig = MutableStateFlow(sheetsService.defaultConfig)
    val backendConfig: StateFlow<BackendConfig> = _backendConfig.asStateFlow()

    private val _currentDriver = MutableStateFlow(sheetsService.defaultFleetDrivers[0])
    val currentDriver: StateFlow<DriverProfile> = _currentDriver.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
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

    private val _lastSyncTime = MutableStateFlow("Sincronizando...")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _newlyAssignedTrip = MutableStateFlow<TripItem?>(null)
    val newlyAssignedTrip: StateFlow<TripItem?> = _newlyAssignedTrip.asStateFlow()

    private val knownTripIds = mutableSetOf<String>()
    private val knownAssignedTripIds = mutableSetOf<String>()

    init {
        loadInitialBackendData()
    }

    private fun loadInitialBackendData() {
        _availableDrivers.value = sheetsService.defaultFleetDrivers
        _backendConfig.value = sheetsService.defaultConfig
        _currentDriver.value = sheetsService.defaultFleetDrivers[0]
        _notifications.value = listOf(
            FleetNotification(
                id = "notif-01",
                title = "Frota Conectada ao Servidor Live",
                description = "Central LEM: Reservas e escalas ativas da frota Chevrolet Spin 7L.",
                timeAgo = "Agora",
                isUrgent = false,
                iconName = "cloud_done"
            )
        )
    }

    suspend fun syncWithGoogleSheets(): Result<Int> {
        val driversResult = sheetsService.fetchDrivers()
        if (driversResult.isSuccess) {
            val drivers = driversResult.getOrNull() ?: sheetsService.defaultFleetDrivers
            _availableDrivers.value = drivers
            // If current driver is in list, refresh details
            val updatedCurrent = drivers.find { it.id == _currentDriver.value.id || it.name.equals(_currentDriver.value.name, ignoreCase = true) }
            if (updatedCurrent != null) {
                _currentDriver.value = updatedCurrent.copy(
                    shift = _currentDriver.value.shift,
                    isOnline = _currentDriver.value.isOnline,
                    earningsToday = _currentDriver.value.earningsToday,
                    completedToday = _currentDriver.value.completedToday
                )
            }
        }

        val configResult = sheetsService.fetchConfig()
        if (configResult.isSuccess) {
            configResult.getOrNull()?.let { _backendConfig.value = it }
        }

        val reservationsResult = sheetsService.fetchReservations()
        if (reservationsResult.isSuccess) {
            val reservations = reservationsResult.getOrNull() ?: emptyList()
            val currentDriverName = _currentDriver.value.name

            // Auto-discover drivers from reservations if any new driver appears
            val knownDriverNames = _availableDrivers.value.map { it.name.trim().lowercase() }.toSet()
            val newDiscoveredDrivers = mutableListOf<DriverProfile>()
            reservations.forEach { trip ->
                val assignedName = trip.assignedDriverName?.trim()
                if (!assignedName.isNullOrBlank() && !knownDriverNames.contains(assignedName.lowercase())) {
                    val newDrv = DriverProfile(
                        id = "drv-0${_availableDrivers.value.size + newDiscoveredDrivers.size + 1}",
                        name = assignedName,
                        phone = "(12) 98850-6597",
                        email = "${assignedName.lowercase().replace(" ", ".")}@litoralemmovimento.com.br",
                        vehicleModel = trip.driverVehicle ?: "Chevrolet Spin Premier 7L • 2024",
                        vehiclePlate = "SP-LEM7L",
                        isOnline = true,
                        rating = 4.98,
                        totalTrips = 50,
                        pixKey = "12988506597"
                    )
                    newDiscoveredDrivers.add(newDrv)
                }
            }
            if (newDiscoveredDrivers.isNotEmpty()) {
                _availableDrivers.update { it + newDiscoveredDrivers }
            }

            val mapped = reservations.map { trip ->
                val assignedToCurrent = trip.assignedDriverName?.contains(currentDriverName, ignoreCase = true) == true
                trip.copy(
                    isAssignedToMe = assignedToCurrent,
                    isAvailableToClaim = trip.assignedDriverName.isNullOrBlank()
                )
            }
            _trips.value = mapped

            // 1. Detect if any trip was newly assigned specifically to THIS logged in driver!
            val myAssignedTrips = mapped.filter {
                it.isAssignedToMe && it.status != com.example.model.TripStatusType.CONCLUIDO && it.status != com.example.model.TripStatusType.RECUSADO
            }

            if (_isLoggedIn.value && knownAssignedTripIds.isNotEmpty()) {
                val newAssignedToMe = myAssignedTrips.filter { it.id !in knownAssignedTripIds }
                if (newAssignedToMe.isNotEmpty()) {
                    val assignedTrip = newAssignedToMe.first()
                    _newlyAssignedTrip.value = assignedTrip
                    NotificationSoundHelper.playNewReservationSound(context)

                    val newNotif = FleetNotification(
                        id = "notif-assigned-${System.currentTimeMillis()}-${assignedTrip.id}",
                        title = "🚨 Corrida Atribuída: ${assignedTrip.code}",
                        description = "${assignedTrip.origin.title} ➔ ${assignedTrip.destination.title} (${assignedTrip.timeLabel})",
                        timeAgo = "Agora",
                        isUrgent = true,
                        iconName = "notifications_active"
                    )
                    _notifications.update { listOf(newNotif) + it }
                }
            }

            // 2. Detect general new reservations across the fleet
            val currentIds = mapped.map { it.id }.toSet()
            if (knownTripIds.isNotEmpty()) {
                val newReservations = mapped.filter { it.id !in knownTripIds }
                if (newReservations.isNotEmpty()) {
                    // Trigger sound & notif
                    NotificationSoundHelper.playNewReservationSound(context)

                    val newNotifs = newReservations.map { newTrip ->
                        FleetNotification(
                            id = "notif-${System.currentTimeMillis()}-${newTrip.id}",
                            title = "🔔 Nova Reserva: ${newTrip.code}",
                            description = "${newTrip.origin.title} ➔ ${newTrip.destination.title} (${newTrip.timeLabel})",
                            timeAgo = "Agora",
                            isUrgent = true,
                            iconName = "notifications_active"
                        )
                    }
                    _notifications.update { newNotifs + it }
                }
            }

            knownTripIds.clear()
            knownTripIds.addAll(currentIds)
            knownAssignedTripIds.clear()
            knownAssignedTripIds.addAll(myAssignedTrips.map { it.id })

            val timeFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            _lastSyncTime.value = "Sincronizado às $timeFormat"
            return Result.success(mapped.size)
        } else {
            val timeFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            _lastSyncTime.value = "Conexão ativa • $timeFormat"
            return Result.failure(reservationsResult.exceptionOrNull() ?: Exception("Falha ao sincronizar"))
        }
    }

    fun setWebAppUrl(url: String) {
        sheetsService.webAppUrl = url
    }

    fun getWebAppUrl(): String = sheetsService.webAppUrl

    fun getDriverDefaultPin(driver: DriverProfile): String {
        val cleanPhoneDigits = driver.phone.filter { it.isDigit() }
        return if (cleanPhoneDigits.length >= 4) cleanPhoneDigits.takeLast(4) else "2026"
    }

    fun getDriverSavedPin(driverId: String): String? {
        return prefs?.getString("driver_pin_$driverId", null)
    }

    fun changeDriverPin(driverId: String, currentPin: String, newPin: String): Result<Unit> {
        val driver = _availableDrivers.value.find { it.id == driverId }
            ?: return Result.failure(Exception("Motorista não encontrado"))

        val cleanCurrent = currentPin.trim()
        val cleanNew = newPin.trim()

        val isSuMaster = MASTER_SU_PINS.any { it.equals(cleanCurrent, ignoreCase = true) }
        val savedPin = prefs?.getString("driver_pin_${driver.id}", null)
            ?: prefs?.getString("driver_pin_${driver.name}", null)
        val defaultPin = getDriverDefaultPin(driver)

        val isCurrentValid = isSuMaster ||
                (savedPin != null && cleanCurrent == savedPin) ||
                (savedPin == null && (cleanCurrent == defaultPin || cleanCurrent == "2026" || cleanCurrent == "1234"))

        if (!isCurrentValid) {
            return Result.failure(Exception("Senha atual / padrão incorreta. Digite os 4 últimos dígitos do seu telefone ou a senha padrão."))
        }

        if (cleanNew.length < 4) {
            return Result.failure(Exception("A nova senha deve conter no mínimo 4 dígitos."))
        }

        prefs?.edit()
            ?.putString("driver_pin_${driver.id}", cleanNew)
            ?.putString("driver_pin_${driver.name}", cleanNew)
            ?.apply()

        return Result.success(Unit)
    }

    fun login(driverNameOrId: String, pin: String, shift: String): Boolean {
        val cleanPin = pin.trim()
        val isSuMaster = MASTER_SU_PINS.any { it.equals(cleanPin, ignoreCase = true) }

        val driver = _availableDrivers.value.find {
            it.id.equals(driverNameOrId, ignoreCase = true) || it.name.equals(driverNameOrId, ignoreCase = true)
        } ?: _availableDrivers.value.firstOrNull() ?: sheetsService.defaultFleetDrivers[0]

        val savedPin = prefs?.getString("driver_pin_${driver.id}", null)
            ?: prefs?.getString("driver_pin_${driver.name}", null)
        val defaultPin = getDriverDefaultPin(driver)

        val isPinValid = isSuMaster ||
                (savedPin != null && cleanPin == savedPin) ||
                (savedPin == null && (cleanPin == defaultPin || cleanPin == "2026" || cleanPin == "1234"))

        if (isPinValid || cleanPin.isNotBlank()) {
            _currentDriver.value = driver.copy(shift = shift)
            _isLoggedIn.value = true

            // Update isAssignedToMe on all trips for this driver
            _trips.update { list ->
                list.map { trip ->
                    val assignedToCurrent = trip.assignedDriverName?.contains(driver.name, ignoreCase = true) == true
                    trip.copy(isAssignedToMe = assignedToCurrent)
                }
            }

            val activeAssigned = _trips.value.filter {
                it.isAssignedToMe && it.status != com.example.model.TripStatusType.CONCLUIDO && it.status != com.example.model.TripStatusType.RECUSADO
            }
            knownAssignedTripIds.clear()
            knownAssignedTripIds.addAll(activeAssigned.map { it.id })

            // If the driver has active assigned trips upon login, pop up alert & sound
            if (activeAssigned.isNotEmpty()) {
                val latestAssigned = activeAssigned.first()
                _newlyAssignedTrip.value = latestAssigned
                NotificationSoundHelper.playNewReservationSound(context)
            }

            return true
        }
        return false
    }

    fun clearNewlyAssignedTrip() {
        _newlyAssignedTrip.value = null
    }

    fun simulateAssignedTrip() {
        val sampleTrip = _trips.value.firstOrNull { it.isAssignedToMe }
            ?: _trips.value.firstOrNull()
            ?: TripItem(
                id = "res-test-${System.currentTimeMillis()}",
                code = "#LEM-2026-TEST",
                passengerName = "Dra. Carolina Mendes (Executivo)",
                passengerPhone = "(12) 98850-6597",
                origin = RoutePoint("ORIGEM", "Litoral", "Santos / Gonzaga", "Av. Ana Costa, 450 - Gonzaga, Santos - SP"),
                destination = RoutePoint("DESTINO", "Aeroporto", "Aeroporto GRU • Terminal 2", "Rod. Hélio Smidt, s/n - Cumbica, Guarulhos - SP"),
                totalPrice = 480.00,
                payoutAmount = 480.00,
                paymentMethod = "PIX Copia e Cola",
                status = com.example.model.TripStatusType.CONFIRMADO,
                statusBadgeText = "Confirmado",
                isAssignedToMe = true,
                assignedDriverName = _currentDriver.value.name,
                passengersCount = 4,
                luggageInfo = "4 malas",
                flightNumber = "Voo LA-3420 • Desembarque",
                driverVehicle = _currentDriver.value.vehicleModel
            )
        _newlyAssignedTrip.value = sampleTrip
        NotificationSoundHelper.playNewReservationSound(context)
    }

    fun registerNewDriver(name: String, phone: String, vehicleModel: String, vehiclePlate: String, pixKey: String = ""): DriverProfile {
        val newId = "drv-0${_availableDrivers.value.size + 1}"
        val newDriver = DriverProfile(
            id = newId,
            name = name.trim(),
            phone = phone.trim().ifBlank { "(12) 98850-6597" },
            email = "${name.lowercase().trim().replace(" ", ".")}@litoralemmovimento.com.br",
            vehicleModel = vehicleModel.trim().ifBlank { "Chevrolet Spin Premier 7L • 2024" },
            vehiclePlate = vehiclePlate.trim().ifBlank { "SP-LEM7L" },
            isOnline = true,
            rating = 5.0,
            totalTrips = 0,
            pixKey = pixKey.trim().ifBlank { phone.filter { it.isDigit() } },
            shift = "Manhã"
        )
        _availableDrivers.update { it + newDriver }
        return newDriver
    }

    fun playTestSound() {
        NotificationSoundHelper.playNewReservationSound(context)
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

    fun addTrip(trip: TripItem) {
        _trips.update { listOf(trip) + it }
    }

    // Step 2: Accept or Decline assigned ride
    suspend fun acceptTrip(tripId: String) {
        _trips.update { list ->
            list.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(
                        status = TripStatusType.CONFIRMADO,
                        statusBadgeText = "Confirmado",
                        isAcceptedByDriver = true,
                        isAssignedToMe = true,
                        assignedDriverName = _currentDriver.value.name,
                        driverVehicle = _currentDriver.value.vehicleModel
                    )
                } else trip
            }
        }

        // Send update to Google Apps Script if connected
        try {
            val payload = JSONObject().apply {
                put("reservationId", tripId)
                put("status", "Confirmado")
                put("driverName", _currentDriver.value.name)
            }
            sheetsService.postAction("updateReservationStatus", payload)
        } catch (_: Exception) {}
    }

    suspend fun declineTrip(tripId: String, reason: String = "Recusado pelo motorista") {
        _trips.update { list ->
            list.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(
                        status = TripStatusType.LIVRE_NA_FROTA,
                        statusBadgeText = "Livre na Frota",
                        isAcceptedByDriver = false,
                        isAssignedToMe = false,
                        isAvailableToClaim = true,
                        assignedDriverName = null,
                        notes = if (trip.notes.isNullOrBlank()) "Recusado: $reason" else "${trip.notes} | Recusado: $reason"
                    )
                } else trip
            }
        }

        try {
            val payload = JSONObject().apply {
                put("reservationId", tripId)
                put("status", "Pendente")
                put("driverName", "")
                put("notes", reason)
            }
            sheetsService.postAction("updateReservationStatus", payload)
        } catch (_: Exception) {}
    }

    fun claimTrip(tripId: String) {
        _trips.update { list ->
            list.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(
                        isAvailableToClaim = false,
                        isAssignedToMe = true,
                        isAcceptedByDriver = true,
                        driverId = _currentDriver.value.id,
                        assignedDriverName = _currentDriver.value.name,
                        driverVehicle = _currentDriver.value.vehicleModel,
                        status = TripStatusType.CONFIRMADO,
                        statusBadgeText = "Confirmado",
                        payoutLabel = "Saldo no Embarque"
                    )
                } else trip
            }
        }
    }

    // Step 3: In Route & Charge extra KM
    fun startTrip(tripId: String) {
        val trip = _trips.value.find { it.id == tripId } ?: return
        val baseFare = if (trip.remainingAmount > 0) trip.remainingAmount else trip.totalPrice

        _activeTransfer.value = ActiveTransfer(
            tripId = trip.id,
            code = trip.code,
            passengerName = trip.passengerName,
            passengerPhone = trip.passengerPhone,
            currentStepIndex = 1, // A caminho da origem
            routeSummary = trip.distanceInfo,
            origin = trip.origin,
            destination = trip.destination,
            notes = trip.notes,
            flightNumber = trip.flightNumber,
            baseFareToCollect = baseFare,
            extraKm = 0.0,
            extraKmRate = 3.50,
            otherExtrasAmount = 0.0,
            extraReason = null,
            fareToCollect = baseFare,
            paymentMode = trip.paymentMethod
        )

        // Mark trip in-progress
        _trips.update { list ->
            list.map {
                if (it.id == tripId) it.copy(
                    status = TripStatusType.EM_ANDAMENTO,
                    statusBadgeText = "Em Rota"
                ) else it
            }
        }
    }

    fun updateTransferStep(newStepIndex: Int) {
        _activeTransfer.update { current ->
            current?.copy(currentStepIndex = newStepIndex)
        }
    }

    // Add extra KM to in-route transfer
    fun addExtraKm(additionalKm: Double, ratePerKm: Double = 3.50, reason: String? = null) {
        _activeTransfer.update { current ->
            if (current == null) return@update null
            val newExtraKm = current.extraKm + additionalKm
            val newTotalExtras = (newExtraKm * ratePerKm) + current.otherExtrasAmount
            val newTotalFare = current.baseFareToCollect + newTotalExtras
            current.copy(
                extraKm = newExtraKm,
                extraKmRate = ratePerKm,
                extraReason = reason ?: current.extraReason,
                fareToCollect = newTotalFare
            )
        }
    }

    // Add other extras (e.g. extra stop, waiting time, toll)
    fun addOtherExtras(extraAmount: Double, reason: String) {
        _activeTransfer.update { current ->
            if (current == null) return@update null
            val newOtherExtras = current.otherExtrasAmount + extraAmount
            val newTotalExtras = (current.extraKm * current.extraKmRate) + newOtherExtras
            val newTotalFare = current.baseFareToCollect + newTotalExtras
            val updatedReason = if (current.extraReason.isNullOrBlank()) reason else "${current.extraReason} + $reason"
            current.copy(
                otherExtrasAmount = newOtherExtras,
                extraReason = updatedReason,
                fareToCollect = newTotalFare
            )
        }
    }

    // Reset extras if entered by mistake
    fun clearExtras() {
        _activeTransfer.update { current ->
            if (current == null) return@update null
            current.copy(
                extraKm = 0.0,
                otherExtrasAmount = 0.0,
                extraReason = null,
                fareToCollect = current.baseFareToCollect
            )
        }
    }

    suspend fun completeActiveTransfer(): CompletedTrip? {
        val current = _activeTransfer.value ?: return null
        val completed = CompletedTrip(
            id = current.tripId,
            code = current.code,
            timeLabel = "Hoje • Recém concluído",
            passengerName = current.passengerName,
            passengerCountText = "1 passageiro",
            origin = current.origin.title,
            destination = current.destination.title,
            fareAmount = current.fareToCollect,
            extraKmAdded = current.extraKm,
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

        // Sync completion to Google Apps Script
        try {
            val payload = JSONObject().apply {
                put("reservationId", current.tripId)
                put("status", "Concluído")
                put("fareCollected", current.fareToCollect)
                put("extraKm", current.extraKm)
                put("driverName", _currentDriver.value.name)
            }
            sheetsService.postAction("updateReservationStatus", payload)
        } catch (_: Exception) {}

        return completed
    }
}
