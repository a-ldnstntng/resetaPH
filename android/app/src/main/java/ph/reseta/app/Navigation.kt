package ph.reseta.app

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import ph.reseta.app.theme.ResetaPrimary
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

enum class AppMockupScreen(val screenNumber: Int, val title: String) {
    WELCOME(1, "1. Welcome"),
    HOME(2, "2. Home"),
    SCANNING(3, "3. Scan Overlay"),
    REVIEW(4, "4. Review"),
    SAVINGS(5, "5. Savings & Presyo"),
    SCHEDULE(6, "6. Schedule"),
    COUNTER_CARD(7, "7. Counter Mode"),
    MAP(8, "8. Map"),
    HISTORY(9, "9. History"),
    SETTINGS(10, "10. Settings"),
    PRICE_EXPLANATION(11, "11. Paliwanag"),
    STORE_DETAIL(12, "12. Store Details"),
    ERROR(13, "13. Error Screen")
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

    var currentScreen by remember { mutableStateOf(AppMockupScreen.HOME) }
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
                currentScreen = AppMockupScreen.SCANNING
                scanViewModel.scanImageUri(context.contentResolver, uri)
            }
        }
    }

    // Photo picker fallback launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentScreen = AppMockupScreen.SCANNING
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
                currentScreen = AppMockupScreen.REVIEW
                scanViewModel.resetState()
            }
            is ScanState.Error -> {
                lastErrorMessage = state.message
                currentScreen = AppMockupScreen.ERROR
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val bottomPadding = if (BuildConfig.DEBUG) 58.dp else 0.dp
        Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)) {
            when (currentScreen) {
                AppMockupScreen.WELCOME -> Screen01Welcome(
                    onContinue = {
                        homeViewModel.completeOnboarding()
                        currentScreen = AppMockupScreen.HOME
                    }
                )
                AppMockupScreen.HOME -> Screen02Home(
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
                            currentScreen = AppMockupScreen.REVIEW
                        }
                    },
                    onHistoryItemClick = { scan ->
                        activeScanId = scan.id
                        reviewViewModel.setInitialScan(scan)
                        savingsViewModel.loadScanPricing(scan.id)
                        currentScreen = AppMockupScreen.SAVINGS
                    },
                    onSettingsClick = { currentScreen = AppMockupScreen.SETTINGS }
                )
                AppMockupScreen.SCANNING -> {
                    val statusMsg = when (val state = scanState) {
                        is ScanState.Processing -> state.statusMessage
                        else -> null
                    }
                    Screen03Scanning(
                        statusMessage = statusMsg,
                        onCancel = {
                            scanViewModel.resetState()
                            currentScreen = AppMockupScreen.HOME
                        }
                    )
                }
                AppMockupScreen.REVIEW -> Screen04Review(
                    scanResult = reviewUiState.scan ?: SampleData.sampleScanResult,
                    onConfirm = {
                        reviewViewModel.saveAndConfirm { savedId ->
                            activeScanId = savedId
                            savingsViewModel.loadScanPricing(savedId)
                            currentScreen = AppMockupScreen.SAVINGS
                        }
                    },
                    onDeepScan = {
                        // Deep decipher with high thinking mode
                    },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.SAVINGS -> Screen05Savings(
                    scanResult = savingsUiState.scan ?: (reviewUiState.scan ?: SampleData.sampleScanResult),
                    onViewScheduleClick = {
                        val currentScan = savingsUiState.scan ?: reviewUiState.scan ?: SampleData.sampleScanResult
                        scheduleViewModel.loadSchedule(currentScan.id)
                        currentScreen = AppMockupScreen.SCHEDULE
                    },
                    onBack = { currentScreen = AppMockupScreen.REVIEW }
                )
                AppMockupScreen.SCHEDULE -> Screen06Schedule(
                    scanResult = scheduleUiState.scan ?: SampleData.sampleScanResult,
                    onListenClick = { /* Text-to-speech runtime check in Stage 4 */ },
                    onCounterModeClick = { currentScreen = AppMockupScreen.COUNTER_CARD },
                    onBack = { currentScreen = AppMockupScreen.SAVINGS }
                )
                AppMockupScreen.COUNTER_CARD -> Screen07CounterCard(
                    scanResult = scheduleUiState.scan ?: SampleData.sampleScanResult,
                    onClose = { currentScreen = AppMockupScreen.SCHEDULE }
                )
                AppMockupScreen.MAP -> Screen08Map(
                    branches = mapUiState.branches,
                    onBranchClick = { branch ->
                        mapViewModel.selectBranch(branch)
                        currentScreen = AppMockupScreen.STORE_DETAIL
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
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.HISTORY -> Screen09History(
                    scans = historyUiState.scans,
                    onScanClick = { scan ->
                        activeScanId = scan.id
                        reviewViewModel.setInitialScan(scan)
                        savingsViewModel.loadScanPricing(scan.id)
                        currentScreen = AppMockupScreen.SAVINGS
                    },
                    onClearHistory = { historyViewModel.clearAllHistory() },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.SETTINGS -> Screen10Settings(
                    selectedLanguage = settingsUiState.language,
                    onLanguageChange = { newLang -> settingsViewModel.setLanguage(newLang) },
                    installId = settingsUiState.installId,
                    totalScansCount = settingsUiState.totalScansCount,
                    onDeleteAllData = { settingsViewModel.deleteAllData() },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.PRICE_EXPLANATION -> Screen11PriceExplanation(
                    onBack = { currentScreen = AppMockupScreen.SAVINGS }
                )
                AppMockupScreen.STORE_DETAIL -> Screen12StoreDetail(
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
                    onBack = { currentScreen = AppMockupScreen.MAP }
                )
                AppMockupScreen.ERROR -> ScreenError(
                    errorMessage = lastErrorMessage,
                    onRetry = {
                        scanViewModel.resetState()
                        launchDocumentScanner()
                    },
                    onBackToHome = {
                        scanViewModel.resetState()
                        currentScreen = AppMockupScreen.HOME
                    }
                )
            }
        }

        // Floating Screen Switcher Drawer Bar: Debug-only for verification on real devices
        if (BuildConfig.DEBUG) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color(0xF2142921),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppMockupScreen.entries.forEach { screen ->
                        val isSelected = currentScreen == screen
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ResetaPrimary else Color(0x33FFFFFF),
                            modifier = Modifier.clickable { currentScreen = screen }
                        ) {
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
