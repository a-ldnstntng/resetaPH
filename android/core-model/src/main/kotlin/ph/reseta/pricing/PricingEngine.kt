package ph.reseta.pricing

import java.util.Locale
import ph.reseta.catalog.SamplePhilippineDrugCatalog
import ph.reseta.model.DatedPrice
import ph.reseta.model.PharmacyChain
import ph.reseta.model.PriceObservation
import ph.reseta.model.PrescriptionItem

/**
 * Pure Kotlin Pricing Engine (Rule 1 & Rule 2).
 * Formulates prescription cost estimates and branded vs generic savings.
 *
 * Rules:
 * - Prices come only from dated price records. Never hardcode or invent a price.
 * - The app labels prices as estimates and shows their recorded date.
 * - Until real data exists, every price stays labeled "Sample price".
 * - Show observation date whenever a real price is displayed.
 */
data class ItemPriceEstimate(
    val genericName: String,
    val brandName: String?,
    val quantity: Int,
    val genericUnitPrice: DatedPrice,
    val brandedUnitPrice: DatedPrice?,
    val totalGenericPrice: Double,
    val totalBrandedPrice: Double?,
    val savingsAmount: Double,
    val savingsPercent: Int,
    val isSample: Boolean = true,
    val recordedDate: String = ""
) {
    fun labelPriceTl(): String = if (isSample) {
        String.format(Locale.US, "₱%.2f (Halimbawang presyo)", totalGenericPrice)
    } else {
        String.format(Locale.US, "₱%.2f (Tantya · %s)", totalGenericPrice, recordedDate)
    }

    fun labelPriceEn(): String = if (isSample) {
        String.format(Locale.US, "₱%.2f (Sample price)", totalGenericPrice)
    } else {
        String.format(Locale.US, "₱%.2f (Estimate · %s)", totalGenericPrice, recordedDate)
    }
}

data class PharmacyBasketTotal(
    val chain: PharmacyChain,
    val totalCostPhp: Double,
    val isLowestPrice: Boolean = false,
    val datedPriceReference: String = "Sample Data",
    val isSample: Boolean = true,
    val itemsAvailableCount: Int = 0,
    val totalItemsCount: Int = 0
) {
    fun labelTl(): String = if (isSample) {
        String.format(Locale.US, "₱%.2f (Halimbawang presyo)", totalCostPhp)
    } else {
        String.format(Locale.US, "₱%.2f (Tantya · %s)", totalCostPhp, datedPriceReference)
    }

    fun labelEn(): String = if (isSample) {
        String.format(Locale.US, "₱%.2f (Sample price)", totalCostPhp)
    } else {
        String.format(Locale.US, "₱%.2f (Estimate · %s)", totalCostPhp, datedPriceReference)
    }
}

data class BasketSavingsSummary(
    val totalGenericEstimatedPhp: Double,
    val totalBrandedEstimatedPhp: Double?,
    val totalSavingsPhp: Double,
    val averageSavingsPercent: Int,
    val basketPerChain: List<PharmacyBasketTotal>,
    val priceDateReference: String = "Sample Data",
    val isSample: Boolean = true
)

object PricingEngine {

