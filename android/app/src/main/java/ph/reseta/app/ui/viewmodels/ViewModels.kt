package ph.reseta.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.ContentResolver
import android.net.Uri
import ph.reseta.app.data.api.ResetaApiClient
import ph.reseta.app.data.preferences.AppLanguage
import ph.reseta.app.data.preferences.UserPreferencesRepository
import ph.reseta.app.data.repository.PharmacyRepository
import ph.reseta.app.data.repository.PrescriptionRepository
import ph.reseta.app.data.repository.PricingRepository
import ph.reseta.app.data.scanner.ImageProcessingUtils
import ph.reseta.app.di.AppContainer
import ph.reseta.model.PharmacyBranch
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan
import ph.reseta.pricing.BasketSavingsSummary
import ph.reseta.sample.SampleData
import ph.reseta.validation.MedicationSafetyValidator

// ==========================================
// 1. HOME VIEWMODEL
// ==========================================
data class HomeUiState(
    val recentScans: List<PrescriptionScan> = emptyList(),
    val isOnboardingComplete: Boolean = true,
    val language: AppLanguage = AppLanguage.TAGLISH,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        prescriptionRepository.getScans(),
        preferencesRepository.isOnboardingComplete,
        preferencesRepository.appLanguage
    ) { scans, onboarding, lang ->
        HomeUiState(
            recentScans = scans,
            isOnboardingComplete = onboarding,
            language = lang,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun loadSampleScan(sampleScan: PrescriptionScan, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            prescriptionRepository.saveScan(sampleScan)
            onComplete(sampleScan.id)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingComplete(true)
        }
    }
}

// ==========================================
// 2. REVIEW VIEWMODEL
// ==========================================
data class ReviewUiState(
    val scan: PrescriptionScan? = null,
    val isSaving: Boolean = false
)

class ReviewViewModel(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun setInitialScan(scan: PrescriptionScan) {
        _uiState.value = ReviewUiState(scan = scan)
    }

    fun loadScan(scanId: String) {
        viewModelScope.launch {
            val scan = prescriptionRepository.getScanById(scanId)
            _uiState.value = ReviewUiState(scan = scan ?: SampleData.sampleScanResult)
        }
    }

    fun updateItem(index: Int, updatedItem: PrescriptionItem) {
        val currentScan = _uiState.value.scan ?: return
        if (index !in currentScan.items.indices) return

        // Recompute clerical arithmetic safety checks
        val outcome = MedicationSafetyValidator.validateItemSafety(
            rawGenericOrBrand = updatedItem.genericName.ifBlank { updatedItem.writtenAs },
            rawStrength = updatedItem.strength,
            rawForm = updatedItem.form,
            quantity = updatedItem.quantity,
            dosesPerDay = updatedItem.dosesPerDay,
            durationDays = updatedItem.durationDays
        )

        val validatedItem = updatedItem.copy(
            genericName = outcome.normalizedGeneric,
            strength = outcome.canonicalStrength,
            form = outcome.canonicalForm,
            mealNoteTl = outcome.mealNoteTl,
            mealNoteEn = outcome.mealNoteEn,
            safetyWarnings = outcome.warnings
        )

        val newItems = currentScan.items.toMutableList()
        newItems[index] = validatedItem
        _uiState.value = _uiState.value.copy(
            scan = currentScan.copy(items = newItems)
        )
    }

    fun saveAndConfirm(onComplete: (String) -> Unit) {
        val scan = _uiState.value.scan ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            prescriptionRepository.saveScan(scan)
            _uiState.value = _uiState.value.copy(isSaving = false)
            onComplete(scan.id)
        }
    }
}

// ==========================================
// 3. SAVINGS VIEWMODEL
// ==========================================
data class SavingsUiState(
    val scan: PrescriptionScan? = null,
    val basketSavings: BasketSavingsSummary? = null,
    val isLoading: Boolean = false
)

class SavingsViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val pricingRepository: PricingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsUiState(isLoading = true))
    val uiState: StateFlow<SavingsUiState> = _uiState.asStateFlow()

    fun loadScanPricing(scanId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val scan = prescriptionRepository.getScanById(scanId) ?: SampleData.sampleScanResult
            val savings = pricingRepository.calculateSavings(scan.items)
            _uiState.value = SavingsUiState(
                scan = scan,
                basketSavings = savings,
                isLoading = false
            )
        }
    }
}

// ==========================================
// 4. SCHEDULE VIEWMODEL
// ==========================================
data class ScheduleUiState(
    val scan: PrescriptionScan? = null,
    val morningItems: List<PrescriptionItem> = emptyList(),
    val noonItems: List<PrescriptionItem> = emptyList(),
    val nightItems: List<PrescriptionItem> = emptyList()
)

