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
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import ph.reseta.app.theme.ResetaAccentOrange
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.theme.ResetaSurface
import ph.reseta.app.ui.components.PriceEstimateBadge
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.app.ui.components.SecondaryResetaButton
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.model.PrescriptionScan
import ph.reseta.pricing.PricingEngine
import ph.reseta.sample.SampleData

/**
 * SCREEN 5: Savings & Price Comparison Screen (Rule 2: Dated Prices).
 */
@Composable
fun Screen05Savings(
    scanResult: PrescriptionScan = SampleData.sampleScanResult,
    onViewScheduleClick: () -> Unit = {},
    onViewMapClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val basketSavings = PricingEngine.calculateBasketSavings(scanResult.items)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = stringResource(R.string.tab_savings),
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Savings Hero Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ResetaPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = ResetaAccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.savings_hero_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "₱%.2f".format(basketSavings.totalSavingsPhp),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text = "Makatipid ng hanggang ${basketSavings.averageSavingsPercent}% kapag Generic ang binili",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Dated Price Disclaimer Card (Rule 2)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ResetaPrimarySoft,
                border = BorderStroke(1.dp, ResetaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.dated_price_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = ResetaInkMuted,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Pharmacy Comparison Breakdown
            Text(
                text = "Paghahambing sa mga Botika (Total Basket)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ResetaInk
            )

            basketSavings.basketPerChain.forEach { chainTotal ->
                ResetaCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = ResetaPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = chainTotal.chain.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaInk
                                )
                                Text(
                                    text = if (chainTotal.isSample) "Halimbawang presyo" else "Tantya · ${chainTotal.datedPriceReference}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ResetaInkMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₱%.2f".format(chainTotal.totalCostPhp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (chainTotal.isLowestPrice) ResetaPrimary else ResetaInk
                            )
                            if (chainTotal.isLowestPrice) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        text = stringResource(R.string.lowest_price_badge),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ResetaPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Item-by-Item Breakdown
            Text(
                text = "Detalye bawat Gamot",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ResetaInk
            )

            scanResult.items.forEach { item ->
                val estimate = PricingEngine.estimateItemPrice(item)
                if (estimate != null) {
                    ResetaCard {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = estimate.genericName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaInk
                                )
                                PriceEstimateBadge(label = if (estimate.isSample) "Sample price" else "Tantya · ${estimate.recordedDate}")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Generic: ₱%.2f × %d".format(estimate.genericUnitPrice.amountPhp, estimate.quantity),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ResetaPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "₱%.2f".format(estimate.totalGenericPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaPrimary
                                )
                            }

                            val branded = estimate.brandedUnitPrice
                            if (branded != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Branded (${estimate.brandName}): ₱%.2f/pc".format(branded.amountPhp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ResetaInkMuted
                                    )
                                    Text(
                                        text = "₱%.2f".format(estimate.totalBrandedPrice),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ResetaInkMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            PrimaryResetaButton(
                text = "Tingnan ang Schedule ng Gamot",
                onClick = onViewScheduleClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryResetaButton(
                text = "Tingnan ang Mapa ng Botika",
                onClick = onViewMapClick,
                icon = Icons.Default.Storefront
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen 5 - Savings & Prices")
@Composable
fun Screen05SavingsPreview() {
    ResetaPHTheme {
        Screen05Savings()
    }
}
