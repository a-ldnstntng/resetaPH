package ph.reseta.app

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import ph.reseta.app.ui.screens.Screen01Welcome
import ph.reseta.app.ui.screens.Screen02Home
import ph.reseta.app.ui.screens.Screen03Scanning
import ph.reseta.app.ui.screens.Screen04Review
import ph.reseta.app.ui.screens.Screen05Savings
import ph.reseta.app.ui.screens.Screen06Schedule
import ph.reseta.app.ui.screens.Screen07CounterCard
import ph.reseta.app.ui.screens.Screen08Map
import ph.reseta.app.ui.screens.Screen09History
import ph.reseta.app.ui.screens.Screen10Settings
import ph.reseta.app.ui.screens.Screen11PriceExplanation
import ph.reseta.app.ui.screens.Screen12StoreDetail
import ph.reseta.app.ui.screens.ScreenError
import ph.reseta.app.ui.viewmodels.HistoryViewModel
import ph.reseta.app.ui.viewmodels.HomeViewModel
import ph.reseta.app.ui.viewmodels.MapViewModel
import ph.reseta.app.ui.viewmodels.ResetaViewModelFactory
import ph.reseta.app.ui.viewmodels.ReviewViewModel
import ph.reseta.app.ui.viewmodels.SavingsViewModel
import ph.reseta.app.ui.viewmodels.ScanState
import ph.reseta.app.ui.viewmodels.ScanViewModel
import ph.reseta.app.ui.viewmodels.ScheduleViewModel
import ph.reseta.app.ui.viewmodels.SettingsViewModel
import ph.reseta.model.PrescriptionScan
import ph.reseta.sample.SampleData

