package ph.reseta.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.reseta.app.R
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaDanger
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.theme.ResetaSurface
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.model.PrescriptionScan
import ph.reseta.sample.SampleData

/**
 * SCREEN 9: Saved Scan History (Rule 5: Delete history option, local only).
 */
@Composable
fun Screen09History(
    scans: List<PrescriptionScan> = listOf(SampleData.sampleScanResult),
    onScanClick: (PrescriptionScan) -> Unit = {},
    onClearHistory: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = stringResource(R.string.history_header),
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (scans.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ResetaSurface),
                    border = BorderStroke(1.dp, ResetaBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.history_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = ResetaInkMuted
                        )
                    }
                }
            } else {
                scans.forEach { scan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onScanClick(scan) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ResetaSurface),
                        border = BorderStroke(1.dp, ResetaBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = ResetaPrimarySoft,
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = ResetaPrimary,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = scan.items.joinToString(", ") { it.genericName },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ResetaInk,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${scan.doctorName ?: "Reseta"} · ${scan.items.size} gamot · ${scan.dateOnSlip ?: "Kamakailan"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ResetaInkMuted
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = ResetaInkMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SecondaryResetaButton(
                    text = stringResource(R.string.clear_history),
                    onClick = onClearHistory,
                    icon = Icons.Default.Delete
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen 9 - Scan History")
@Composable
fun Screen09HistoryPreview() {
    ResetaPHTheme {
        Screen09History()
    }
}
