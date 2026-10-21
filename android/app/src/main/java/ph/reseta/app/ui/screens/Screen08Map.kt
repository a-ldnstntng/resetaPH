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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.runtime.key
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import ph.reseta.app.R
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.model.PharmacyBranch
import ph.reseta.sample.SampleData

/**
 * SCREEN 8: Nearby Pharmacy Map & Branch Cards.
 * Rule 3 & Requirement 2:
 * - Uses Maps Compose (GoogleMap).
 * - API Key from local.properties (restricted by package and SHA-1).
 * - All branches labeled "Sample" with no invented addresses, phones, hours, or stock claims.
 */
@Composable
fun Screen08Map(
    branches: List<PharmacyBranch> = SampleData.sampleBranches,
    onBranchClick: (PharmacyBranch) -> Unit = {},
    onCopyInquiry: (PharmacyBranch) -> Unit = {},
    onDirectionsClick: (PharmacyBranch) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val initialPos = LatLng(
        branches.firstOrNull()?.latitude ?: 14.5685,
        branches.firstOrNull()?.longitude ?: 120.9934
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 14.5f)
    }

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
            // Interactive Maps Compose container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E4DE)),
                border = BorderStroke(1.dp, ResetaBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = false),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            compassEnabled = true,
                            myLocationButtonEnabled = false
                        )
                    ) {
                        branches.forEach { branch ->
                            key(branch.id) {
                                val markerState = rememberMarkerState(
                                    key = branch.id,
                                    position = LatLng(branch.latitude, branch.longitude)
                                )
                                Marker(
                                    state = markerState,
                                    title = branch.name,
                                    snippet = "Sample branch · Walang aktwal na stock o oras"
                                )
                            }
                        }
                    }
                }
            }

            // Data source disclaimer banner (Rule 2 requirement: no invented data)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ResetaPrimarySoft),
                border = BorderStroke(1.dp, ResetaPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ResetaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Nakabinbin ang listahan ng mga botika mula sa external data source. Lahat ng branch dito ay Sample data lamang.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ResetaInk
                    )
                }
            }

            Text(
                text = "Mga Pinakamalapit na Botika [Sample]",
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = branch.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ResetaInk
                                    )
                                    if (branch.isSample) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFF1F5F9)
                                        ) {
                                            Text(
                                                text = "Sample",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ResetaInkMuted,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
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
