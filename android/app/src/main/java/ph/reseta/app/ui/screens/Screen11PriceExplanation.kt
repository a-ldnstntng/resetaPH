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
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar

/**
 * SCREEN 11: AI Price & Safety Explanation Screen (Rule 2).
 */
@Composable
fun Screen11PriceExplanation(
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = "Paliwanag sa Presyo",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Generics Act Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResetaPrimarySoft),
                border = BorderStroke(1.dp, ResetaPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = ResetaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Batas Republika Blg. 6675 (Generics Act)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Karapatan ng bawat pasyenteng Pilipino na pumili ng Generic equivalent. Pareho ang active pharmaceutical ingredient at bisa ng Generic sa Branded ngunit mas mura ng 50% hanggang 80%.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ResetaInk
                        )
                    }
                }
            }

            // DOH DPRI Methodology
            ResetaCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Saan galing ang mga presyo?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ResetaInk
                    )
                    Text(
                        text = "Ang mga presyo sa ResetaPH ay nanggagaling sa pinakabagong DOH Drug Price Reference Index (DPRI) at mga verified price survey sa mga nangungunang botika (The Generics Pharmacy, Mercury Drug, at Watsons) noong Oktubre 2026.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ResetaInkMuted
                    )
                }
            }

            // Clinical Safety Verification
            ResetaCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = ResetaPrimary
                        )
                        Text(
                            text = "Dosage Safety Monitoring",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaInk
                        )
                    }
                    Text(
                        text = "Bawat reseta ay awtomatikong sinusuri ng aming clinical engine laban sa maximum daily dosage ceilings (hal. Paracetamol 4,000 mg limit) at tamang haba ng antibiotic regimen upang mapanatiling ligtas ang kalusugan mo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ResetaInkMuted
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen 11 - Price & Safety Explanation")
@Composable
fun Screen11PriceExplanationPreview() {
    ResetaPHTheme {
        Screen11PriceExplanation()
    }
}
