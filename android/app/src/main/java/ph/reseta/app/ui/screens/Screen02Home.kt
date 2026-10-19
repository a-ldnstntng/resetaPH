package ph.reseta.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoLibrary
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
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.model.PrescriptionScan
import ph.reseta.sample.SampleData

/**
 * SCREEN 2: Home Screen (Camera scan, gallery upload, sample prescriptions, scan history).
 */
@Composable
fun Screen02Home(
    onScanClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSampleClick: (String) -> Unit = {},
    onHistoryItemClick: (PrescriptionScan) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = "ResetaPH",
            onSettingsClick = onSettingsClick,
            currentLanguageTag = "TAG"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Title
            Column {
                Text(
                    text = stringResource(R.string.home_hero_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = ResetaInk,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.brand_tagline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = ResetaInkMuted
                )
            }

            // Primary CTAs
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryResetaButton(
                    text = stringResource(R.string.scan_cta),
                    onClick = onScanClick,
                    icon = Icons.Default.CameraAlt
                )
                SecondaryResetaButton(
                    text = stringResource(R.string.upload_cta),
                    onClick = onUploadClick,
                    icon = Icons.Default.PhotoLibrary
                )
            }

            // Sample Prescriptions Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.sample_header),
                    style = MaterialTheme.typography.titleMedium,
                    color = ResetaInk,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSampleClick("sample_aug") },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ResetaSurface),
                    border = BorderStroke(1.dp, ResetaBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ResetaPrimarySoft,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ResetaPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Antibiotic at Maintenance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ResetaInk
                            )
                            Text(
                                text = "Augmentin 625mg & Amlodipine 10mg",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ResetaInkMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ResetaInkMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Recent Scan History Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.history_header),
                    style = MaterialTheme.typography.titleMedium,
                    color = ResetaInk,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryItemClick(SampleData.sampleScanResult) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ResetaSurface),
                    border = BorderStroke(1.dp, ResetaBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Co-Amoxiclav, Amlodipine, Metformin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ResetaInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dr. Maria Santos, MD · 3 gamot · 10/18/2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = ResetaInkMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ResetaInkMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Disclaimer Footer
            Text(
                text = stringResource(R.string.disclaimer_footer),
                style = MaterialTheme.typography.bodySmall,
                color = ResetaInkMuted
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen 2 - Home")
@Composable
fun Screen02HomePreview() {
    ResetaPHTheme {
        Screen02Home()
    }
}
