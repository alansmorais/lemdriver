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

    // Preset available drivers for login switch
    val availableDrivers = listOf(
        DriverProfile(
            id = "carlos",
            name = "Carlos Eduardo M.",
            vehicleModel = "Toyota Corolla 2023",
            vehiclePlate = "BRA2E19",
            rating = 4.98,
            completedTripsCount = 240,
            isOnline = true,
            shift = "Manhã",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBENBpQaUh0iwX5wy4GWZr7zyg1n7z7bpubr4bhL9d4xRfpwk_7MNpkXSyQN4JgD6BnLKXu3SrIrM0D6XYkrfuh0VPORauaeOZKbom8BZiWxYtAI9s-SWa0SD0BEjKw3UqKgKJoAvp8-D1tekeJpHpu4cJj-TtB-IFBemRguynK597ObJUBBHEEjBz5kNuLEnisRElw10GA9i5aLUB8qEHywWeUKkNTtwj-RV5IGMBAINzOBo7zma_O",
            earningsToday = 800.00,
            completedToday = 2
        ),
        DriverProfile(
            id = "roberto",
            name = "Roberto Silva",
            vehicleModel = "Chevrolet Spin 7 Lugares",
            vehiclePlate = "SPN7X88",
            rating = 4.95,
            completedTripsCount = 312,
            isOnline = true,
            shift = "Tarde",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB6XAb1AofMi2dShcp_XKFKFLQWQJRoiq6LlMn6CjMAexV2oDpmK3IX3RzaqWftlpkngmy5yWFUUm_vdmljh-o5knNdmr1cdWhRSlnOpu9SVD__WK31v8PBKAJJf8O1NatSr7jjFxFF_qCHJ6H-81Yz1MS5zLdsCHUcuTrIsx2HjvaeSZHugsplKQAjGmqOPdeiGRzeMEW6AJ8xDBkz2X598TXJgm5CHWKbKTyyrY6g_HZGgFOoFjS-",
            earningsToday = 620.00,
            completedToday = 1
        ),
        DriverProfile(
            id = "marcos",
            name = "Marcos Vinicius",
            vehicleModel = "Toyota SW4 Executive",
            vehiclePlate = "SW4E990",
            rating = 4.99,
            completedTripsCount = 415,
            isOnline = true,
            shift = "Manhã",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBENBpQaUh0iwX5wy4GWZr7zyg1n7z7bpubr4bhL9d4xRfpwk_7MNpkXSyQN4JgD6BnLKXu3SrIrM0D6XYkrfuh0VPORauaeOZKbom8BZiWxYtAI9s-SWa0SD0BEjKw3UqKgKJoAvp8-D1tekeJpHpu4cJj-TtB-IFBemRguynK597ObJUBBHEEjBz5kNuLEnisRElw10GA9i5aLUB8qEHywWeUKkNTtwj-RV5IGMBAINzOBo7zma_O",
            earningsToday = 950.00,
            completedToday = 2
        ),
        DriverProfile(
            id = "andre",
            name = "André Fagundes",
            vehicleModel = "BYD Song Plus Híbrido",
            vehiclePlate = "BYD9S22",
            rating = 4.97,
            completedTripsCount = 188,
            isOnline = true,
            shift = "Noite",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB6XAb1AofMi2dShcp_XKFKFLQWQJRoiq6LlMn6CjMAexV2oDpmK3IX3RzaqWftlpkngmy5yWFUUm_vdmljh-o5knNdmr1cdWhRSlnOpu9SVD__WK31v8PBKAJJf8O1NatSr7jjFxFF_qCHJ6H-81Yz1MS5zLdsCHUcuTrIsx2HjvaeSZHugsplKQAjGmqOPdeiGRzeMEW6AJ8xDBkz2X598TXJgm5CHWKbKTyyrY6g_HZGgFOoFjS-",
            earningsToday = 450.00,
            completedToday = 1
        )
    )

    private val _currentDriver = MutableStateFlow(availableDrivers[0])
    val currentDriver: StateFlow<DriverProfile> = _currentDriver.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _trips = MutableStateFlow<List<TripItem>>(getInitialTrips())
    val trips: StateFlow<List<TripItem>> = _trips.asStateFlow()

    private val _activeTransfer = MutableStateFlow<ActiveTransfer?>(getInitialActiveTransfer())
    val activeTransfer: StateFlow<ActiveTransfer?> = _activeTransfer.asStateFlow()

    private val _earnings = MutableStateFlow(
        EarningsSummary(
            weeklyTotal = 2840.00,
            tripsCompletedWeekly = 14,
            goalPercentage = 92,
            receivedOnSite = 1950.00,
            fleetSettlement = 890.00,
            todayTotal = 800.00,
            todayCompletedCount = 2
        )
    )
    val earnings: StateFlow<EarningsSummary> = _earnings.asStateFlow()

    private val _completedTrips = MutableStateFlow<List<CompletedTrip>>(getInitialCompletedTrips())
    val completedTrips: StateFlow<List<CompletedTrip>> = _completedTrips.asStateFlow()

    private val _notifications = MutableStateFlow<List<FleetNotification>>(getInitialNotifications())
    val notifications: StateFlow<List<FleetNotification>> = _notifications.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("Há 2 min")
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

    fun setShift(shift: String) {
        _currentDriver.update { it.copy(shift = shift) }
    }

    fun toggleOnlineStatus() {
        _currentDriver.update { it.copy(isOnline = !it.isOnline) }
    }

    fun syncWithSheets() {
        _lastSyncTime.value = "Agora mesmo"
    }

    fun claimTrip(tripId: String) {
        _trips.update { list ->
            list.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(
                        isAvailableToClaim = false,
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
            passengerSubtitle = trip.passengerSubtitle,
            passengerAvatarUrl = trip.passengerAvatarUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuC0-x2zOtOX9JXq8a5BXv7m9qp2PHnn0VPlx4WlscW0tM2Nqc-Ese9qrZfVcknN5YCMMGYI1hOeEgi2_RVdTkrvBEhtiKuqWyPnfugxP6aC1_ZZKLzWqEKE4dn97YlLu8Tg_x8wtLgHefBSV_1rtcxD4NJFI01rqLeXnsNqPnsY9JRdDcapZJanBBPz8BAWcENDvE9oALioGdhPHpjk4jBdyobrW3s_0lSeFAGLFUYCuql5vv3naIYn",
            passengerPhone = trip.passengerPhone,
            remainingMinutes = 28,
            remainingDistanceKm = 19.4,
            estimatedArrival = "17:48",
            currentStepIndex = 2, // Embarcado
            routeSummary = "Via Rodovia dos Imigrantes",
            trafficCondition = "Pista Fluida",
            origin = trip.origin,
            destination = trip.destination,
            centralAlert = "Voo com conexão em Congonhas. Passageiro VIP pontual.",
            fareToCollect = trip.payoutAmount,
            paymentMode = trip.paymentMethod,
            paymentNote = "Recibo digital pronto",
            mapImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCFfJvr1oVNWHN8AfTA-KMzcG_FVhlkcJQc-efVZl-WwsCPtHeLeMiF5gi3d9Xh1eqUtn85MrisnMPoxzw5OPmFYSc3sNj40iAe5YcCgHObD7LlXOwC1A1xKNzQEpMmLZJjAUCk4TkyQY4FVG9j1LaP57JnJxCLl-WaF3Uy7t9Wf0cLuFyK1mmVU4qrIXBfdyVcj8UfnyZEF3WfG9hlw1vNgA9B5HQGHeNvJtoFeQ-66-Y8ezr6Glie"
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
            timeLabel = "Hoje • Agora",
            passengerName = current.passengerName,
            passengerCountText = "1 passageiro",
            origin = current.origin.title,
            destination = current.destination.title,
            fareAmount = current.fareToCollect,
            statusText = "Recebido"
        )
        _completedTrips.update { listOf(completed) + it }
        _earnings.update { e ->
            e.copy(
                weeklyTotal = e.weeklyTotal + current.fareToCollect,
                tripsCompletedWeekly = e.tripsCompletedWeekly + 1,
                receivedOnSite = e.receivedOnSite + current.fareToCollect,
                todayTotal = e.todayTotal + current.fareToCollect,
                todayCompletedCount = e.todayCompletedCount + 1
            )
        }
        _currentDriver.update { d ->
            d.copy(
                earningsToday = d.earningsToday + current.fareToCollect,
                completedToday = d.completedToday + 1,
                completedTripsCount = d.completedTripsCount + 1
            )
        }
        // Remove from pending trips
        _trips.update { list -> list.filterNot { it.id == current.tripId } }
        _activeTransfer.value = null
        return completed
    }

    private fun getInitialTrips(): List<TripItem> = listOf(
        TripItem(
            id = "LM-2048",
            transferType = "Transfer Executivo",
            timeLabel = "Hoje • 14:30",
            status = TripStatusType.CONFIRMADO,
            statusBadgeText = "CONFIRMADO",
            isAvailableToClaim = false,
            origin = RoutePoint(
                badge = "A",
                categoryTag = "Origem • Aeroporto",
                title = "Aeroporto de Guarulhos - Terminal 3",
                subtitle = "Desembarque Internacional, Portão G"
            ),
            destination = RoutePoint(
                badge = "B",
                categoryTag = "Destino • Litoral Norte",
                title = "Riviera de São Lourenço - Módulo 6",
                subtitle = "Al. dos Manacás, Residencial Costa do Sol"
            ),
            distanceInfo = "124 km • Rodovia dos Imigrantes / Rio-Santos",
            passengersCount = 3,
            luggageInfo = "4 malas G",
            specialPerk = "Cadeira infantil inclusa",
            passengerName = "Dr. Fernando Albuquerque",
            passengerSubtitle = "Cliente Executivo VIP",
            passengerInitials = "FA",
            passengerPhone = "+5511999999999",
            payoutAmount = 480.00,
            payoutLabel = "A Receber"
        ),
        TripItem(
            id = "LM-2051",
            transferType = "Transfer Aeroporto",
            timeLabel = "Hoje • 17:00",
            status = TripStatusType.LIVRE_NA_FROTA,
            statusBadgeText = "LIVRE NA FROTA",
            isAvailableToClaim = true,
            origin = RoutePoint(
                badge = "A",
                categoryTag = "Origem • Baixada Santista",
                title = "Santos (Ponta da Praia)",
                subtitle = "Av. Almirante Saldanha da Gama, 110"
            ),
            destination = RoutePoint(
                badge = "B",
                categoryTag = "Destino • São Paulo Capital",
                title = "Aeroporto de Congonhas (CGH)",
                subtitle = "Piso Superior Embarque, Portão 2"
            ),
            distanceInfo = "76 km • Subida Anchieta",
            passengersCount = 2,
            luggageInfo = "2 malas M",
            specialPerk = "Pedágios Inclusos",
            passengerName = "Marina Rezende",
            passengerSubtitle = "Passageira Executiva",
            passengerInitials = "MR",
            passengerPhone = "+5513988776655",
            payoutAmount = 320.00,
            payoutLabel = "Valor Líquido"
        ),
        TripItem(
            id = "LM-2049",
            transferType = "Transfer Noturno",
            timeLabel = "Hoje • 21:15",
            status = TripStatusType.AGENDADO,
            statusBadgeText = "AGENDADO",
            isAvailableToClaim = false,
            origin = RoutePoint(
                badge = "A",
                categoryTag = "Origem",
                title = "Bertioga (Centro Comercial)",
                subtitle = "Av. 19 de Maio, 450"
            ),
            destination = RoutePoint(
                badge = "B",
                categoryTag = "Destino",
                title = "Praia de Juquehy (São Sebastião)",
                subtitle = "Av. Mãe Bernarda, 1800"
            ),
            distanceInfo = "42 km • Translado Local",
            passengersCount = 2,
            luggageInfo = "2 malas P",
            specialPerk = null,
            passengerName = "Gustavo Prado",
            passengerSubtitle = "Reserva Hotelaria Juquehy",
            passengerInitials = "GP",
            passengerPhone = "+5511988887777",
            payoutAmount = 260.00,
            payoutLabel = "A Receber"
        ),
        TripItem(
            id = "LM-2053",
            transferType = "Transfer Executivo",
            timeLabel = "Hoje • 23:00",
            status = TripStatusType.LIVRE_NA_FROTA,
            statusBadgeText = "LIVRE NA FROTA",
            isAvailableToClaim = true,
            origin = RoutePoint(
                badge = "A",
                categoryTag = "Origem • São Paulo Capital",
                title = "Aeroporto de Guarulhos (GRU)",
                subtitle = "Terminal 2 - Desembarque Oeste"
            ),
            destination = RoutePoint(
                badge = "B",
                categoryTag = "Destino • Baixada Santista",
                title = "Guarujá (Pitangueiras)",
                subtitle = "Av. Marechal Deodoro da Fonseca, 890"
            ),
            distanceInfo = "105 km • Rodovia dos Imigrantes",
            passengersCount = 4,
            luggageInfo = "4 malas M",
            specialPerk = "Água e Wi-Fi Solicitados",
            passengerName = "Carlos Barreto",
            passengerSubtitle = "Grupo Corporativo",
            passengerInitials = "CB",
            passengerPhone = "+5511977776666",
            payoutAmount = 420.00,
            payoutLabel = "Valor Líquido"
        )
    )

    private fun getInitialActiveTransfer(): ActiveTransfer = ActiveTransfer(
        tripId = "LM-2046",
        passengerName = "Camila Siqueira",
        passengerSubtitle = "Transfer Executivo Premium",
        passengerAvatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC0-x2zOtOX9JXq8a5BXv7m9qp2PHnn0VPlx4WlscW0tM2Nqc-Ese9qrZfVcknN5YCMMGYI1hOeEgi2_RVdTkrvBEhtiKuqWyPnfugxP6aC1_ZZKLzWqEKE4dn97YlLu8Tg_x8wtLgHefBSV_1rtcxD4NJFI01rqLeXnsNqPnsY9JRdDcapZJanBBPz8BAWcENDvE9oALioGdhPHpjk4jBdyobrW3s_0lSeFAGLFUYCuql5vv3naIYn",
        passengerPhone = "+5513999999999",
        remainingMinutes = 28,
        remainingDistanceKm = 19.4,
        estimatedArrival = "17:48",
        currentStepIndex = 2, // Embarcado (3 of 4)
        routeSummary = "Via Rodovia dos Imigrantes",
        trafficCondition = "Pista Fluida",
        origin = RoutePoint(
            badge = "A",
            categoryTag = "Origem (Embarque efetuado)",
            title = "Av. Marechal Mallet, 1020",
            subtitle = "Canto do Forte, Praia Grande - SP"
        ),
        destination = RoutePoint(
            badge = "B",
            categoryTag = "Destino Final",
            title = "Hotel Unique",
            subtitle = "Av. Brigadeiro Luís Antônio, 4700 - SP"
        ),
        centralAlert = "Voo às 19:30 em Congonhas. Passageira pontual com 1 cadeira infantil já instalada.",
        fareToCollect = 390.00,
        paymentMode = "PIX ou Dinheiro",
        paymentNote = "Recibo digital pronto",
        mapImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCFfJvr1oVNWHN8AfTA-KMzcG_FVhlkcJQc-efVZl-WwsCPtHeLeMiF5gi3d9Xh1eqUtn85MrisnMPoxzw5OPmFYSc3sNj40iAe5YcCgHObD7LlXOwC1A1xKNzQEpMmLZJjAUCk4TkyQY4FVG9j1LaP57JnJxCLl-WaF3Uy7t9Wf0cLuFyK1mmVU4qrIXBfdyVcj8UfnyZEF3WfG9hlw1vNgA9B5HQGHeNvJtoFeQ-66-Y8ezr6Glie"
    )

    private fun getInitialCompletedTrips(): List<CompletedTrip> = listOf(
        CompletedTrip(
            id = "LM-2045",
            timeLabel = "Hoje • 14:30",
            passengerName = "Dra. Renata Vasconcellos",
            passengerCountText = "1 passageiro",
            origin = "Enseada, Guarujá - SP",
            destination = "Aeroporto de Guarulhos (GRU) - T3",
            fareAmount = 380.00,
            statusText = "Recebido"
        ),
        CompletedTrip(
            id = "LM-2044",
            timeLabel = "Hoje • 09:15",
            passengerName = "Marcio Albuquerque",
            passengerCountText = "1 passageiro",
            origin = "Ponta da Praia, Santos - SP",
            destination = "Av. Brigadeiro Faria Lima, SP",
            fareAmount = 290.00,
            statusText = "Recebido"
        ),
        CompletedTrip(
            id = "LM-2040",
            timeLabel = "Ontem • 19:40",
            passengerName = "Família Siqueira (3 pax)",
            passengerCountText = "3 passageiros",
            origin = "Aeroporto Congonhas (CGH)",
            destination = "Riviera de São Lourenço, Bertioga",
            fareAmount = 450.00,
            statusText = "Recebido"
        )
    )

    private fun getInitialNotifications(): List<FleetNotification> = listOf(
        FleetNotification(
            id = "notif-1",
            title = "Nova Viagem Disponível na Frota",
            description = "Transfer Santos -> Congonhas (LM-2051) liberado para aceite.",
            timeAgo = "Há 5 min",
            isUrgent = true,
            iconName = "bolt"
        ),
        FleetNotification(
            id = "notif-2",
            title = "Planilha Google Sheets Sincronizada",
            description = "Escalas de amanhã atualizadas pela central de operações.",
            timeAgo = "Há 25 min",
            isUrgent = false,
            iconName = "cloud_done"
        ),
        FleetNotification(
            id = "notif-3",
            title = "Condição da Serra dos Imigrantes",
            description = "Pista norte fluida, tempo de descida estimado em 42 minutos.",
            timeAgo = "Há 1 hora",
            isUrgent = false,
            iconName = "traffic"
        )
    )
}
