package com.example.model

enum class AppScreen(val label: String, val routeName: String) {
    LOGIN("Login", "login"),
    VIAGENS("Viagens", "viagens"),
    EM_ROTA("Em Rota", "em_rota"),
    GANHOS("Ganhos", "ganhos"),
    PERFIL("Perfil", "perfil")
}

enum class TripCategory(val label: String) {
    MINHAS("Minhas"),
    DISPONIVEIS("Disponíveis"),
    TODAS("Todas")
}

enum class TripStatusType {
    CONFIRMADO,
    LIVRE_NA_FROTA,
    AGENDADO,
    EM_ANDAMENTO,
    CONCLUIDO
}

data class RoutePoint(
    val badge: String,
    val categoryTag: String,
    val title: String,
    val subtitle: String
)

data class TripItem(
    val id: String,
    val transferType: String,
    val timeLabel: String,
    val status: TripStatusType,
    val statusBadgeText: String,
    val isAvailableToClaim: Boolean = false,
    val origin: RoutePoint,
    val destination: RoutePoint,
    val distanceInfo: String,
    val passengersCount: Int,
    val luggageInfo: String,
    val specialPerk: String? = null,
    val passengerName: String,
    val passengerSubtitle: String,
    val passengerInitials: String,
    val passengerPhone: String = "+5511999999999",
    val passengerAvatarUrl: String? = null,
    val payoutAmount: Double,
    val payoutLabel: String = "A Receber",
    val flightDetails: String? = null,
    val paymentMethod: String = "PIX ou Dinheiro"
)

data class DriverProfile(
    val id: String,
    val name: String,
    val vehicleModel: String,
    val vehiclePlate: String,
    val rating: Double,
    val completedTripsCount: Int,
    val isOnline: Boolean,
    val shift: String,
    val avatarUrl: String,
    val earningsToday: Double,
    val completedToday: Int,
    val phone: String = "+5513998887766",
    val pixKey: String = "carlos.transfers@litoral.com.br"
)

data class ActiveTransfer(
    val tripId: String,
    val passengerName: String,
    val passengerSubtitle: String,
    val passengerAvatarUrl: String,
    val passengerPhone: String,
    val remainingMinutes: Int,
    val remainingDistanceKm: Double,
    val estimatedArrival: String,
    val currentStepIndex: Int, // 0: Despacho, 1: A caminho, 2: Embarcado, 3: Concluído
    val routeSummary: String,
    val trafficCondition: String,
    val origin: RoutePoint,
    val destination: RoutePoint,
    val centralAlert: String,
    val fareToCollect: Double,
    val paymentMode: String,
    val paymentNote: String,
    val mapImageUrl: String
)

data class CompletedTrip(
    val id: String,
    val timeLabel: String,
    val passengerName: String,
    val passengerCountText: String? = null,
    val origin: String,
    val destination: String,
    val fareAmount: Double,
    val statusText: String = "Recebido"
)

data class EarningsSummary(
    val weeklyTotal: Double,
    val tripsCompletedWeekly: Int,
    val goalPercentage: Int,
    val receivedOnSite: Double,
    val fleetSettlement: Double,
    val todayTotal: Double,
    val todayCompletedCount: Int
)

data class FleetNotification(
    val id: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val isUrgent: Boolean = false,
    val iconName: String = "notifications"
)
