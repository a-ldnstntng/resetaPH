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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.sp
import ph.reseta.app.R
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.model.PrescriptionScan
import ph.reseta.sample.SampleData

/**
 * SCREEN 7: Pharmacy Counter Mode (High contrast, large fonts for dispensary counters).
 */
@Composable
fun Screen07CounterCard(
    scanResult: PrescriptionScan = SampleData.sampleScanResult,
    onClose: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A140F))
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x3326A69A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = stringResource(R.string.counter_mode_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA3F2D4)
                    )
                    Text(
                        text = stringResource(R.string.counter_mode_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0E6E2)
                    )
                }
            }

            scanResult.items.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${index + 1}",
                                style = MaterialTheme.typography.titleLarge,
                                color = ResetaPrimary,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ResetaPrimarySoft
                            ) {
                                Text(
                                    text = "BILANG: ${item.quantity} ${item.form.uppercase()}S",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ResetaPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Text(
                            text = item.genericName.uppercase(),
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = ResetaInk
                        )

                        Text(
                            text = "STRENGTH: ${item.strength}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00684A)
                        )

                        if (item.brandName != null) {
                            HorizontalDivider(color = ResetaBorder, thickness = 1.dp)
                            Text(
                                text = "Nakasulat sa reseta: ${item.brandName} (${item.writtenAs})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ResetaInkMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryResetaButton(
            text = stringResource(R.string.close_counter_cta),
            onClick = onClose
        )
    }
}

@Preview(showBackground = true, name = "Screen 7 - Counter Card Mode")
@Composable
fun Screen07CounterCardPreview() {
    ResetaPHTheme {
        Screen07CounterCard()
    }
}
