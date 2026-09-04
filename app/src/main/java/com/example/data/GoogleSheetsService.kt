package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.BackendConfig
import com.example.model.DriverProfile
import com.example.model.RoutePoint
import com.example.model.TripItem
import com.example.model.TripStatusType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GoogleSheetsService(private val context: Context? = null) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val prefs = context?.getSharedPreferences("lem_motoristas_prefs", Context.MODE_PRIVATE)

    var webAppUrl: String
        get() = prefs?.getString("web_app_url", "") ?: ""
        set(value) {
            prefs?.edit()?.putString("web_app_url", value.trim())?.apply()
        }

    // Default authentic drivers straight from Google Apps Script 'Motoristas' sheet
    val defaultFleetDrivers = listOf(
        DriverProfile(
            id = "drv-01",
            name = "Carlos Silva",
            phone = "(12) 98877-6655",
            email = "carlos@litoralemmovimento.com.br",
            vehicleModel = "Chevrolet Spin Premier 7L",
            vehiclePlate = "SP-LIT7A24",
            isOnline = true,
            rating = 4.98,
            totalTrips = 342,
            pixKey = "12988776655",
            shift = "Manhã",
            earningsToday = 0.0,
            completedToday = 0
        ),
        DriverProfile(
            id = "drv-02",
            name = "Marcos Oliveira",
            phone = "(11) 97654-3210",
            email = "marcos@litoralemmovimento.com.br",
            vehicleModel = "Chevrolet Spin LTZ 7L",
            vehiclePlate = "SP-MOV7B88",
            isOnline = true,
            rating = 4.95,
            totalTrips = 289,
            pixKey = "11976543210",
            shift = "Manhã",
            earningsToday = 0.0,
            completedToday = 0
        )
    )

    // Default authentic fleet config straight from Google Apps Script 'Configuracoes' sheet
    val defaultConfig = BackendConfig(
        companyName = "Litoral em Movimento Transfer Executivo",
        contactWhatsapp = "(12) 98850-6597",
        officialPixKey = "12988506597",
        signalPercent = "50%",
        fleetModel = "Chevrolet Spin 7 Lugares"
    )

    suspend fun fetchDrivers(): Result<List<DriverProfile>> = withContext(Dispatchers.IO) {
        val url = webAppUrl
        if (url.isBlank()) {
            return@withContext Result.success(defaultFleetDrivers)
        }

        try {
            val endpoint = if (url.contains("?")) "$url&action=getDrivers" else "$url?action=getDrivers"
            val request = Request.Builder()
                .url(endpoint)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
            }

            val json = JSONObject(body)
            if (json.optString("status") == "ok") {
                val data = json.optJSONArray("data") ?: JSONArray()
                val list = mutableListOf<DriverProfile>()
                for (i in 0 until data.length()) {
                    val obj = data.getJSONObject(i)
                    list.add(
                        DriverProfile(
                            id = obj.optString("id", "drv-0${i + 1}"),
                            name = obj.optString("name", "Motorista"),
                            phone = obj.optString("phone", ""),
                            email = obj.optString("email", ""),
                            vehicleModel = obj.optString("vehicle", "Chevrolet Spin 7L"),
                            vehiclePlate = obj.optString("plate", "SP-LEM7L"),
                            isOnline = obj.optString("status", "Disponível").contains("Disponível", ignoreCase = true),
                            rating = obj.optDouble("rating", 4.9),
                            totalTrips = obj.optInt("trips", 0),
                            pixKey = obj.optString("pix", "")
                        )
                    )
                }
                Result.success(if (list.isNotEmpty()) list else defaultFleetDrivers)
            } else {
                Result.failure(Exception(json.optString("message", "Erro ao buscar motoristas")))
            }
        } catch (e: Exception) {
            Log.w("GoogleSheetsService", "Falha ao conectar no Google Apps Script para motoristas: ${e.message}")
            Result.success(defaultFleetDrivers)
        }
    }

    suspend fun fetchReservations(): Result<List<TripItem>> = withContext(Dispatchers.IO) {
        val url = webAppUrl
        if (url.isBlank()) {
            return@withContext Result.success(emptyList())
        }

        try {
            val endpoint = if (url.contains("?")) "$url&action=getReservations" else "$url?action=getReservations"
            val request = Request.Builder()
                .url(endpoint)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
            }

            val json = JSONObject(body)
            if (json.optString("status") == "ok") {
                val data = json.optJSONArray("data") ?: JSONArray()
                val list = mutableListOf<TripItem>()
                for (i in 0 until data.length()) {
                    val obj = data.getJSONObject(i)
                    val id = obj.optString("id", "res-$i")
                    val code = obj.optString("code", "LM-${2000 + i}")
                    val rawStatus = obj.optString("status", "Pendente")
                    val assignedDriver = obj.optString("assignedDriverName").takeIf { it.isNotBlank() }
                    val driverVehicle = obj.optString("driverVehicle").takeIf { it.isNotBlank() }
                    val flightNum = obj.optString("flightNumber").takeIf { it.isNotBlank() }
                    val notes = obj.optString("notes").takeIf { it.isNotBlank() }
                    val tripType = obj.optString("tripType", "Individual (Exclusivo)")
                    val orig = obj.optString("origin", "Origem")
                    val origDet = obj.optString("originDetails", "")
                    val dest = obj.optString("destination", "Destino")
                    val destDet = obj.optString("destinationDetails", "")
                    val date = obj.optString("date", "Hoje")
                    val time = obj.optString("time", "12:00")
                    val pax = obj.optInt("passengers", 1)
                    val luggage = obj.optInt("luggageCount", 1)
                    val childSeat = obj.optBoolean("hasChildSeat", false) || obj.optString("hasChildSeat").equals("Sim", ignoreCase = true)
                    val totalPrice = obj.optDouble("totalPrice", 0.0)
                    val depositAmount = obj.optDouble("depositAmount", 0.0)
                    val remainingAmount = obj.optDouble("remainingAmount", totalPrice - depositAmount)
                    val depositPaid = obj.optBoolean("depositPaid", false) || obj.optString("depositPaid").equals("Sim", ignoreCase = true)
                    val paymentMethod = obj.optString("paymentMethod", "PIX Copia e Cola")
                    val paymentStatus = obj.optString("paymentStatus", if (depositPaid) "Sinal Pago (50%)" else "Aguardando Sinal")

                    val statusType = when {
                        rawStatus.contains("Conclu", ignoreCase = true) -> TripStatusType.CONCLUIDO
                        rawStatus.contains("A caminho", ignoreCase = true) || rawStatus.contains("Andamento", ignoreCase = true) -> TripStatusType.EM_ANDAMENTO
                        rawStatus.contains("Confirmad", ignoreCase = true) -> TripStatusType.CONFIRMADO
                        rawStatus.contains("Cancel", ignoreCase = true) -> TripStatusType.RECUSADO
                        assignedDriver.isNullOrBlank() -> TripStatusType.LIVRE_NA_FROTA
                        else -> TripStatusType.PENDENTE_ACEITE
                    }

                    list.add(
                        TripItem(
                            id = id,
                            code = code,
                            transferType = tripType,
                            timeLabel = "$time • $date",
                            date = date,
                            status = statusType,
                            statusBadgeText = when (statusType) {
                                TripStatusType.CONFIRMADO -> "Confirmado"
                                TripStatusType.PENDENTE_ACEITE -> "Aguardando Aceite"
                                TripStatusType.LIVRE_NA_FROTA -> "Livre na Frota"
                                TripStatusType.EM_ANDAMENTO -> "Em Rota"
                                TripStatusType.CONCLUIDO -> "Concluído"
                                TripStatusType.RECUSADO -> "Recusado"
                                TripStatusType.AGENDADO -> "Agendado"
                            },
                            isAvailableToClaim = assignedDriver.isNullOrBlank(),
                            isAssignedToMe = false, // will be resolved per driver
                            isAcceptedByDriver = statusType == TripStatusType.CONFIRMADO || statusType == TripStatusType.EM_ANDAMENTO,
                            driverId = null,
                            assignedDriverName = assignedDriver,
                            driverVehicle = driverVehicle,
                            origin = RoutePoint(
                                badge = "A",
                                categoryTag = "Origem",
                                title = orig,
                                subtitle = origDet.ifEmpty { "Ponto de Encontro" }
                            ),
                            destination = RoutePoint(
                                badge = "B",
                                categoryTag = "Destino",
                                title = dest,
                                subtitle = destDet.ifEmpty { "Desembarque" }
                            ),
                            distanceInfo = "$tripType • Frota Spin 7L",
                            passengersCount = pax,
                            luggageInfo = "$luggage malas",
                            hasChildSeat = childSeat,
                            notes = notes,
                            flightNumber = flightNum,
                            passengerName = obj.optString("customerName", "Passageiro"),
                            passengerPhone = obj.optString("customerPhone", ""),
                            totalPrice = totalPrice,
                            depositAmount = depositAmount,
                            remainingAmount = remainingAmount,
                            depositPaid = depositPaid,
                            paymentStatus = paymentStatus,
                            payoutAmount = if (remainingAmount > 0) remainingAmount else totalPrice,
                            payoutLabel = "Saldo no Embarque",
                            paymentMethod = paymentMethod
                        )
                    )
                }
                Result.success(list)
            } else {
                Result.failure(Exception(json.optString("message", "Erro ao carregar reservas")))
            }
        } catch (e: Exception) {
            Log.w("GoogleSheetsService", "Falha ao consultar reservas da planilha: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchConfig(): Result<BackendConfig> = withContext(Dispatchers.IO) {
        val url = webAppUrl
        if (url.isBlank()) {
            return@withContext Result.success(defaultConfig)
        }

        try {
            val endpoint = if (url.contains("?")) "$url&action=getConfig" else "$url?action=getConfig"
            val request = Request.Builder().url(endpoint).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }

            val json = JSONObject(body)
            if (json.optString("status") == "ok") {
                val data = json.optJSONObject("data") ?: JSONObject()
                val config = BackendConfig(
                    companyName = data.optString("NOME_EMPRESA", defaultConfig.companyName),
                    contactWhatsapp = data.optString("CONTATO_WHATSAPP", defaultConfig.contactWhatsapp),
                    officialPixKey = data.optString("CHAVE_PIX_OFICIAL", defaultConfig.officialPixKey),
                    signalPercent = data.optString("PERCENTUAL_SINAL", defaultConfig.signalPercent),
                    fleetModel = data.optString("FROTA_OFICIAL", defaultConfig.fleetModel),
                    webAppUrl = url
                )
                Result.success(config)
            } else {
                Result.failure(Exception(json.optString("message")))
            }
        } catch (e: Exception) {
            Result.success(defaultConfig)
        }
    }

    suspend fun postAction(action: String, payload: JSONObject): Result<String> = withContext(Dispatchers.IO) {
        val url = webAppUrl
        if (url.isBlank()) {
            return@withContext Result.success("Operação local concluída (modo offline)")
        }

        try {
            payload.put("action", action)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = payload.toString().toRequestBody(mediaType)
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Result.success(respBody)
            } else {
                Result.failure(Exception("HTTP ${response.code}: $respBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
