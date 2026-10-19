package ph.reseta.pricing

import ph.reseta.catalog.SamplePhilippineDrugCatalog
import ph.reseta.model.DatedPrice
import ph.reseta.model.PharmacyChain
import ph.reseta.model.PrescriptionItem

/**
 * Pure Kotlin Pricing Engine (Rule 1 & Rule 2).
 * Formulates prescription cost estimates and branded vs generic savings.
 * In this sample mode, all prices are marked as synthetic sample estimates.
 */
data class ItemPriceEstimate(
    val genericName: String,
    val brandName: String?,
    val quantity: Int,
    val genericUnitPrice: DatedPrice,
    val brandedUnitPrice: DatedPrice?,
    val totalGenericPrice: Double,
    val totalBrandedPrice: Double,
    val savingsAmount: Double,
    val savingsPercent: Int,
    val isSample: Boolean = true
)

data class PharmacyBasketTotal(
    val chain: PharmacyChain,
    val totalCostPhp: Double,
    val isLowestPrice: Boolean = false,
    val datedPriceReference: String = "Sample Data",
    val isSample: Boolean = true
)

data class BasketSavingsSummary(
    val totalGenericEstimatedPhp: Double,
    val totalBrandedEstimatedPhp: Double,
    val totalSavingsPhp: Double,
    val averageSavingsPercent: Int,
    val basketPerChain: List<PharmacyBasketTotal>,
    val priceDateReference: String = "Sample Data",
    val isSample: Boolean = true
)

object PricingEngine {

    fun estimateItemPrice(
        item: PrescriptionItem,
        preferredChain: PharmacyChain = PharmacyChain.TGP
    ): ItemPriceEstimate? {
        val entry = SamplePhilippineDrugCatalog.findEntry(item.genericName, item.strength)
            ?: SamplePhilippineDrugCatalog.findEntry(item.writtenAs, item.strength)
            ?: return null

        val genericPrice = entry.genericDatedPrices[preferredChain]
            ?: entry.genericDatedPrices.values.firstOrNull()
            ?: return null

        val brandedPrice = entry.brandedDatedPrices[PharmacyChain.MERCURY_DRUG]
            ?: entry.brandedDatedPrices.values.firstOrNull()

        val qty = if (item.quantity > 0) item.quantity else 1
        val totalGeneric = genericPrice.amountPhp * qty
        val totalBranded = (brandedPrice?.amountPhp ?: (genericPrice.amountPhp * 2.5)) * qty
        val savings = (totalBranded - totalGeneric).coerceAtLeast(0.0)
        val percent = if (totalBranded > 0) ((savings / totalBranded) * 100).toInt() else 0

        return ItemPriceEstimate(
            genericName = entry.genericName,
            brandName = entry.brandNames.firstOrNull(),
            quantity = qty,
            genericUnitPrice = genericPrice,
            brandedUnitPrice = brandedPrice,
            totalGenericPrice = totalGeneric,
            totalBrandedPrice = totalBranded,
            savingsAmount = savings,
            savingsPercent = percent,
            isSample = true
        )
    }

    fun calculateBasketSavings(items: List<PrescriptionItem>): BasketSavingsSummary {
        val estimates = items.mapNotNull { estimateItemPrice(it) }

        var totalGeneric = 0.0
        var totalBranded = 0.0

        for (est in estimates) {
            totalGeneric += est.totalGenericPrice
            totalBranded += est.totalBrandedPrice
        }

        val totalSavings = (totalBranded - totalGeneric).coerceAtLeast(0.0)
        val savingsPercent = if (totalBranded > 0) ((totalSavings / totalBranded) * 100).toInt() else 0

        val chains = listOf(
            PharmacyChain.TGP,
            PharmacyChain.WATSONS,
            PharmacyChain.MERCURY_DRUG,
            PharmacyChain.SOUTH_STAR
        )

        val basketPerChain = chains.map { chain ->
            var chainSum = 0.0
            for (item in items) {
                val entry = SamplePhilippineDrugCatalog.findEntry(item.genericName, item.strength)
                    ?: SamplePhilippineDrugCatalog.findEntry(item.writtenAs, item.strength)
                val unitPrice = entry?.genericDatedPrices?.get(chain)?.amountPhp
                    ?: entry?.genericDatedPrices?.values?.firstOrNull()?.amountPhp
                    ?: 5.0
                chainSum += unitPrice * (if (item.quantity > 0) item.quantity else 1)
            }
            PharmacyBasketTotal(
                chain = chain,
                totalCostPhp = chainSum,
                datedPriceReference = "Sample Data",
                isSample = true
            )
        }

        val minCost = basketPerChain.minOfOrNull { it.totalCostPhp } ?: 0.0
        val finalizedBasket = basketPerChain.map {
            it.copy(isLowestPrice = it.totalCostPhp == minCost)
        }

        return BasketSavingsSummary(
            totalGenericEstimatedPhp = totalGeneric,
            totalBrandedEstimatedPhp = totalBranded,
            totalSavingsPhp = totalSavings,
            averageSavingsPercent = savingsPercent,
            basketPerChain = finalizedBasket,
            priceDateReference = "Sample Data",
            isSample = true
        )
    }
}
