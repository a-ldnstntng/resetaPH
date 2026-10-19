package ph.reseta.app.data.repository

import ph.reseta.catalog.SamplePhilippineDrugCatalog
import ph.reseta.model.DrugCatalogEntry
import ph.reseta.model.PrescriptionItem
import ph.reseta.pricing.BasketSavingsSummary
import ph.reseta.pricing.ItemPriceEstimate
import ph.reseta.pricing.PricingEngine

/**
 * Repository interface for prescription pricing (Rule 2).
 * Allows Stage 4 to seamlessly swap this Sample implementation with Backend validation and dated prices.
 */
interface PricingRepository {
    suspend fun getCatalog(): List<DrugCatalogEntry>
    suspend fun calculateSavings(items: List<PrescriptionItem>): BasketSavingsSummary
    suspend fun estimateItemPrice(item: PrescriptionItem): ItemPriceEstimate?
}

class SamplePricingRepository : PricingRepository {

    override suspend fun getCatalog(): List<DrugCatalogEntry> {
        return SamplePhilippineDrugCatalog.entries
    }

    override suspend fun calculateSavings(items: List<PrescriptionItem>): BasketSavingsSummary {
        return PricingEngine.calculateBasketSavings(items)
    }

    override suspend fun estimateItemPrice(item: PrescriptionItem): ItemPriceEstimate? {
        return PricingEngine.estimateItemPrice(item)
    }
}
