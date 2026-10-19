package ph.reseta.model

import kotlinx.serialization.Serializable

/**
 * Pure Kotlin data models for ResetaPH (Rule 1: No Android imports).
 */

@Serializable
enum class PharmacyChain(val displayName: String, val shortName: String) {
    TGP("The Generics Pharmacy", "TGP"),
    MERCURY_DRUG("Mercury Drug", "Mercury"),
    WATSONS("Watsons", "Watsons"),
    SOUTH_STAR("South Star Drug", "South Star"),
    ROSE_PHARMACY("Rose Pharmacy", "Rose")
}

@Serializable
enum class SafetySeverity {
    INFO,
    CAUTION,
    WARNING
}

@Serializable
data class SafetyWarning(
    val code: String,
    val severity: SafetySeverity,
    val messageTl: String,
    val messageEn: String
)

/**
 * Rule 2: Prices come only from dated price records. Never hardcode a price.
 * The app labels prices as estimates and shows their recorded date.
 */
@Serializable
data class DatedPrice(
    val amountPhp: Double,
    val recordedDate: String = "",
    val source: String = "Sample Data",
    val isSample: Boolean = true,
    val isEstimate: Boolean = true,
    val evidenceNote: String = ""
) {
    fun labelTl(): String = if (isSample) "₱%.2f (Halimbawang presyo)".format(amountPhp) else "₱%.2f (Tantya · %s)".format(amountPhp, recordedDate)
    fun labelEn(): String = if (isSample) "₱%.2f (Sample price)".format(amountPhp) else "₱%.2f (Estimate · %s)".format(amountPhp, recordedDate)
}

/**
 * Requirement 1: price_observations structure.
 * (drug, brand, chain, unit price, observed_on, source, evidence note)
 * Until real data exists, every price stays labeled "Sample price".
 * Show observation date whenever a real price is displayed. Do not invent prices or dates.
 */
@Serializable
data class PriceObservation(
    val drug: String,
    val brand: String? = null,
    val chain: PharmacyChain,
    val unitPrice: Double,
    val observedOn: String, // YYYY-MM-DD
    val source: String,     // e.g. "Physical survey", "DOH-DPRI", "Receipt"
    val evidenceNote: String = "",
    val isSample: Boolean = true
) {
    fun labelTl(): String = if (isSample) "₱%.2f (Halimbawang presyo)".format(unitPrice) else "₱%.2f (Tantya · %s)".format(unitPrice, observedOn)
    fun labelEn(): String = if (isSample) "₱%.2f (Sample price)".format(unitPrice) else "₱%.2f (Estimate · %s)".format(unitPrice, observedOn)
}

@Serializable
data class DrugCatalogEntry(
    val genericName: String,
    val brandNames: List<String>,
    val form: String,
    val standardStrength: String,
    val isSample: Boolean = true,
    val source: String = "Sample Mock Data (Unsurveyed)",
    val genericDatedPrices: Map<PharmacyChain, DatedPrice>,
    val brandedDatedPrices: Map<PharmacyChain, DatedPrice>,
    val defaultMealNoteTl: String,
    val defaultMealNoteEn: String,
    val defaultPatientNoteTl: String,
    val defaultPatientNoteEn: String
)

@Serializable
data class PrescriptionItem(
    val id: String,
    val writtenAs: String,
    val brandName: String? = null,
    val genericName: String,
    val strength: String,
    val form: String,
    val quantity: Int,
    val dosesPerDay: Int,
    val durationDays: Int? = null,
    val slots: List<String> = emptyList(), // "umaga", "tanghali", "gabi"
    val mealNoteTl: String? = null,
    val mealNoteEn: String? = null,
    val noteTl: String? = null,
    val noteEn: String? = null,
    val uncertainFields: List<String> = emptyList(),
    val safetyWarnings: List<SafetyWarning> = emptyList()
)

@Serializable
data class PrescriptionScan(
    val id: String,
    val items: List<PrescriptionItem>,
    val doctorName: String? = null,
    val prcNumber: String? = null,
    val dateOnSlip: String? = null,
    val readScore: Int = 85,
    val medicationLineCount: Int = items.size,
    val scannedAtTimestamp: Long = System.currentTimeMillis()
)

@Serializable
data class PharmacyBranch(
    val id: String,
    val name: String,
    val chain: PharmacyChain,
    val address: String = "Sample Location",
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val phoneNumber: String? = null,
    val openingHours: String? = null,
    val stockClaim: String? = null,
    val isSample: Boolean = true
)
