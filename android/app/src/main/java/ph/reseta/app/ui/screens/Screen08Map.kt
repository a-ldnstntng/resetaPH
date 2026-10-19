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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
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
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.model.PharmacyBranch
import ph.reseta.sample.SampleData

/**
 * SCREEN 8: Nearby Pharmacy Map & Branch Cards.
 */
@Composable
fun Screen08Map(
    branches: List<PharmacyBranch> = SampleData.sampleBranches,
    onBranchClick: (PharmacyBranch) -> Unit = {},
    onCopyInquiry: (PharmacyBranch) -> Unit = {},
    onDirectionsClick: (PharmacyBranch) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = stringResource(R.string.pharmacies_title),
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulated Map Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E4DE)),
                border = BorderStroke(1.dp, ResetaBorder)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = ResetaPrimary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xCCFFFFFF)
                        ) {
                            Text(
                                text = "Taft Avenue, Malate · 4 na Botika",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = ResetaInk,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Mga Pinakamalapit na Branch",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ResetaInk
            )

            branches.forEach { branch ->
                ResetaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBranchClick(branch) },
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = branch.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaInk
                                )
                                Text(
                                    text = branch.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ResetaInkMuted
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ResetaPrimarySoft
                            ) {
                                Text(
                                    text = "%.2f km".format(branch.distanceKm),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ResetaPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SecondaryResetaButton(
                                text = "Direksyon",
                                onClick = { onDirectionsClick(branch) },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Directions
                            )
                            SecondaryResetaButton(
                                text = "Kopyahin",
                                onClick = { onCopyInquiry(branch) },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.ContentCopy
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen 8 - Pharmacy Map")
@Composable
fun Screen08MapPreview() {
    ResetaPHTheme {
        Screen08Map()
    }
}