    fun estimateItemPrice(
        item: PrescriptionItem,
        preferredChain: PharmacyChain = PharmacyChain.TGP,
        observations: List<PriceObservation> = emptyList()
    ): ItemPriceEstimate? {
        val qty = if (item.quantity > 0) item.quantity else 1

        // Check if real price observations exist for this drug
        val matchingObservations = observations.filter {
            it.drug.equals(item.genericName, ignoreCase = true) ||
            it.drug.equals(item.writtenAs, ignoreCase = true)
        }

        if (matchingObservations.isNotEmpty()) {
            val genObs = matchingObservations.firstOrNull { it.chain == preferredChain && it.brand.isNullOrEmpty() }
                ?: matchingObservations.firstOrNull { it.brand.isNullOrEmpty() }
                ?: matchingObservations.first()

            val brandObs = matchingObservations.firstOrNull { !it.brand.isNullOrEmpty() }

            val genericPrice = DatedPrice(
                amountPhp = genObs.unitPrice,
                recordedDate = genObs.observedOn,
                source = genObs.source,
                isSample = genObs.isSample,
                isEstimate = true,
                evidenceNote = genObs.evidenceNote
            )

            val brandedPrice = brandObs?.let {
                DatedPrice(
                    amountPhp = it.unitPrice,
                    recordedDate = it.observedOn,
                    source = it.source,
                    isSample = it.isSample,
                    isEstimate = true,
                    evidenceNote = it.evidenceNote
                )
            }

            val totalGeneric = genericPrice.amountPhp * qty
            val totalBranded = brandedPrice?.let { it.amountPhp * qty }
            val savings = if (totalBranded != null && totalBranded > totalGeneric) {
                totalBranded - totalGeneric
            } else {
                0.0
            }
            val percent = if (totalBranded != null && totalBranded > 0) {
                ((savings / totalBranded) * 100).toInt()
            } else {
                0
            }

            return ItemPriceEstimate(
                genericName = genObs.drug,
                brandName = brandObs?.brand ?: item.brandName,
                quantity = qty,
                genericUnitPrice = genericPrice,
                brandedUnitPrice = brandedPrice,
                totalGenericPrice = totalGeneric,
                totalBrandedPrice = totalBranded,
                savingsAmount = savings,
                savingsPercent = percent,
                isSample = genObs.isSample,
                recordedDate = genObs.observedOn
            )
        }

        // Fall back to sample catalog with isSample = true
        val entry = SamplePhilippineDrugCatalog.findEntry(item.genericName, item.strength)
            ?: SamplePhilippineDrugCatalog.findEntry(item.writtenAs, item.strength)
            ?: return null

        val genericPrice = entry.genericDatedPrices[preferredChain]
            ?: entry.genericDatedPrices.values.firstOrNull()
            ?: return null

        val brandedPrice = entry.brandedDatedPrices[PharmacyChain.MERCURY_DRUG]
            ?: entry.brandedDatedPrices.values.firstOrNull()

        val totalGeneric = genericPrice.amountPhp * qty
        val totalBranded = brandedPrice?.let { it.amountPhp * qty }
        val savings = if (totalBranded != null && totalBranded > totalGeneric) {
            totalBranded - totalGeneric
        } else {
            0.0
        }
        val percent = if (totalBranded != null && totalBranded > 0) {
            ((savings / totalBranded) * 100).toInt()
        } else {
            0
        }

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
            isSample = true,
            recordedDate = genericPrice.recordedDate
        )
    }

    fun calculateBasketSavings(
        items: List<PrescriptionItem>,
        observations: List<PriceObservation> = emptyList()
    ): BasketSavingsSummary {
        val estimates = items.mapNotNull { estimateItemPrice(it, observations = observations) }

        var totalGeneric = 0.0
        var totalBrandedSum = 0.0
        var hasBranded = false

        for (est in estimates) {
            totalGeneric += est.totalGenericPrice
            if (est.totalBrandedPrice != null) {
                totalBrandedSum += est.totalBrandedPrice
                hasBranded = true
            }
        }

        val totalSavings = if (hasBranded && totalBrandedSum > totalGeneric) {
            totalBrandedSum - totalGeneric
        } else {
            0.0
        }
        val savingsPercent = if (hasBranded && totalBrandedSum > 0) {
            ((totalSavings / totalBrandedSum) * 100).toInt()
        } else {
            0
        }

        val chains = listOf(
            PharmacyChain.TGP,
            PharmacyChain.WATSONS,
            PharmacyChain.MERCURY_DRUG,
            PharmacyChain.SOUTH_STAR
        )

        val basketPerChain = chains.map { chain ->
            var chainSum = 0.0
            var itemsFound = 0
            var latestDate = "Sample Data"
            var isChainSample = true

            for (item in items) {
                val qty = if (item.quantity > 0) item.quantity else 1
                val obs = observations.firstOrNull {
                    (it.drug.equals(item.genericName, ignoreCase = true) ||
                     it.drug.equals(item.writtenAs, ignoreCase = true)) &&
                    it.chain == chain &&
                    it.brand.isNullOrEmpty()
                }

                if (obs != null) {
                    chainSum += obs.unitPrice * qty
                    itemsFound++
                    latestDate = obs.observedOn
                    isChainSample = obs.isSample
                } else {
                    val entry = SamplePhilippineDrugCatalog.findEntry(item.genericName, item.strength)
                        ?: SamplePhilippineDrugCatalog.findEntry(item.writtenAs, item.strength)
                    val samplePrice = entry?.genericDatedPrices?.get(chain)
                        ?: entry?.genericDatedPrices?.values?.firstOrNull()

                    if (samplePrice != null) {
                        chainSum += samplePrice.amountPhp * qty
                        itemsFound++
                        latestDate = samplePrice.recordedDate.ifEmpty { "Sample Data" }
                    }
                }
            }

            PharmacyBasketTotal(
                chain = chain,
                totalCostPhp = chainSum,
                datedPriceReference = latestDate,
                isSample = isChainSample,
                itemsAvailableCount = itemsFound,
                totalItemsCount = items.size
            )
        }

        // Rule & Fix: To be labeled 'Lowest Price', a pharmacy must have all items available.
        // Otherwise a chain pricing only 1 of 3 drugs appears artificially cheaper than complete baskets.
        val completeBaskets = basketPerChain.filter { it.totalCostPhp > 0 && it.itemsAvailableCount == it.totalItemsCount }
        val eligibleBaskets = if (completeBaskets.isNotEmpty()) completeBaskets else basketPerChain.filter { it.totalCostPhp > 0 }
        val minCost = eligibleBaskets.minOfOrNull { it.totalCostPhp } ?: 0.0

        val finalizedBasket = basketPerChain.map {
            val isComplete = it.itemsAvailableCount == it.totalItemsCount
            val isEligible = if (completeBaskets.isNotEmpty()) isComplete else true
            it.copy(isLowestPrice = isEligible && it.totalCostPhp > 0 && it.totalCostPhp == minCost)
        }

        val allSample = estimates.all { it.isSample }
        val dateRef = estimates.firstOrNull { !it.isSample }?.recordedDate ?: "Sample Data"

        return BasketSavingsSummary(
            totalGenericEstimatedPhp = totalGeneric,
            totalBrandedEstimatedPhp = if (hasBranded) totalBrandedSum else null,
            totalSavingsPhp = totalSavings,
            averageSavingsPercent = savingsPercent,
            basketPerChain = finalizedBasket,
            priceDateReference = dateRef,
            isSample = allSample
        )
    }
}
