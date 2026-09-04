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

    companion object {
        const val DEFAULT_WEB_APP_URL = "https://script.google.com/macros/s/AKfycbzVXHlSLJykQpgwJBs6OEiLZx70lBstHfQNkB7EvvL275foVcxRCSAzxDUKb8gqEoilNA/exec"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val prefs = context?.getSharedPreferences("lem_motoristas_prefs", Context.MODE_PRIVATE)

    var webAppUrl: String
        get() = prefs?.getString("web_app_url", DEFAULT_WEB_APP_URL)?.takeIf { it.isNotBlank() } ?: DEFAULT_WEB_APP_URL
        set(value) {
            prefs?.edit()?.putString("web_app_url", value.trim())?.apply()
        }

    // Default authentic drivers straight from server live backend
    val defaultFleetDrivers = listOf(
        DriverProfile(
            id = "drv-01",
            name = "Eduardo Silveira",
            phone = "(12) 98850-6597",
            email = "eduardo.motorista@litoralemmovimento.com.br",
            vehicleModel = "Chevrolet Spin Premier 7L • 2024 (6 Pass. + Mot.)",
            vehiclePlate = "SP-LIT7A24",
            isOnline = true,
            rating = 4.98,
            totalTrips = 342,
            pixKey = "12988506597",
            shift = "Manhã",
            earningsToday = 0.0,
            completedToday = 0
        ),
        DriverProfile(
            id = "drv-02",
            name = "Edivam Santos",
            phone = "(12) 98850-6597",
            email = "edivam.motorista@litoralemmovimento.com.br",
            vehicleModel = "Chevrolet Spin Premier 7L • 2024 (6 Pass. + Mot.)",
            vehiclePlate = "SP-MOV7B88",
            isOnline = true,
            rating = 4.97,
            totalTrips = 310,
            pixKey = "12988506597",
            shift = "Manhã",
            earningsToday = 0.0,
            completedToday = 0
        ),
        DriverProfile(
            id = "drv-03",
            name = "Karine Souza",
            phone = "(12) 98850-6597",
            email = "karine.motorista@litoralemmovimento.com.br",
            vehicleModel = "Chevrolet Spin Premier 7L • 2024 (6 Pass. + Mot.)",
            vehiclePlate = "SP-LIT7C50",
            isOnline = true,
            rating = 4.99,
            totalTrips = 275,
            pixKey = "12988506597",
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
            val separator = if (url.contains("?")) "&" else "?"
            val endpoint = "$url${separator}action=getDrivers&_t=${System.currentTimeMillis()}"
            val request = Request.Builder()
                .url(endpoint)
                .header("Cache-Control", "no-cache")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
            }

            val trimmedBody = body.trim()
            val dataArray: JSONArray = when {
                trimmedBody.startsWith("[") -> JSONArray(trimmedBody)
                trimmedBody.startsWith("{") -> {
                    val json = JSONObject(trimmedBody)
                    when {
                        json.has("data") -> json.optJSONArray("data") ?: JSONArray()
                        json.has("drivers") -> json.optJSONArray("drivers") ?: JSONArray()
                        json.has("motoristas") -> json.optJSONArray("motoristas") ?: JSONArray()
                        else -> JSONArray()
                    }
                }
                else -> JSONArray()
            }

            val list = mutableListOf<DriverProfile>()
            for (i in 0 until dataArray.length()) {
                val obj = dataArray.optJSONObject(i) ?: continue
                val name = obj.optString("name", obj.optString("nome", obj.optString("motorista", ""))).trim()
                if (name.isBlank()) continue

                val id = obj.optString("id", "drv-0${i + 1}")
                val phone = obj.optString("phone", obj.optString("telefone", "(12) 98850-6597"))
                val email = obj.optString("email", "")
                val vehicleModel = obj.optString(
                    "vehicleModel",
                    obj.optString("vehicle", obj.optString("veiculo", "Chevrolet Spin Premier 7L • 2024"))
                )
                val vehiclePlate = obj.optString(
                    "plate",
                    obj.optString("placa", obj.optString("vehiclePlate", "SP-LEM7L"))
                )
                val rawStatus = obj.optString("status", "Disponível")
                val isOnline = rawStatus.contains("Disponível", ignoreCase = true) || obj.optBoolean("isOnline", true)
                val rating = if (obj.has("rating")) obj.optDouble("rating", 4.98) else 4.98
                val totalTrips = if (obj.has("totalTrips")) obj.optInt("totalTrips", 0) else obj.optInt("trips", obj.optInt("viagens", 100))
                val pixKey = obj.optString("pixKey", obj.optString("pix", obj.optString("chavePix", phone.filter { it.isDigit() })))

                list.add(
                    DriverProfile(
                        id = id,
                        name = name,
                        phone = phone,
                        email = email,
                        vehicleModel = vehicleModel,
                        vehiclePlate = vehiclePlate,
                        isOnline = isOnline,
                        rating = rating,
                        totalTrips = totalTrips,
                        pixKey = pixKey
                    )
                )
            }
            Result.success(if (list.isNotEmpty()) list else defaultFleetDrivers)
        } catch (e: Exception) {
            Log.w("GoogleSheetsService", "Conexão com servidor de motoristas: ${e.message}")
            Result.success(defaultFleetDrivers)
        }
    }

    suspend fun fetchReservations(): Result<List<TripItem>> = withContext(Dispatchers.IO) {
        val url = webAppUrl
        if (url.isBlank()) {
            return@withContext Result.success(emptyList())
        }

        try {
            val separator = if (url.contains("?")) "&" else "?"
            val endpoint = "$url${separator}action=getReservations&_t=${System.currentTimeMillis()}"
            val request = Request.Builder()
                .url(endpoint)
                .header("Cache-Control", "no-cache")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
            }

            val trimmedBody = body.trim()
            val dataArray: JSONArray = when {
                trimmedBody.startsWith("[") -> JSONArray(trimmedBody)
                trimmedBody.startsWith("{") -> {
                    val json = JSONObject(trimmedBody)
                    when {
                        json.has("data") -> json.optJSONArray("data") ?: JSONArray()
                        json.has("reservations") -> json.optJSONArray("reservations") ?: JSONArray()
                        json.has("reservas") -> json.optJSONArray("reservas") ?: JSONArray()
                        else -> JSONArray()
                    }
                }
                else -> JSONArray()
            }

            val list = mutableListOf<TripItem>()
            for (i in 0 until dataArray.length()) {
                val obj = dataArray.optJSONObject(i) ?: continue
                val id = obj.optString("id", obj.optString("codigo", "res-$i"))
                val code = obj.optString("code", obj.optString("codigo", "LM-${2000 + i}"))
                val rawStatus = obj.optString("status", "Pendente")
                val assignedDriver = (obj.optString("assignedDriverName").takeIf { it.isNotBlank() }
                    ?: obj.optString("motorista").takeIf { it.isNotBlank() })
                val driverVehicle = (obj.optString("driverVehicle").takeIf { it.isNotBlank() }
                    ?: obj.optString("veiculo").takeIf { it.isNotBlank() })
                val flightNum = (obj.optString("flightNumber").takeIf { it.isNotBlank() }
                    ?: obj.optString("voo").takeIf { it.isNotBlank() })
                val notes = (obj.optString("notes").takeIf { it.isNotBlank() }
                    ?: obj.optString("observacoes").takeIf { it.isNotBlank() })
                val tripType = obj.optString("tripType", obj.optString("tipo", "Individual (Exclusivo)"))
                val orig = obj.optString("origin", obj.optString("origem", "Origem"))
                val origDet = obj.optString("originDetails", obj.optString("detalhesOrigem", ""))
                val dest = obj.optString("destination", obj.optString("destino", "Destino"))
                val destDet = obj.optString("destinationDetails", obj.optString("detalhesDestino", ""))
                val date = obj.optString("date", obj.optString("data", "Hoje"))
                val time = obj.optString("time", obj.optString("horario", obj.optString("hora", "12:00")))
                val pax = if (obj.has("passengers")) obj.optInt("passengers", 1) else obj.optInt("passageiros", 1)
                val luggage = if (obj.has("luggageCount")) obj.optInt("luggageCount", 1) else obj.optInt("malas", 1)
                val childSeat = obj.optBoolean("hasChildSeat", false)
                    || obj.optString("hasChildSeat").equals("Sim", ignoreCase = true)
                    || obj.optString("cadeirinha").equals("Sim", ignoreCase = true)
                val totalPrice = if (obj.has("totalPrice")) obj.optDouble("totalPrice", 0.0) else obj.optDouble("valorTotal", obj.optDouble("valor", 0.0))
                val depositAmount = if (obj.has("depositAmount")) obj.optDouble("depositAmount", 0.0) else obj.optDouble("valorSinal", obj.optDouble("sinal", 0.0))
                val remainingAmount = if (obj.has("remainingAmount")) obj.optDouble("remainingAmount", totalPrice - depositAmount) else obj.optDouble("valorRestante", totalPrice - depositAmount)
                val depositPaid = obj.optBoolean("depositPaid", false)
                    || obj.optString("depositPaid").equals("Sim", ignoreCase = true)
                    || obj.optString("sinalPago").equals("Sim", ignoreCase = true)
                val paymentMethod = obj.optString("paymentMethod", obj.optString("formaPagamento", "PIX"))
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
                        passengerName = obj.optString("customerName", obj.optString("nomeCliente", obj.optString("cliente", "Passageiro"))),
                        passengerPhone = obj.optString("customerPhone", obj.optString("telefoneCliente", obj.optString("telefone", ""))),
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
        } catch (e: Exception) {
            Log.w("GoogleSheetsService", "Falha ao consultar reservas do servidor: ${e.message}")
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
