package com.example.model

enum class AppScreen(val label: String, val routeName: String) {
    LOGIN("Login", "login"),
    VIAGENS("Escalas", "viagens"),
    EM_ROTA("Em Rota", "em_rota"),
    GANHOS("Ganhos", "ganhos"),
    PERFIL("Perfil", "perfil")
}

enum class TripCategory(val label: String) {
    MINHAS("Minhas Escalas"),
    DISPONIVEIS("Disponíveis"),
    TODAS("Todas as Viagens")
}

enum class TripStatusType {
    CONFIRMADO,
    LIVRE_NA_FROTA,
    PENDENTE_ACEITE,
    AGENDADO,
    EM_ANDAMENTO,
    CONCLUIDO,
    RECUSADO
}

data class RoutePoint(
    val badge: String,
    val categoryTag: String,
    val title: String,
    val subtitle: String
)

data class TripItem(
    val id: String,
    val code: String = "",
    val transferType: String = "Individual (Exclusivo)",
    val timeLabel: String = "",
    val date: String = "",
    val status: TripStatusType = TripStatusType.CONFIRMADO,
    val statusBadgeText: String = "Confirmado",
    val isAvailableToClaim: Boolean = false,
    val isAssignedToMe: Boolean = false,
    val isAcceptedByDriver: Boolean = true,
    val driverId: String? = null,
    val assignedDriverName: String? = null,
    val driverVehicle: String? = null,
    val origin: RoutePoint,
    val destination: RoutePoint,
    val distanceInfo: String = "",
    val passengersCount: Int = 1,
    val luggageInfo: String = "1 mala",
    val hasChildSeat: Boolean = false,
    val notes: String? = null,
    val flightNumber: String? = null,
    val passengerName: String,
    val passengerPhone: String = "",
    val totalPrice: Double = 0.0,
    val depositAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val depositPaid: Boolean = false,
    val paymentStatus: String = "Aguardando Sinal 50%",
    val payoutAmount: Double = 0.0, // Saldo no Embarque a cobrar
    val payoutLabel: String = "Saldo no Embarque",
    val paymentMethod: String = "PIX Copia e Cola"
) {
    val passengerInitials: String
        get() = passengerName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()
            .ifEmpty { "PAX" }
}

data class DriverProfile(
    val id: String,
    val name: String,
    val vehicleModel: String,
    val vehiclePlate: String,
    val isOnline: Boolean = true,
    val shift: String = "Manhã",
    val earningsToday: Double = 0.0,
    val completedToday: Int = 0,
    val phone: String = "",
    val email: String = "",
    val rating: Double = 5.0,
    val totalTrips: Int = 0,
    val pixKey: String = ""
) {
    val initials: String
        get() = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()
            .ifEmpty { "LEM" }
}

data class ActiveTransfer(
    val tripId: String,
    val code: String = "",
    val passengerName: String,
    val passengerPhone: String,
    val currentStepIndex: Int = 1, // 0: Despacho/Aceite, 1: A caminho do embarque, 2: Cheguei no local / Embarcado, 3: Em rota ao destino, 4: Concluído
    val routeSummary: String,
    val origin: RoutePoint,
    val destination: RoutePoint,
    val notes: String? = null,
    val flightNumber: String? = null,
    val baseFareToCollect: Double,
    val extraKm: Double = 0.0,
    val extraKmRate: Double = 3.50,
    val otherExtrasAmount: Double = 0.0,
    val extraReason: String? = null,
    val fareToCollect: Double = baseFareToCollect + (extraKm * extraKmRate) + otherExtrasAmount,
    val paymentMode: String = "PIX Copia e Cola"
) {
    val totalExtras: Double
        get() = (extraKm * extraKmRate) + otherExtrasAmount

    val passengerInitials: String
        get() = passengerName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first() }
            .joinToString("")
            .uppercase()
            .ifEmpty { "PAX" }
}

data class CompletedTrip(
    val id: String,
    val code: String = "",
    val timeLabel: String,
    val passengerName: String,
    val passengerCountText: String? = null,
    val origin: String,
    val destination: String,
    val fareAmount: Double,
    val extraKmAdded: Double = 0.0,
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

data class BackendConfig(
    val companyName: String = "Litoral em Movimento Transfer Executivo",
    val contactWhatsapp: String = "(12) 98850-6597",
    val officialPixKey: String = "12988506597",
    val signalPercent: String = "50%",
    val fleetModel: String = "Chevrolet Spin 7 Lugares",
    val webAppUrl: String = ""
)
