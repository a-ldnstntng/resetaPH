package ph.reseta.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.reseta.app.theme.ResetaInk
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
    STORE_DETAIL(12, "12. Store Details")
}

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf(AppMockupScreen.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        // Active Screen Content
        val bottomPadding = if (BuildConfig.DEBUG) 58.dp else 0.dp
        Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)) {
            when (currentScreen) {
                AppMockupScreen.WELCOME -> Screen01Welcome(
                    onContinue = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.HOME -> Screen02Home(
                    onScanClick = { currentScreen = AppMockupScreen.SCANNING },
                    onUploadClick = { currentScreen = AppMockupScreen.REVIEW },
                    onSampleClick = { currentScreen = AppMockupScreen.REVIEW },
                    onHistoryItemClick = { currentScreen = AppMockupScreen.SAVINGS },
                    onSettingsClick = { currentScreen = AppMockupScreen.SETTINGS }
                )
                AppMockupScreen.SCANNING -> Screen03Scanning(
                    onCancel = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.REVIEW -> Screen04Review(
                    onConfirm = { currentScreen = AppMockupScreen.SAVINGS },
                    onDeepScan = { /* Handled in Stage 3 */ },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.SAVINGS -> Screen05Savings(
                    onViewScheduleClick = { currentScreen = AppMockupScreen.SCHEDULE },
                    onBack = { currentScreen = AppMockupScreen.REVIEW }
                )
                AppMockupScreen.SCHEDULE -> Screen06Schedule(
                    onListenClick = { /* Handled in Stage 4 */ },
                    onCounterModeClick = { currentScreen = AppMockupScreen.COUNTER_CARD },
                    onBack = { currentScreen = AppMockupScreen.SAVINGS }
                )
                AppMockupScreen.COUNTER_CARD -> Screen07CounterCard(
                    onClose = { currentScreen = AppMockupScreen.SCHEDULE }
                )
                AppMockupScreen.MAP -> Screen08Map(
                    onBranchClick = { currentScreen = AppMockupScreen.STORE_DETAIL },
                    onDirectionsClick = { /* Maps intent in Stage 4 */ },
                    onCopyInquiry = { /* Copy to clipboard */ },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.HISTORY -> Screen09History(
                    onScanClick = { currentScreen = AppMockupScreen.SAVINGS },
                    onClearHistory = { /* Clear in Stage 2 */ },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.SETTINGS -> Screen10Settings(
                    onDeleteAllData = { /* Handled in Stage 2 */ },
                    onBack = { currentScreen = AppMockupScreen.HOME }
                )
                AppMockupScreen.PRICE_EXPLANATION -> Screen11PriceExplanation(
                    onBack = { currentScreen = AppMockupScreen.SAVINGS }
                )
                AppMockupScreen.STORE_DETAIL -> Screen12StoreDetail(
                    onCallClick = { /* Phone call intent */ },
                    onDirectionsClick = { /* Directions */ },
                    onCopyInquiryClick = { /* Clipboard */ },
                    onBack = { currentScreen = AppMockupScreen.MAP }
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
