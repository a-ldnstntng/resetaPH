package ph.reseta.pricing

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ph.reseta.model.PharmacyChain
import ph.reseta.model.PriceObservation
import ph.reseta.model.PrescriptionItem

class PricingEngineTest {

    @Test
    fun lowestPrice_requiresCompleteBasketWhenCompleteBasketsExist() {
        // Use custom drug names not in the sample mock catalog so fallback doesn't auto-fill
        val item1 = PrescriptionItem(
            id = "1",
            writtenAs = "CustomMedsAlpha 500mg #20",
            genericName = "CustomMedsAlpha",
            strength = "500 mg",
            form = "capsule",
            quantity = 20,
            dosesPerDay = 3
        )
        val item2 = PrescriptionItem(
            id = "2",
            writtenAs = "CustomMedsBeta 10mg #10",
            genericName = "CustomMedsBeta",
            strength = "10 mg",
            form = "tablet",
            quantity = 10,
            dosesPerDay = 1
        )

        val observations = listOf(
            // TGP has observations for BOTH items: 4.00 * 20 = 80, 2.00 * 10 = 20. Total = 100.00
            PriceObservation("CustomMedsAlpha", null, PharmacyChain.TGP, 4.00, "2026-10-15", "Survey", "", false),
            PriceObservation("CustomMedsBeta", null, PharmacyChain.TGP, 2.00, "2026-10-15", "Survey", "", false),
            // Watsons has observation for ONLY 1 item: 1.00 * 20 = 20. Total = 20.00 (incomplete basket!)
            PriceObservation("CustomMedsAlpha", null, PharmacyChain.WATSONS, 1.00, "2026-10-15", "Survey", "", false)
        )

        val summary = PricingEngine.calculateBasketSavings(listOf(item1, item2), observations)

        val tgpBasket = summary.basketPerChain.first { it.chain == PharmacyChain.TGP }
        val watsonsBasket = summary.basketPerChain.first { it.chain == PharmacyChain.WATSONS }

        // TGP has both items
        assertEquals(2, tgpBasket.itemsAvailableCount)
        assertEquals(2, tgpBasket.totalItemsCount)
        assertTrue("Complete basket at TGP should be marked as lowest price", tgpBasket.isLowestPrice)

        // Watsons has ₱20 vs TGP's ₱100, but only 1 of 2 items available (incomplete basket)
        assertEquals(1, watsonsBasket.itemsAvailableCount)
        assertEquals(2, watsonsBasket.totalItemsCount)
        assertFalse("Incomplete basket must NOT be marked lowest price when complete basket exists", watsonsBasket.isLowestPrice)
    }

    @Test
    fun labelPrice_inEuropeanLocale_formatsWithPeriodDecimalSeparator() {
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.GERMANY) // Comma decimal locale

            val item = PrescriptionItem(
                id = "1",
                writtenAs = "Amoxicillin 500mg #20",
                genericName = "Amoxicillin",
                strength = "500 mg",
                form = "capsule",
                quantity = 20,
                dosesPerDay = 3
            )

            val observations = listOf(
                PriceObservation("Amoxicillin", null, PharmacyChain.TGP, 4.50, "2026-10-15", "Survey", "", false)
            )

            val estimate = PricingEngine.estimateItemPrice(item, PharmacyChain.TGP, observations)
            assertNotNull(estimate)
            val labelTl = estimate!!.labelPriceTl()
            val labelEn = estimate.labelPriceEn()

            assertTrue("Expected ₱90.00 with period, got: $labelTl", labelTl.contains("₱90.00"))
            assertFalse("Should not contain comma decimal: $labelTl", labelTl.contains("₱90,00"))
            assertTrue("Expected ₱90.00 with period in English, got: $labelEn", labelEn.contains("₱90.00"))
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun calculateBasketSavings_withEmptyItems_returnsZeroTotalsSafely() {
        val summary = PricingEngine.calculateBasketSavings(emptyList())
        assertEquals(0.0, summary.totalGenericEstimatedPhp, 0.001)
        assertEquals(0.0, summary.totalSavingsPhp, 0.001)
        assertEquals(0, summary.averageSavingsPercent)
        assertTrue(summary.isSample)
    }
}
