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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ph.reseta.app.theme.ResetaSurface
import ph.reseta.app.theme.ResetaWarning
import ph.reseta.app.ui.components.DosageSlotBadge
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.model.PrescriptionItem
import ph.reseta.model.PrescriptionScan
import ph.reseta.sample.SampleData

/**
 * SCREEN 4: Review / Transcription Verification Screen.
 */
@Composable
fun Screen04Review(
    scanResult: PrescriptionScan = SampleData.sampleScanResult,
    onConfirm: () -> Unit = {},
    onDeepScan: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = stringResource(R.string.review_title),
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Line Count Check Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ResetaPrimarySoft,
                border = BorderStroke(1.dp, ResetaPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ResetaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${scanResult.items.size} sa ${scanResult.medicationLineCount} gamot ang nabasa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaPrimary
                        )
                        Text(
                            text = stringResource(R.string.review_count_match),
                            style = MaterialTheme.typography.bodySmall,
                            color = ResetaInkMuted
                        )
                    }
                }
            }

            // Doctor Info Header
            if (scanResult.doctorName != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ResetaSurface),
                    border = BorderStroke(1.dp, ResetaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = scanResult.doctorName ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ResetaInk
                        )
                        Text(
                            text = scanResult.dateOnSlip ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = ResetaInkMuted
                        )
                    }
                }
            }

            // Extracted Medication Items List
            Text(
                text = "Mga Nabasang Gamot (${scanResult.items.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ResetaInk
            )

            scanResult.items.forEachIndexed { index, item ->
                ResetaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${index + 1}. ${item.genericName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaInk
                                )
                                if (item.brandName != null) {
                                    Text(
                                        text = "Brand: ${item.brandName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ResetaPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ResetaPrimarySoft
                            ) {
                                Text(
                                    text = "#${item.quantity}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ResetaPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Strength: ${item.strength} · Form: ${item.form}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ResetaInkMuted
                        )

                        // Dosage Slots
                        Row {
                            item.slots.forEach { slot ->
                                DosageSlotBadge(slot)
                            }
                        }

                        if (!item.mealNoteTl.isNullOrEmpty()) {
                            Text(
                                text = "Paano inumin: ${item.mealNoteTl}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ResetaInk
                            )
                        }

                        if (item.uncertainFields.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF3E0),
                                border = BorderStroke(1.dp, ResetaWarning)
                            ) {
                                Text(
                                    text = "May hindi tiyak na sulat: ${item.uncertainFields.joinToString(", ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ResetaWarning,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        item.safetyWarnings.forEach { warning ->
                            ph.reseta.app.ui.components.SafetyWarningBanner(warning)
                        }
                    }
                }
            }

            // Deep Decipher Button
            SecondaryResetaButton(
                text = stringResource(R.string.deep_scan_cta),
                onClick = onDeepScan,
                icon = Icons.Default.AutoAwesome
            )

            // Confirm & View Results CTA
            PrimaryResetaButton(
                text = stringResource(R.string.confirm_review_cta),
                onClick = onConfirm
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen 4 - Review Medications")
@Composable
fun Screen04ReviewPreview() {
    ResetaPHTheme {
        Screen04Review()
    }
}
