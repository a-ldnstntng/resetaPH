package ph.reseta.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ph.reseta.app.data.db.PriceObservationDao
import ph.reseta.app.data.db.PriceObservationEntity
import ph.reseta.catalog.SamplePhilippineDrugCatalog
import ph.reseta.model.DrugCatalogEntry
import ph.reseta.model.PharmacyChain
import ph.reseta.model.PriceObservation
import ph.reseta.model.PrescriptionItem
import ph.reseta.pricing.BasketSavingsSummary
import ph.reseta.pricing.ItemPriceEstimate
import ph.reseta.pricing.PriceObservationCsvParser
import ph.reseta.pricing.PricingEngine

/**
 * Repository interface for prescription pricing (Rule 2 / Stage 4).
 * Handles price_observations, CSV import, and dated price calculation.
 */
interface PricingRepository {
    suspend fun getCatalog(): List<DrugCatalogEntry>
    suspend fun calculateSavings(items: List<PrescriptionItem>): BasketSavingsSummary
    suspend fun estimateItemPrice(item: PrescriptionItem): ItemPriceEstimate?
    suspend fun importFromCsv(csvContent: String): Int
    suspend fun getAllObservations(): List<PriceObservation>
    suspend fun getObservationsForDrug(drugName: String): List<PriceObservation>
}

class RoomPricingRepository(
    private val dao: PriceObservationDao
) : PricingRepository {

    override suspend fun getCatalog(): List<DrugCatalogEntry> = withContext(Dispatchers.IO) {
        SamplePhilippineDrugCatalog.entries
    }

    override suspend fun calculateSavings(items: List<PrescriptionItem>): BasketSavingsSummary = withContext(Dispatchers.IO) {
        val allObs = dao.allObservations.map { it.toDomain() }
        PricingEngine.calculateBasketSavings(items, observations = allObs)
    }

    override suspend fun estimateItemPrice(item: PrescriptionItem): ItemPriceEstimate? = withContext(Dispatchers.IO) {
        val drugObs = dao.getObservationsForDrug(item.genericName).map { it.toDomain() }
        PricingEngine.estimateItemPrice(item, observations = drugObs)
    }

    override suspend fun importFromCsv(csvContent: String): Int = withContext(Dispatchers.IO) {
        val parsed = PriceObservationCsvParser.parse(csvContent)
        val entities = parsed.map { it.toEntity() }
        dao.insertAll(entities)
        entities.size
    }

    override suspend fun getAllObservations(): List<PriceObservation> = withContext(Dispatchers.IO) {
        dao.allObservations.map { it.toDomain() }
    }

    override suspend fun getObservationsForDrug(drugName: String): List<PriceObservation> = withContext(Dispatchers.IO) {
        dao.getObservationsForDrug(drugName).map { it.toDomain() }
    }

    private fun PriceObservationEntity.toDomain(): PriceObservation {
        val chainEnum = runCatching { PharmacyChain.valueOf(chain) }.getOrDefault(PharmacyChain.TGP)
        return PriceObservation(
            drug = drug,
            brand = brand,
            chain = chainEnum,
            unitPrice = unitPrice,
            observedOn = observedOn,
            source = source,
            evidenceNote = evidenceNote,
            isSample = isSample
        )
    }

    private fun PriceObservation.toEntity(): PriceObservationEntity {
        return PriceObservationEntity(
            0,
            drug,
            brand,
            chain.name,
            unitPrice,
            observedOn,
            source,
            evidenceNote,
            isSample
        )
    }
}
