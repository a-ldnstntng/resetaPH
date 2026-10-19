package ph.reseta.pricing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ph.reseta.model.PharmacyChain
import ph.reseta.model.PrescriptionItem

class PriceObservationCsvParserTest {

    @Test
    fun parse_validCsv_returnsPriceObservations() {
        val csv = """
            drug,brand,chain,unit_price,observed_on,source,evidence_note,is_sample
            Amoxicillin,Amoxil,Mercury,8.50,2026-10-15,Physical Survey,Receipt #12345,false
            Paracetamol,,TGP,2.00,2026-10-10,DOH-DPRI,Bulletin 2026-08,false
        """.trimIndent()

        val observations = PriceObservationCsvParser.parse(csv)
        assertEquals(2, observations.size)

        val obs1 = observations[0]
        assertEquals("Amoxicillin", obs1.drug)
        assertEquals("Amoxil", obs1.brand)
        assertEquals(PharmacyChain.MERCURY_DRUG, obs1.chain)
        assertEquals(8.50, obs1.unitPrice, 0.001)
        assertEquals("2026-10-15", obs1.observedOn)
        assertEquals("Physical Survey", obs1.source)
        assertEquals("Receipt #12345", obs1.evidenceNote)
        assertFalse(obs1.isSample)

        val obs2 = observations[1]
        assertEquals("Paracetamol", obs2.drug)
        assertEquals(null, obs2.brand)
        assertEquals(PharmacyChain.TGP, obs2.chain)
        assertEquals(2.00, obs2.unitPrice, 0.001)
        assertEquals("2026-10-10", obs2.observedOn)
        assertFalse(obs2.isSample)
    }

    @Test
    fun pricingEngine_withObservations_usesObservedPricesAndShowsObservationDate() {
        val csv = """
            drug,brand,chain,unit_price,observed_on,source,evidence_note,is_sample
            Amoxicillin,,TGP,4.25,2026-10-12,Actual Receipt,Store receipt,false
            Amoxicillin,Amoxil,Mercury,9.00,2026-10-12,Actual Receipt,Store receipt,false
        """.trimIndent()

        val observations = PriceObservationCsvParser.parse(csv)
        val item = PrescriptionItem(
            id = "test-1",
            writtenAs = "Amoxicillin 500mg #20",
            genericName = "Amoxicillin",
            strength = "500 mg",
            form = "capsule",
            quantity = 20,
            dosesPerDay = 3
        )

        val estimate = PricingEngine.estimateItemPrice(item, preferredChain = PharmacyChain.TGP, observations = observations)
        org.junit.Assert.assertNotNull(estimate)
        assertEquals(85.0, estimate!!.totalGenericPrice, 0.001)
        assertEquals(180.0, estimate.totalBrandedPrice!!, 0.001)
        assertEquals(95.0, estimate.savingsAmount, 0.001)
        assertFalse(estimate.isSample)
        assertEquals("2026-10-12", estimate.recordedDate)
        assertEquals("₱85.00 (Tantya · 2026-10-12)", estimate.labelPriceTl())
    }
}
