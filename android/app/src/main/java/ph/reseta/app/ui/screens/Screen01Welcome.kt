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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.reseta.app.theme.ResetaBorder
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary
import ph.reseta.app.theme.ResetaPrimarySoft
import ph.reseta.app.theme.ResetaSurface
import ph.reseta.app.ui.components.PrimaryResetaButton

/**
 * SCREEN 1: Welcome & First-Time Language Picker (Rule 7).
 */
@Composable
fun Screen01Welcome(
    onContinue: (isTaglish: Boolean) -> Unit = {}
) {
    var isTaglishSelected by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = ResetaPrimarySoft,
                modifier = Modifier.size(72.dp),
                border = BorderStroke(1.dp, ResetaBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = ResetaPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Maligayang Pagdating sa ResetaPH",
                style = MaterialTheme.typography.headlineMedium,
                color = ResetaPrimary,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Piliin ang nais mong wika sa pagbasa ng reseta at mga paalala sa gamot.",
                style = MaterialTheme.typography.bodyLarge,
                color = ResetaInkMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Taglish Option (Default)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTaglishSelected = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTaglishSelected) ResetaPrimarySoft else ResetaSurface
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isTaglishSelected) ResetaPrimary else ResetaBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Taglish / Filipino (Default)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mga presyo, paraan ng pag-inom, at botika sa Filipino.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ResetaInkMuted
                        )
                    }
                    if (isTaglishSelected) {
                        Surface(
                            shape = CircleShape,
                            color = ResetaPrimary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // English Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTaglishSelected = false },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (!isTaglishSelected) ResetaPrimarySoft else ResetaSurface
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (!isTaglishSelected) ResetaPrimary else ResetaBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "English",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ResetaInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prices, dosage instructions, and reminders in English.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ResetaInkMuted
                        )
                    }
                    if (!isTaglishSelected) {
                        Surface(
                            shape = CircleShape,
                            color = ResetaPrimary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Maaari mo itong palitan anumang oras sa Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ResetaInkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryResetaButton(
                text = if (isTaglishSelected) "Magpatuloy" else "Continue",
                onClick = { onContinue(isTaglishSelected) }
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen 1 - Welcome / Onboarding")
@Composable
fun Screen01WelcomePreview() {
    ResetaPHTheme {
        Screen01Welcome()
    }
}
