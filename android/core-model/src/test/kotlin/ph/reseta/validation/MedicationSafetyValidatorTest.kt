package ph.reseta.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ph.reseta.model.SafetySeverity

class MedicationSafetyValidatorTest {

    @Test
    fun validateItemSafety_whenDrugNameMissing_emitsWarning() {
        val outcome = MedicationSafetyValidator.validateItemSafety(
            rawGenericOrBrand = "",
            rawStrength = "500 mg",
            rawForm = "capsule"
        )
        assertTrue(outcome.warnings.any { it.code == "MISSING_DRUG_NAME" && it.severity == SafetySeverity.WARNING })
    }

    @Test
    fun validateItemSafety_whenQuantityShortage_emitsArithmeticCaution() {
        // 3 doses/day * 7 days = 21 units needed, but quantity is only 14
        val outcome = MedicationSafetyValidator.validateItemSafety(
            rawGenericOrBrand = "Amoxicillin",
            rawStrength = "500 mg",
            rawForm = "capsule",
            quantity = 14,
            dosesPerDay = 3,
            durationDays = 7
        )
        val shortageWarning = outcome.warnings.firstOrNull { it.code == "ARITHMETIC_QUANTITY_SHORTAGE" }
        org.junit.Assert.assertNotNull(shortageWarning)
        assertEquals(SafetySeverity.CAUTION, shortageWarning!!.severity)
        assertTrue(shortageWarning.messageTl.contains("14"))
        assertTrue(shortageWarning.messageTl.contains("21"))
    }

    @Test
    fun validateItemSafety_whenExactArithmeticMatch_hasNoArithmeticWarnings() {
        // 3 doses/day * 7 days = 21 units, quantity = 21
        val outcome = MedicationSafetyValidator.validateItemSafety(
            rawGenericOrBrand = "Amoxicillin",
            rawStrength = "500 mg",
            rawForm = "capsule",
            quantity = 21,
            dosesPerDay = 3,
            durationDays = 7
        )
        assertFalse(outcome.warnings.any { it.code.startsWith("ARITHMETIC_") })
    }

    private fun assertFalse(condition: Boolean) {
        org.junit.Assert.assertFalse(condition)
    }
}