enum class AppScreen {
    WELCOME,
    HOME,
    SCANNING,
    REVIEW,
    SAVINGS,
    SCHEDULE,
    COUNTER_CARD,
    MAP,
    HISTORY,
    SETTINGS,
    PRICE_EXPLANATION,
    STORE_DETAIL,
    ERROR
}

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val app = context.applicationContext as ResetaApplication
    val factory = remember { ResetaViewModelFactory(app.container) }

    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val reviewViewModel: ReviewViewModel = viewModel(factory = factory)
    val savingsViewModel: SavingsViewModel = viewModel(factory = factory)
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = factory)
    val historyViewModel: HistoryViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val mapViewModel: MapViewModel = viewModel(factory = factory)
    val scanViewModel: ScanViewModel = viewModel(factory = factory)

    val homeUiState by homeViewModel.uiState.collectAsState()
    val reviewUiState by reviewViewModel.uiState.collectAsState()
    val savingsUiState by savingsViewModel.uiState.collectAsState()
    val scheduleUiState by scheduleViewModel.uiState.collectAsState()
    val historyUiState by historyViewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val mapUiState by mapViewModel.uiState.collectAsState()
    val scanState by scanViewModel.scanState.collectAsState()

    var currentScreen by remember(homeUiState.isOnboardingComplete) {
        mutableStateOf(if (homeUiState.isOnboardingComplete) AppScreen.HOME else AppScreen.WELCOME)
    }
    var activeScanId by remember { mutableStateOf<String?>(null) }
    var lastErrorMessage by remember { mutableStateOf("Hindi maikonekta sa server.") }

    // 1. Configure ML Kit Document Scanner: 1 page, JPEG, gallery import (Rule 1)
    val scannerOptions = remember {
        GmsDocumentScannerOptions.Builder()
            .setPageLimit(1)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .build()
    }
    val scanner = remember { GmsDocumentScanning.getClient(scannerOptions) }

    // Scanner result launcher
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val gmsResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val page = gmsResult?.pages?.firstOrNull()
            val uri = page?.imageUri
            if (uri != null) {
                currentScreen = AppScreen.SCANNING
                scanViewModel.scanImageUri(context.contentResolver, uri)
            }
        }
    }

    // Photo picker fallback launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentScreen = AppScreen.SCANNING
            scanViewModel.scanImageUri(context.contentResolver, uri)
        }
    }

    val launchDocumentScanner: () -> Unit = {
        val activity = context as? Activity
        if (activity != null) {
            scanner.getStartScanIntent(activity)
                .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener {
                    // Fallback to visual media picker if Google Play Services document scanner is unavailable
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        } else {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    // React to Scan State Changes (Rule 2: Show plain error screen on failure/rejection)
    LaunchedEffect(scanState) {
        when (val state = scanState) {
            is ScanState.Success -> {
                activeScanId = state.scan.id
                reviewViewModel.setInitialScan(state.scan)
                currentScreen = AppScreen.REVIEW
                scanViewModel.resetState()
            }
            is ScanState.Error -> {
                lastErrorMessage = state.message
                currentScreen = AppScreen.ERROR
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        when (currentScreen) {
            AppScreen.WELCOME -> Screen01Welcome(
                onContinue = {
                    homeViewModel.completeOnboarding()
                    currentScreen = AppScreen.HOME
                },
                onAlreadyHaveAccount = {
                    homeViewModel.completeOnboarding()
                    currentScreen = AppScreen.HOME
                }
            )
            AppScreen.HOME -> Screen02Home(
                recentScans = homeUiState.recentScans,
                onScanClick = { launchDocumentScanner() },
                onUploadClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onSampleClick = { sampleType ->
                    val sample = if (sampleType == "triple") {
                        SampleData.sampleScanResult.copy(
                            id = "scan-triple-${System.currentTimeMillis()}",
                            items = listOf(SampleData.sampleItem3, SampleData.sampleItem2)
                        )
                    } else {
                        SampleData.sampleScanResult.copy(
                            id = "scan-aug-${System.currentTimeMillis()}",
                            items = listOf(SampleData.sampleItem1, SampleData.sampleItem2)
                        )
                    }
                    homeViewModel.loadSampleScan(sample) { savedId ->
                        activeScanId = savedId
                        reviewViewModel.setInitialScan(sample)
                        currentScreen = AppScreen.REVIEW
                    }
                },
                onHistoryItemClick = { scan ->
                    activeScanId = scan.id
                    reviewViewModel.setInitialScan(scan)
                    savingsViewModel.loadScanPricing(scan.id)
                    currentScreen = AppScreen.SAVINGS
                },
                onSettingsClick = { currentScreen = AppScreen.SETTINGS }
            )
            AppScreen.SCANNING -> {
                val statusMsg = when (val state = scanState) {
                    is ScanState.Processing -> state.statusMessage
                    else -> null
                }
                Screen03Scanning(
                    statusMessage = statusMsg,
                    onCancel = {
                        scanViewModel.resetState()
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.REVIEW -> Screen04Review(
                scanResult = reviewUiState.scan ?: SampleData.sampleScanResult,
                onConfirm = {
                    reviewViewModel.saveAndConfirm { savedId ->
                        activeScanId = savedId
                        savingsViewModel.loadScanPricing(savedId)
                        currentScreen = AppScreen.SAVINGS
                    }
                },
                onDeepScan = {
                    // Deep decipher with high thinking mode
                },
                onBack = { currentScreen = AppScreen.HOME }
            )
            AppScreen.SAVINGS -> Screen05Savings(
                scanResult = savingsUiState.scan ?: (reviewUiState.scan ?: SampleData.sampleScanResult),
                onViewScheduleClick = {
                    val currentScan = savingsUiState.scan ?: reviewUiState.scan ?: SampleData.sampleScanResult
                    scheduleViewModel.loadSchedule(currentScan.id)
                    currentScreen = AppScreen.SCHEDULE
                },
                onBack = { currentScreen = AppScreen.REVIEW }
            )
            AppScreen.SCHEDULE -> Screen06Schedule(
                scanResult = scheduleUiState.scan ?: SampleData.sampleScanResult,
                onListenClick = { /* Text-to-speech runtime check in Stage 4 */ },
                onCounterModeClick = { currentScreen = AppScreen.COUNTER_CARD },
                onBack = { currentScreen = AppScreen.SAVINGS }
            )
            AppScreen.COUNTER_CARD -> Screen07CounterCard(
                scanResult = scheduleUiState.scan ?: SampleData.sampleScanResult,
                onClose = { currentScreen = AppScreen.SCHEDULE }
            )
            AppScreen.MAP -> Screen08Map(
                branches = mapUiState.branches,
                onBranchClick = { branch ->
                    mapViewModel.selectBranch(branch)
                    currentScreen = AppScreen.STORE_DETAIL
                },
                onDirectionsClick = { branch ->
                    val uri = android.net.Uri.parse("geo:${branch.latitude},${branch.longitude}?q=${branch.latitude},${branch.longitude}(${android.net.Uri.encode(branch.name)})")
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                    runCatching { context.startActivity(intent) }
                },
                onCopyInquiry = { branch ->
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText(
                        "ResetaPH Inquiry",
                        "Magandang araw po! Nais ko pong itanong kung may available kayong gamot sa ${branch.name}? Maraming salamat po!"
                    )
                    clipboard.setPrimaryClip(clip)
                    android.widget.Toast.makeText(context, "Na-kopyang inquiry text!", android.widget.Toast.LENGTH_SHORT).show()
                },
                onBack = { currentScreen = AppScreen.HOME }
            )
            AppScreen.HISTORY -> Screen09History(
                scans = historyUiState.scans,
                onScanClick = { scan ->
                    activeScanId = scan.id
                    reviewViewModel.setInitialScan(scan)
                    savingsViewModel.loadScanPricing(scan.id)
                    currentScreen = AppScreen.SAVINGS
                },
                onClearHistory = { historyViewModel.clearAllHistory() },
                onBack = { currentScreen = AppScreen.HOME }
            )
            AppScreen.SETTINGS -> Screen10Settings(
                selectedLanguage = settingsUiState.language,
                onLanguageChange = { newLang -> settingsViewModel.setLanguage(newLang) },
                installId = settingsUiState.installId,
                totalScansCount = settingsUiState.totalScansCount,
                onDeleteAllData = { settingsViewModel.deleteAllData() },
                onBack = { currentScreen = AppScreen.HOME }
            )
            AppScreen.PRICE_EXPLANATION -> Screen11PriceExplanation(
                onBack = { currentScreen = AppScreen.SAVINGS }
            )
            AppScreen.STORE_DETAIL -> Screen12StoreDetail(
                branch = mapUiState.selectedBranch ?: SampleData.sampleBranches.first(),
                onCallClick = {
                    // Phone number is null for sample data, no call action
                },
                onDirectionsClick = {
                    val branch = mapUiState.selectedBranch ?: SampleData.sampleBranches.first()
                    val uri = android.net.Uri.parse("geo:${branch.latitude},${branch.longitude}?q=${branch.latitude},${branch.longitude}(${android.net.Uri.encode(branch.name)})")
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                    runCatching { context.startActivity(intent) }
                },
                onCopyInquiryClick = {
                    val branch = mapUiState.selectedBranch ?: SampleData.sampleBranches.first()
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText(
                        "ResetaPH Inquiry",
                        "Magandang araw po! Nais ko pong itanong kung may available kayong gamot sa ${branch.name}? Maraming salamat po!"
                    )
                    clipboard.setPrimaryClip(clip)
                    android.widget.Toast.makeText(context, "Na-kopyang inquiry text!", android.widget.Toast.LENGTH_SHORT).show()
                },
                onBack = { currentScreen = AppScreen.MAP }
            )
            AppScreen.ERROR -> ScreenError(
                errorMessage = lastErrorMessage,
                onRetry = {
                    scanViewModel.retryLastScan(context.contentResolver) {
                        scanViewModel.resetState()
                        launchDocumentScanner()
                    }
                },
                onBackToHome = {
                    scanViewModel.resetState()
                    currentScreen = AppScreen.HOME
                }
            )
        }
    }
}
