package ph.reseta.validation

import ph.reseta.catalog.SamplePhilippineDrugCatalog
import ph.reseta.model.SafetySeverity
import ph.reseta.model.SafetyWarning

/**
 * Pure Kotlin Prescription Arithmetic & Consistency Validator (Rule 1).
 *
 * NOTE: This validator strictly performs arithmetic consistency and completeness checks
 * (e.g. quantity vs doses/day × days, missing strength/frequency).
 * It NEVER performs clinical pharmacology assessments, daily mg limit approvals,
 * or implies that any dose is safe.
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

    fun validateItemSafety(
        rawGenericOrBrand: String,
        rawStrength: String,
        rawForm: String,
        quantity: Int = 0,
        dosesPerDay: Int = 0,
        durationDays: Int? = null
    ): SafetyValidationOutcome {
        val entry = SamplePhilippineDrugCatalog.findEntry(rawGenericOrBrand, rawStrength)
        val warnings = mutableListOf<SafetyWarning>()

        val genericName = entry?.genericName ?: rawGenericOrBrand.trim().ifEmpty { "Hindi Natukoy" }
        val strength = entry?.standardStrength ?: rawStrength.trim().ifEmpty { "" }
        val form = entry?.form ?: rawForm.trim().ifEmpty { "tablet" }

        // 1. Missing drug name check
        if (rawGenericOrBrand.isBlank() || genericName == "Hindi Natukoy") {
            warnings.add(
                SafetyWarning(
                    code = "MISSING_DRUG_NAME",
                    severity = SafetySeverity.WARNING,
                    messageTl = "Pakisigurado: Hindi malinaw o kulang ang pangalan ng gamot sa reseta. Kumonsulta sa doktor o botika.",
                    messageEn = "Pakisigurado: Drug name is missing or illegible on the prescription. Please verify with doctor or pharmacist."
                )
            )
        }

        // 2. Missing dosage strength check
        if (strength.isBlank()) {
            warnings.add(
                SafetyWarning(
                    code = "MISSING_STRENGTH",
                    severity = SafetySeverity.CAUTION,
                    messageTl = "Pakisigurado: Walang nakasaad na dosage strength (hal. 500mg). Linawin sa parmasyutiko bago bumili.",
                    messageEn = "Pakisigurado: Dosage strength (e.g. 500mg) is missing. Clarify with the pharmacist before purchasing."
                )
            )
        }

        // 3. Missing or zero frequency check
        if (dosesPerDay <= 0) {
            warnings.add(
                SafetyWarning(
                    code = "MISSING_FREQUENCY",
                    severity = SafetySeverity.CAUTION,
                    messageTl = "Pakisigurado: Hindi malinaw kung ilang beses iinumin sa isang araw. Kumpirmahin ang tamang agwat sa doktor.",
                    messageEn = "Pakisigurado: Frequency of doses per day is missing or unclear. Confirm proper interval with your doctor."
                )
            )
        }

        // 4. Missing or zero quantity check
        if (quantity <= 0) {
            warnings.add(
                SafetyWarning(
                    code = "MISSING_QUANTITY",
                    severity = SafetySeverity.CAUTION,
                    messageTl = "Pakisigurado: Walang nakasaad na kabuuang bilang ng bibilhing gamot (# o quantity).",
                    messageEn = "Pakisigurado: Total quantity to purchase (#) is not specified on the prescription."
                )
            )
        }

        // 5. Arithmetic consistency check (quantity vs dosesPerDay × durationDays)
        if (durationDays != null && durationDays > 0 && dosesPerDay > 0 && quantity > 0) {
            val expectedQuantity = dosesPerDay * durationDays
            if (quantity < expectedQuantity) {
                warnings.add(
                    SafetyWarning(
                        code = "ARITHMETIC_QUANTITY_SHORTAGE",
                        severity = SafetySeverity.CAUTION,
                        messageTl = "Pakisigurado: Nakasaad ang $quantity piraso sa reseta, ngunit kailangan ng $expectedQuantity piraso para sa $durationDays araw ($dosesPerDay bawat araw).",
                        messageEn = "Pakisigurado: Prescription indicates $quantity units, but $expectedQuantity units are required for $durationDays days ($dosesPerDay daily)."
                    )
                )
            } else if (quantity > expectedQuantity) {
                warnings.add(
                    SafetyWarning(
                        code = "ARITHMETIC_QUANTITY_EXCESS",
                        severity = SafetySeverity.INFO,
                        messageTl = "Pakisigurado: May $quantity piraso sa reseta, higit sa kailangang $expectedQuantity piraso para sa $durationDays araw ($dosesPerDay bawat araw).",
                        messageEn = "Pakisigurado: Prescription lists $quantity units, which exceeds the calculated $expectedQuantity units for $durationDays days ($dosesPerDay daily)."
                    )
                )
            }
        }

        // NO clinical dose ceiling checks, NO antibiotic duration enforcement, NO "safe" assertions.
        return SafetyValidationOutcome(
            normalizedGeneric = genericName,
            canonicalStrength = strength.ifEmpty { "Standard" },
            canonicalForm = form,
            mealNoteTl = entry?.defaultMealNoteTl ?: "Inumin ayon sa bilin ng doktor.",
            mealNoteEn = entry?.defaultMealNoteEn ?: "Take as instructed by your doctor.",
            patientNoteTl = entry?.defaultPatientNoteTl ?: "Sundin ang nakasulat na tagubilin ng lisensyadong doktor.",
            patientNoteEn = entry?.defaultPatientNoteEn ?: "Follow the written directions of your licensed physician.",
            warnings = warnings
        )
    }
}
