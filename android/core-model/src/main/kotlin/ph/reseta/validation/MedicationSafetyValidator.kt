package ph.reseta.validation

import ph.reseta.catalog.PhilippineDrugCatalog
import ph.reseta.model.SafetySeverity
import ph.reseta.model.SafetyWarning

/**
 * Pure Kotlin Clinical Dosage and Safety Validator (Rule 1).
 * Enforces Philippine DOH DPRI and FDA safety thresholds.
 */
data class SafetyValidationOutcome(
    val normalizedGeneric: String,
    val canonicalStrength: String,
    val canonicalForm: String,
    val mealNoteTl: String,
    val mealNoteEn: String,
    val patientNoteTl: String,
    val patientNoteEn: String,
    val warnings: List<SafetyWarning>
)

object MedicationSafetyValidator {

    private fun parseStrengthMg(strength: String): Double? {
        val regex = Regex("""(\d+(?:\.\d+)?)\s*mg""", RegexOption.IGNORE_CASE)
        val match = regex.find(strength) ?: return null
        return match.groupValues[1].toDoubleOrNull()
    }

    fun validateItemSafety(
        rawGenericOrBrand: String,
        rawStrength: String,
        rawForm: String,
        dosesPerDay: Int,
        durationDays: Int?
    ): SafetyValidationOutcome {
        val entry = PhilippineDrugCatalog.findEntry(rawGenericOrBrand, rawStrength)
        val warnings = mutableListOf<SafetyWarning>()

        val genericName = entry?.genericName ?: rawGenericOrBrand.trim().ifEmpty { "Hindi Natukoy" }
        val strength = entry?.standardStrength ?: rawStrength.trim().ifEmpty { "500 mg" }
        val form = entry?.form ?: rawForm.trim().ifEmpty { "tablet" }

        val singleDoseMg = parseStrengthMg(strength)
        val safeDosesPerDay = dosesPerDay.coerceAtLeast(1)

        if (entry != null && singleDoseMg != null && entry.maxDailyDoseMg != null) {
            val dailyDoseMg = singleDoseMg * safeDosesPerDay
            if (dailyDoseMg > entry.maxDailyDoseMg) {
                warnings.add(
                    SafetyWarning(
                        code = "DOSAGE_CEILING_EXCEEDED",
                        severity = SafetySeverity.WARNING,
                        messageTl = "BABALA SA DOSAGE: Ang kabuuang %.0f mg bawat araw ay lumalagpas sa karaniwang ligtas na ceiling ng DOH (%.0f mg/araw). Kumpirmahin muna sa doktor."
                            .format(dailyDoseMg, entry.maxDailyDoseMg),
                        messageEn = "DOSAGE WARNING: The total %.0f mg per day exceeds the standard safe DOH limit (%.0f mg/day). Confirm with your prescribing doctor."
                            .format(dailyDoseMg, entry.maxDailyDoseMg)
                    )
                )
            }
        }

        if (entry != null && entry.isAntibiotic) {
            val days = durationDays ?: 7
            if (days < 5) {
                warnings.add(
                    SafetyWarning(
                        code = "ANTIBIOTIC_COURSE_TOO_SHORT",
                        severity = SafetySeverity.CAUTION,
                        messageTl = "PAALALA SA ANTIBIOTIC: Karaniwang 7 hanggang 14 na araw ang buong kurso. Huwag ihinto nang maaga upang maiwasan ang antibiotic resistance."
                            .format(days),
                        messageEn = "ANTIBIOTIC REMINDER: A complete course is usually 7 to 14 days. Do not stop early to prevent antimicrobial resistance."
                    )
                )
            } else if (days > 14) {
                warnings.add(
                    SafetyWarning(
                        code = "ANTIBIOTIC_COURSE_TOO_LONG",
                        severity = SafetySeverity.CAUTION,
                        messageTl = "PAALALA: Mahigit 14 na araw ang antibiotic na ito. Tiyaking may follow-up check-up sa doktor.",
                        messageEn = "REMINDER: This antibiotic course exceeds 14 days. Ensure you have a scheduled follow-up with your doctor."
                    )
                )
            }
        }

        return SafetyValidationOutcome(
            normalizedGeneric = genericName,
            canonicalStrength = strength,
            canonicalForm = form,
            mealNoteTl = entry?.defaultMealNoteTl ?: "Inumin ayon sa bilin ng doktor.",
            mealNoteEn = entry?.defaultMealNoteEn ?: "Take as instructed by your doctor.",
            patientNoteTl = entry?.defaultPatientNoteTl ?: "Sundin ang tamang oras at dami ng pag-inom.",
            patientNoteEn = entry?.defaultPatientNoteEn ?: "Follow the prescribed timing and quantity.",
            warnings = warnings
        )
    }
}
