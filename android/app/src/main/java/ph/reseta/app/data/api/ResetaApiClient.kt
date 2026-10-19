package ph.reseta.app.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ph.reseta.app.BuildConfig
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan
import ph.reseta.model.SafetySeverity
import ph.reseta.model.SafetyWarning
import java.io.IOException
import java.util.concurrent.TimeUnit

@Serializable
data class ScanApiRequest(
    val mimeType: String,
    val base64Data: String,
    val deepScan: Boolean = false,
    val language: String = "fil"
)

@Serializable
data class ApiRawLine(
    val rawText: String,
    val isMedication: Boolean
)

@Serializable
data class ApiSafetyWarning(
    val code: String,
    val severity: String,
    val messageTl: String,
    val messageEn: String
)

@Serializable
data class ApiMedicationItem(
    val id: String = "",
    val writtenAs: String,
    val brandName: String? = null,
    val genericName: String,
    val strength: String,
    val form: String,
    val quantity: Int = 1,
    val dosesPerDay: Int = 1,
    val durationDays: Int? = null,
    val slots: List<String> = emptyList(),
    val mealNoteTl: String? = null,
    val mealNoteEn: String? = null,
    val noteTl: String? = null,
    val noteEn: String? = null,
    val uncertainFields: List<String> = emptyList(),
    val catalogId: String? = null,
    val isMatched: Boolean = false,
    val safetyWarnings: List<ApiSafetyWarning> = emptyList()
)

@Serializable
data class ScanApiResponse(
    val id: String? = null,
    val lines: List<ApiRawLine> = emptyList(),
    val medicationLineCount: Int = 0,
    val lineCountMismatch: Boolean = false,
    val mismatchNotice: String? = null,
    val doctorName: String? = null,
    val prcNumber: String? = null,
    val dateOnSlip: String? = null,
    val readScore: Int = 85,
    val items: List<ApiMedicationItem> = emptyList(),
    val scannedAtTimestamp: Long = 0L,
    val error: String? = null
)

class ResetaApiClient(
    private val baseUrl: String = BuildConfig.API_BASE_URL
) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun scanPrescription(
        imageBytes: ByteArray,
        mimeType: String = "image/jpeg",
        deepScan: Boolean = false,
        installId: String
    ): Result<PrescriptionScan> = withContext(Dispatchers.IO) {
        val base64Data = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)
        val payload = ScanApiRequest(
            mimeType = mimeType,
            base64Data = base64Data,
            deepScan = deepScan,
            language = "fil"
        )

        val requestBody = json.encodeToString(ScanApiRequest.serializer(), payload)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val url = if (baseUrl.endsWith("/")) "${baseUrl}api/scan" else "$baseUrl/api/scan"

        val request = Request.Builder()
            .url(url)
            .header("x-install-id", installId) // Rule 6 & 8: Rate-limiting install ID header
            .header("Accept", "application/json")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errorParsed = json.decodeFromString<ScanApiResponse>(bodyString)
                    errorParsed.error ?: "Server error (${response.code})"
                } catch (_: Exception) {
                    when (response.code) {
                        429 -> "Masyadong maraming request. Maghintay ng 1 minuto bago mag-scan muli."
                        504 -> "Nag-timeout ang server. Pakisubukan muli."
                        413 -> "Masyadong malaki ang litrato ng reseta."
                        else -> "Server returned code ${response.code}"
                    }
                }
                return@withContext Result.failure(IOException(errorMsg))
            }

            val apiResponse = json.decodeFromString<ScanApiResponse>(bodyString)
            if (apiResponse.error != null) {
                return@withContext Result.failure(IOException(apiResponse.error))
            }

            // Map API items to pure domain PrescriptionItem
            val domainItems = apiResponse.items.mapIndexed { idx, it ->
                val warnings = it.safetyWarnings.map { w ->
                    val severity = when (w.severity.uppercase()) {
                        "WARNING" -> SafetySeverity.WARNING
                        "INFO" -> SafetySeverity.INFO
                        else -> SafetySeverity.CAUTION
                    }
                    SafetyWarning(
                        code = w.code,
                        severity = severity,
                        messageTl = w.messageTl,
                        messageEn = w.messageEn
                    )
                }

                PrescriptionItem(
                    id = it.id.ifEmpty { "item-${idx + 1}" },
                    writtenAs = it.writtenAs,
                    brandName = it.brandName,
                    genericName = it.genericName,
                    strength = it.strength,
                    form = it.form,
                    quantity = it.quantity,
                    dosesPerDay = it.dosesPerDay,
                    durationDays = it.durationDays,
                    slots = it.slots,
                    mealNoteTl = it.mealNoteTl,
                    mealNoteEn = it.mealNoteEn,
                    noteTl = it.noteTl,
                    noteEn = it.noteEn,
                    uncertainFields = it.uncertainFields,
                    safetyWarnings = warnings
                )
            }

            val scan = PrescriptionScan(
                id = apiResponse.id ?: "scan-${System.currentTimeMillis()}",
                items = domainItems,
                doctorName = apiResponse.doctorName,
                prcNumber = apiResponse.prcNumber,
                dateOnSlip = apiResponse.dateOnSlip,
                readScore = apiResponse.readScore,
                medicationLineCount = apiResponse.medicationLineCount,
                scannedAtTimestamp = if (apiResponse.scannedAtTimestamp > 0) apiResponse.scannedAtTimestamp else System.currentTimeMillis()
            )

            Result.success(scan)
        } catch (e: Exception) {
            val friendly = when (e) {
                is java.net.ConnectException -> "Hindi maikonekta sa server (${url}). Tiyaking bukas ang server at nakakonekta ang device sa tamang port (adb reverse tcp:3000 tcp:3000)."
                is java.net.SocketTimeoutException -> "Nag-timeout ang koneksyon sa server. Pakisubukan muli."
                else -> e.message ?: "Failed to scan prescription"
            }
            Result.failure(IOException(friendly, e))
        }
    }
}
