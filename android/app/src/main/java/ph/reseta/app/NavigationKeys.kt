package ph.reseta.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ScreenWelcome : NavKey
@Serializable data object ScreenHome : NavKey
@Serializable data object ScreenScanning : NavKey
@Serializable data object ScreenReview : NavKey
@Serializable data object ScreenSavings : NavKey
@Serializable data object ScreenSchedule : NavKey
@Serializable data object ScreenCounter : NavKey
@Serializable data object ScreenMap : NavKey
@Serializable data object ScreenHistory : NavKey
@Serializable data object ScreenSettings : NavKey
@Serializable data object ScreenPriceExplanation : NavKey
@Serializable data object ScreenStoreDetail : NavKey
