package ph.reseta.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
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
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.model.PharmacyBranch
import ph.reseta.sample.SampleData

/**
 * SCREEN 12: Pharmacy Store Details & Inquiry Screen.
 */
@Composable
fun Screen12StoreDetail(
    branch: PharmacyBranch = SampleData.sampleBranches.first(),
    onCallClick: () -> Unit = {},
    onDirectionsClick: () -> Unit = {},
    onCopyInquiryClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val sampleInquiry = "Magandang araw po! Nais ko pong itanong kung may available kayong Co-Amoxiclav 625mg (#14) at Amlodipine 10mg (#30) sa ${branch.name}? Maraming salamat po!"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = "Detalye ng Botika",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Storefront Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ResetaPrimarySoft),
                border = BorderStroke(1.dp, ResetaBorder)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = ResetaPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = branch.chain.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaPrimary
                        )
                    }
                }
            }

            // Branch Address Card
            ResetaCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = branch.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ResetaInk
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ResetaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${branch.address} (%.2f km)".format(branch.distanceKm),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ResetaInkMuted
                        )
                    }

                    if (branch.phoneNumber != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = ResetaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = branch.phoneNumber ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ResetaInkMuted
                            )
                        }
                    }
                }
            }

            // Pre-formatted Inquiry Message Card
            ResetaCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Handang Tanong sa Botika (Inquiry Template)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ResetaInk
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ResetaPrimarySoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sampleInquiry,
                            style = MaterialTheme.typography.bodySmall,
                            color = ResetaInk,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    SecondaryResetaButton(
                        text = "Kopyahin ang Inquiry Text",
                        onClick = onCopyInquiryClick,
                        icon = Icons.Default.ContentCopy
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrimaryResetaButton(
                    text = "Direksyon",
                    onClick = onDirectionsClick,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Directions
                )
                if (branch.phoneNumber != null) {
                    SecondaryResetaButton(
                        text = "Tawagan",
                        onClick = onCallClick,
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Call
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen 12 - Store Details")
@Composable
fun Screen12StoreDetailPreview() {
    ResetaPHTheme {
        Screen12StoreDetail()
    }
}
