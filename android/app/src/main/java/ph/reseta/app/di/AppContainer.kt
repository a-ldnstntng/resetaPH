package ph.reseta.app.di

import android.content.Context
import ph.reseta.app.data.db.ResetaDatabase
import ph.reseta.app.data.preferences.DataStoreUserPreferencesRepository
import ph.reseta.app.data.preferences.UserPreferencesRepository
import ph.reseta.app.data.repository.PharmacyRepository
import ph.reseta.app.data.repository.PrescriptionRepository
import ph.reseta.app.data.repository.PricingRepository
import ph.reseta.app.data.repository.RoomPrescriptionRepository
import ph.reseta.app.data.repository.SamplePharmacyRepository
import ph.reseta.app.data.repository.RoomPricingRepository

interface AppContainer {
    val prescriptionRepository: PrescriptionRepository
    val pricingRepository: PricingRepository
    val pharmacyRepository: PharmacyRepository
    val userPreferencesRepository: UserPreferencesRepository
    val apiClient: ph.reseta.app.data.api.ResetaApiClient
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: ResetaDatabase by lazy {
        ResetaDatabase.getInstance(context)
    }

    override val prescriptionRepository: PrescriptionRepository by lazy {
        RoomPrescriptionRepository(database.prescriptionScanDao())
    }

    override val pricingRepository: PricingRepository by lazy {
        ph.reseta.app.data.repository.RoomPricingRepository(database.priceObservationDao())
    }

    override val pharmacyRepository: PharmacyRepository by lazy {
        SamplePharmacyRepository()
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        DataStoreUserPreferencesRepository(context)
    }

    override val apiClient: ph.reseta.app.data.api.ResetaApiClient by lazy {
        ph.reseta.app.data.api.ResetaApiClient(context)
    }
}