class ScheduleViewModel(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun loadSchedule(scanId: String) {
        viewModelScope.launch {
            val scan = prescriptionRepository.getScanById(scanId) ?: SampleData.sampleScanResult
            val morning = scan.items.filter { it.slots.contains("umaga") }
            val noon = scan.items.filter { it.slots.contains("tanghali") }
            val night = scan.items.filter { it.slots.contains("gabi") }
            _uiState.value = ScheduleUiState(
                scan = scan,
                morningItems = morning,
                noonItems = noon,
                nightItems = night
            )
        }
    }
}

// ==========================================
// 5. HISTORY VIEWMODEL
// ==========================================
data class HistoryUiState(
    val scans: List<PrescriptionScan> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = prescriptionRepository.getScans().combine(
        MutableStateFlow(false)
    ) { scans, loading ->
        HistoryUiState(scans = scans, isLoading = loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun deleteScan(id: String) {
        viewModelScope.launch {
            prescriptionRepository.deleteScan(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            prescriptionRepository.clearAllScans()
        }
    }
}

// ==========================================
// 6. SETTINGS VIEWMODEL
// ==========================================
data class SettingsUiState(
    val language: AppLanguage = AppLanguage.TAGLISH,
    val installId: String = "",
    val totalScansCount: Int = 0
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val prescriptionRepository: PrescriptionRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.appLanguage,
        preferencesRepository.installId,
        prescriptionRepository.getScans()
    ) { lang, id, scans ->
        SettingsUiState(
            language = lang,
            installId = id,
            totalScansCount = scans.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            preferencesRepository.getOrCreateInstallId()
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(language)
        }
    }

    fun deleteAllData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            prescriptionRepository.clearAllScans()
            onComplete()
        }
    }
}

// ==========================================
// 7. MAP VIEWMODEL
// ==========================================
data class MapUiState(
    val branches: List<PharmacyBranch> = emptyList(),
    val selectedBranch: PharmacyBranch? = null,
    val isLoading: Boolean = false
)

class MapViewModel(
    private val pharmacyRepository: PharmacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadBranches()
    }

    fun loadBranches() {
        viewModelScope.launch {
            val branches = pharmacyRepository.getNearbyBranches()
            _uiState.value = MapUiState(
                branches = branches,
                selectedBranch = branches.firstOrNull(),
                isLoading = false
            )
        }
    }

    fun selectBranch(branch: PharmacyBranch) {
        _uiState.value = _uiState.value.copy(selectedBranch = branch)
    }

    fun selectBranchById(branchId: String) {
        viewModelScope.launch {
            val branch = pharmacyRepository.getBranchById(branchId)
            _uiState.value = _uiState.value.copy(selectedBranch = branch)
        }
    }
}

// ==========================================
// 8. SCAN VIEWMODEL
// ==========================================
sealed interface ScanState {
    object Idle : ScanState
    data class Processing(val statusMessage: String = "Binabasa ang reseta gamit ang AI...") : ScanState
    data class Success(val scan: PrescriptionScan) : ScanState
    data class Error(val message: String) : ScanState
}

class ScanViewModel(
    private val apiClient: ResetaApiClient,
    private val prescriptionRepository: PrescriptionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun resetState() {
        _scanState.value = ScanState.Idle
    }

    fun scanImageUri(contentResolver: ContentResolver, uri: Uri, deepScan: Boolean = false) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing("Inihahanda ang malinaw na litrato...")
            try {
                // Rule 1: Send at full quality without downscaling below 1600px on long side
                val imageBytes = ImageProcessingUtils.processImageUriToBytes(contentResolver, uri)

                _scanState.value = ScanState.Processing("Sinusuri ang mga sulat-kamay at gamot gamit ang AI...")
                val installId = preferencesRepository.getOrCreateInstallId()

                val result = apiClient.scanPrescription(
                    imageBytes = imageBytes,
                    mimeType = "image/jpeg",
                    deepScan = deepScan,
                    installId = installId
                )

                result.fold(
                    onSuccess = { scan ->
                        // Rule 5: Store only structured results in Room
                        prescriptionRepository.saveScan(scan)
                        _scanState.value = ScanState.Success(scan)
                    },
                    onFailure = { error ->
                        _scanState.value = ScanState.Error(error.message ?: "Failed to process prescription")
                    }
                )
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Hindi mabuksan ang litrato ng reseta.")
            }
        }
    }
}

// ==========================================
// VIEWMODEL FACTORY PROVIDER
// ==========================================
class ResetaViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    container.prescriptionRepository,
                    container.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(ReviewViewModel::class.java) -> {
                ReviewViewModel(container.prescriptionRepository) as T
            }
            modelClass.isAssignableFrom(SavingsViewModel::class.java) -> {
                SavingsViewModel(
                    container.prescriptionRepository,
                    container.pricingRepository
                ) as T
            }
            modelClass.isAssignableFrom(ScheduleViewModel::class.java) -> {
                ScheduleViewModel(container.prescriptionRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(container.prescriptionRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    container.userPreferencesRepository,
                    container.prescriptionRepository
                ) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(container.pharmacyRepository) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(
                    container.apiClient,
                    container.prescriptionRepository,
                    container.userPreferencesRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
