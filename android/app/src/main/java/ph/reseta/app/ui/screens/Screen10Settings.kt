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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton

/**
 * SCREEN 10: Accessibility & Language Settings (Rule 5 & Rule 7).
 */
@Composable
fun Screen10Settings(
    onDeleteAllData: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var isTaglish by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = stringResource(R.string.settings_title),
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Language Selection Section (Rule 7)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.settings_language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ResetaInk
                )

                ResetaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isTaglish = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.lang_taglish),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaInk
                                )
                                Text(
                                    text = "Tagalog/Filipino para sa botika at mga bilin.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ResetaInkMuted
                                )
                            }
                            RadioButton(
                                selected = isTaglish,
                                onClick = { isTaglish = true },
                                colors = RadioButtonDefaults.colors(selectedColor = ResetaPrimary)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isTaglish = false },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.lang_english),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetaInk
                                )
                                Text(
                                    text = "English for medical terms and instructions.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ResetaInkMuted
                                )
                            }
                            RadioButton(
                                selected = !isTaglish,
                                onClick = { isTaglish = false },
                                colors = RadioButtonDefaults.colors(selectedColor = ResetaPrimary)
                            )
                        }
                    }
                }
            }

            // Data & Privacy Section (Rule 5)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.settings_data_privacy),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ResetaInk
                )

                ResetaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = ResetaPrimary
                            )
                            Text(
                                text = "Ligtas at Pribado ang Reseta Mo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ResetaInk
                            )
                        }

                        Text(
                            text = "Naka-off ang automatic cloud backup. Hindi kailanman sine-save o nila-log ang mga orihinal na litrato ng reseta. Ang mga resulta lamang ang lokal na naka-save sa iyong phone (Room database).",
                            style = MaterialTheme.typography.bodySmall,
                            color = ResetaInkMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SecondaryResetaButton(
                    text = "Burahin ang Lahat ng Scan Data",
                    onClick = onDeleteAllData,
                    icon = Icons.Default.DeleteForever
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Screen 10 - Settings")
@Composable
fun Screen10SettingsPreview() {
    ResetaPHTheme {
        Screen10Settings()
    }
}
