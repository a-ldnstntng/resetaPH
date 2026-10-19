package ph.reseta.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ph.reseta.app.data.db.PrescriptionScanDao
import ph.reseta.app.data.db.PrescriptionScanEntity
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan

/**
 * Repository interface for prescription persistence (Rule 5).
 * Stage 4 will reuse this exact interface with the backend.
 */
interface PrescriptionRepository {
    fun getScans(): Flow<List<PrescriptionScan>>
    suspend fun getScanById(id: String): PrescriptionScan?
    suspend fun saveScan(scan: PrescriptionScan)
    suspend fun deleteScan(id: String)
    suspend fun clearAllScans()
}

class RoomPrescriptionRepository(
    private val dao: PrescriptionScanDao
) : PrescriptionRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private fun mapEntityToDomain(entity: PrescriptionScanEntity): PrescriptionScan {
        val items: List<PrescriptionItem> = try {
            json.decodeFromString<List<PrescriptionItem>>(entity.itemsJson)
        } catch (_: Exception) {
            emptyList()
        }

        return PrescriptionScan(
            id = entity.id,
            items = items,
            doctorName = entity.doctorName,
            prcNumber = entity.prcNumber,
            dateOnSlip = entity.dateOnSlip,
            readScore = entity.readScore,
            medicationLineCount = entity.medicationLineCount,
            scannedAtTimestamp = entity.scannedAtTimestamp
        )
    }

    private fun mapDomainToEntity(scan: PrescriptionScan): PrescriptionScanEntity {
        val itemsJson = json.encodeToString(scan.items)
        return PrescriptionScanEntity(
            scan.id,
            scan.doctorName,
            scan.prcNumber,
            scan.dateOnSlip,
            scan.readScore,
            scan.medicationLineCount,
            itemsJson,
            scan.scannedAtTimestamp
        )
    }

    override fun getScans(): Flow<List<PrescriptionScan>> {
        return dao.allScansFlow.map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun getScanById(id: String): PrescriptionScan? = withContext(Dispatchers.IO) {
        val entity = dao.getScanById(id) ?: return@withContext null
        mapEntityToDomain(entity)
    }

    override suspend fun saveScan(scan: PrescriptionScan) = withContext(Dispatchers.IO) {
        val entity = mapDomainToEntity(scan)
        dao.insertScan(entity)
    }

    override suspend fun deleteScan(id: String) = withContext(Dispatchers.IO) {
        dao.deleteScanById(id)
    }

    override suspend fun clearAllScans() = withContext(Dispatchers.IO) {
        dao.deleteAllScans()
    }
}
