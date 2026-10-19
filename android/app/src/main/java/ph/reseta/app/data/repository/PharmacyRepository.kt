package ph.reseta.app.data.repository

import ph.reseta.model.PharmacyBranch
import ph.reseta.sample.SampleData

/**
 * Repository interface for pharmacy branch discovery and store details.
 * Stage 4 will connect this to Google Maps Platform / backend branch database.
 */
interface PharmacyRepository {
    suspend fun getNearbyBranches(userLat: Double = 14.5685, userLng: Double = 120.9934): List<PharmacyBranch>
    suspend fun getBranchById(id: String): PharmacyBranch?
}

class SamplePharmacyRepository : PharmacyRepository {

    override suspend fun getNearbyBranches(userLat: Double, userLng: Double): List<PharmacyBranch> {
        return SampleData.sampleBranches
    }

    override suspend fun getBranchById(id: String): PharmacyBranch? {
        return SampleData.sampleBranches.firstOrNull { it.id == id }
    }
}
