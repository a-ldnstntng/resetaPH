package ph.reseta.app.data.api

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ph.reseta.app.BuildConfig
import ph.reseta.app.util.NetworkUtils
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan
import ph.reseta.model.SafetySeverity
import ph.reseta.model.SafetyWarning
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

private const val TAG = "ResetaNetwork"

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
    private val context: Context? = null,
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
        // 1. Device network check
        if (context != null && !NetworkUtils.isNetworkAvailable(context)) {
            val netType = NetworkUtils.getNetworkType(context)
            Log.e(TAG, "Request aborted: No active internet connection detected (Transport: $netType)")
            return@withContext Result.failure(
                IOException("Walang koneksyon sa internet. Pakisuri ang iyong Wi-Fi o mobile data bago mag-scan muli.")
            )
        }

        val base64Data = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)
        val payload = ScanApiRequest(
            mimeType = mimeType,
            base64Data = base64Data,
            deepScan = deepScan,
            language = "fil"
        )

        val requestBody = json.encodeToString(ScanApiRequest.serializer(), payload)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val primaryUrl = if (baseUrl.endsWith("/")) "${baseUrl}api/scan" else "$baseUrl/api/scan"

        // Candidate URLs: primary LAN URL first, plus 10.0.2.2 fallback if running on emulator
        val candidateUrls = mutableListOf(primaryUrl)
        val isEmulator = Build.FINGERPRINT.startsWith("generic") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu")

        if (isEmulator && !primaryUrl.contains("10.0.2.2") && !primaryUrl.contains("localhost")) {
            candidateUrls.add("http://10.0.2.2:3000/api/scan")
        }

        var lastException: Exception? = null

        for (url in candidateUrls) {
            val startTime = System.currentTimeMillis()
            Log.i(TAG, "--> POST $url (Payload: ${imageBytes.size} bytes, InstallId: ${installId.take(8)}..., DeepScan: $deepScan)")

            val request = Request.Builder()
                .url(url)
                .header("x-install-id", installId) // Rate-limiting install ID header
                .header("Accept", "application/json")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                val duration = System.currentTimeMillis() - startTime
                val bodyString = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    Log.e(TAG, "<-- HTTP ERROR ${response.code} from $url in ${duration}ms: $bodyString")
                    val errorMsg = try {
                        val errorParsed = json.decodeFromString<ScanApiResponse>(bodyString)
                        errorParsed.error ?: "Server error (${response.code})"
                    } catch (_: Exception) {
                        when (response.code) {
                            429 -> "Masyadong maraming request. Maghintay ng 1 minuto bago mag-scan muli."
                            504 -> "Nag-timeout ang server. Pakisubukan muli."
                            413 -> "Masyadong malaki ang litrato ng reseta (lampas 20MB limit)."
                            500 -> "Server internal error: Pakitiyak na naka-set ang GEMINI_API_KEY sa .env file ng server."
                            else -> "Server returned HTTP ${response.code}: $bodyString"
                        }
                    }
                    return@withContext Result.failure(IOException(errorMsg))
                }

                val apiResponse = json.decodeFromString<ScanApiResponse>(bodyString)
                if (apiResponse.error != null) {
                    Log.e(TAG, "<-- API Logical Error from $url: ${apiResponse.error}")
                    return@withContext Result.failure(IOException(apiResponse.error))
                }

                Log.i(TAG, "<-- SUCCESS 200 from $url in ${duration}ms: Transcribed ${apiResponse.items.size} medications")

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

                return@withContext Result.success(scan)
            } catch (e: Exception) {
                val duration = System.currentTimeMillis() - startTime
                Log.e(TAG, "<-- FAILED request to $url after ${duration}ms: ${e.javaClass.simpleName} - ${e.message}", e)
                lastException = e
                // Continue to next candidate URL if available
            }
        }

        // Format user-friendly error with clear diagnostic guidance
        val err = lastException
        val friendlyMessage = when (err) {
            is UnknownHostException ->
                "Hindi ma-resolba ang server ($primaryUrl).\nWalang internet o mali ang hostname."
            is ConnectException ->
                "Hindi makakonekta sa server ($primaryUrl).\n\nPakisuri ang mga sumusunod:\n1. Naka-on ang Wi-Fi ng telepono at pareho ng Wi-Fi network ng computer.\n2. Tumatakbo ang backend server gamit ang 'npm run dev' (nakikinig sa 0.0.0.0:3000).\n3. Walang firewall na humaharang sa port 3000 ng computer."
            is SocketTimeoutException ->
                "Nag-timeout ang koneksyon sa server pagkatapos ng 60 segundo.\nMaaaring mabagal ang internet o matagal sumagot ang AI service. Pakisubukan muli."
            else ->
                err?.message ?: "Nabigo ang pag-scan ng reseta."
        }

        Result.failure(IOException(friendlyMessage, err))
    }
}
