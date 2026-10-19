package ph.reseta.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.reseta.app.R
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.app.util.ResetaTtsManager
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan
import ph.reseta.sample.SampleData

/**
 * SCREEN 6: Dosage Schedule & Medication Reminders.
 * Rule 4: Text-to-speech checks for fil-PH voice at runtime, falls back if missing,
 * never claims it works offline, and builds spoken text strictly from schedule fields.
 */
@Composable
fun Screen06Schedule(
    scanResult: PrescriptionScan = SampleData.sampleScanResult,
    onListenClick: () -> Unit = {},
    onCounterModeClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val ttsManager = remember { ResetaTtsManager(context) }
    val ttsState by ttsManager.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = stringResource(R.string.schedule_title),
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Audio TTS playback CTA & Rule 4 Voice Fallback notice
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResetaPrimarySoft),
                border = BorderStroke(1.dp, ResetaPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isSpeaking = ttsState is ResetaTtsManager.TtsState.Speaking

                    SecondaryResetaButton(
                        text = if (isSpeaking) "Itigil ang Pagbasa" else stringResource(R.string.listen_schedule_cta),
                        onClick = {
                            if (isSpeaking) {
                                ttsManager.stop()
                            } else {
                                ttsManager.speakSchedule(scanResult.items)
                            }
                        },
                        icon = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp
                    )

                    // Show runtime voice status / fallback notice
                    val readyState = ttsState as? ResetaTtsManager.TtsState.Ready
                    if (readyState?.notice != null) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = readyState.notice,
                                style = MaterialTheme.typography.bodySmall,
                                color = ResetaInkMuted
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ResetaInkMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Paalala: Kailangan ng internet connection para sa boses at hindi ito gumagana offline.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ResetaInkMuted
                            )
                        }
                    }
                }
            }

            // Morning Slot
            ScheduleSlotCard(
                slotTitle = "Umaga (Morning)",
                slotSubtitle = "Kadalasan 7:00 AM - 8:00 AM kasabay o pagkatapos mag-almusal",
                items = scanResult.items.filter { it.slots.contains("umaga") }
            )

            // Noon Slot
            ScheduleSlotCard(
                slotTitle = "Tanghali (Noon)",
                slotSubtitle = "Kadalasan 12:00 PM - 1:00 PM pagkatapos ng tanghalian",
                items = scanResult.items.filter { it.slots.contains("tanghali") }
            )

            // Night Slot
            ScheduleSlotCard(
                slotTitle = "Gabi (Night / Bedtime)",
                slotSubtitle = "Kadalasan 7:00 PM pagkatapos ng hapunan o bago matulog",
                items = scanResult.items.filter { it.slots.contains("gabi") }
            )

            // Pharmacy Counter Mode CTA
            PrimaryResetaButton(
                text = stringResource(R.string.counter_mode_cta),
                onClick = onCounterModeClick
            )
        }
    }
}

@Composable
fun ScheduleSlotCard(
    slotTitle: String,
    slotSubtitle: String,
    items: List<PrescriptionItem>
) {
    if (items.isEmpty()) return

    ResetaCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text(
                    text = slotTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ResetaPrimary
                )
                Text(
                    text = slotSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ResetaInkMuted
                )
            }

            items.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ResetaPrimarySoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${item.genericName} (${item.brandName ?: "Generic"})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaInk
                        )
                        Text(
                            text = "${item.strength} • ${item.dosesPerDay} beses sa isang araw • ${item.quantity} piraso",
                            style = MaterialTheme.typography.bodySmall,
                            color = ResetaInkMuted
                        )
                        if (!item.mealNoteTl.isNullOrBlank()) {
                            Text(
                                text = "Paalala: ${item.mealNoteTl}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ResetaPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen 6 - Medication Schedule")
@Composable
fun Screen06SchedulePreview() {
    ResetaPHTheme {
        Screen06Schedule()
    }
}
