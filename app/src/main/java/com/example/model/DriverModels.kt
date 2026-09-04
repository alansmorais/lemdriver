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
    val driverId: String? = null,
    val origin: RoutePoint,
    val destination: RoutePoint,
    val distanceInfo: String,
    val passengersCount: Int,
    val luggageInfo: String,
    val notes: String? = null,
    val passengerName: String,
    val passengerPhone: String = "+5511999999999",
    val payoutAmount: Double,
    val payoutLabel: String = "A Receber",
    val paymentMethod: String = "PIX"
) {
    val passengerInitials: String
        get() = passengerName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()
}

data class DriverProfile(
    val id: String,
    val name: String,
    val vehicleModel: String,
    val vehiclePlate: String,
    val isOnline: Boolean,
    val shift: String,
    val earningsToday: Double,
    val completedToday: Int,
    val phone: String = "+5513998887766",
    val pixKey: String = "carlos.transfers@litoral.com.br"
) {
    val initials: String
        get() = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()
}

data class ActiveTransfer(
    val tripId: String,
    val passengerName: String,
    val passengerPhone: String,
    val currentStepIndex: Int, // 0: Despacho, 1: A caminho, 2: Embarcado, 3: Concluído
    val routeSummary: String,
    val origin: RoutePoint,
    val destination: RoutePoint,
    val notes: String? = null,
    val fareToCollect: Double,
    val paymentMode: String
) {
    val passengerInitials: String
        get() = passengerName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()
}

data class CompletedTrip(
    val id: String,
    val timeLabel: String,
    val passengerName: String,
    val passengerCountText: String? = null,
    val origin: String,
    val destination: String,
    val fareAmount: Double,
    val paymentMethod: String = "PIX",
    val statusText: String = "Concluído"
)

data class EarningsSummary(
    val weeklyTotal: Double,
    val tripsCompletedWeekly: Int,
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
