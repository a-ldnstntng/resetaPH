package ph.reseta.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.reseta.app.theme.ResetaDanger
import ph.reseta.app.theme.ResetaInk
import ph.reseta.app.theme.ResetaInkMuted
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.ui.components.PrimaryResetaButton
import ph.reseta.app.ui.components.ResetaCard
import ph.reseta.app.ui.components.ResetaTopBar
import ph.reseta.app.ui.components.SecondaryResetaButton

/**
 * Screen displayed on network failure, no internet connection, or rejected scan.
 */
@Composable
fun ScreenError(
    errorMessage: String = "Walang koneksyon sa internet. Pakisuri ang iyong koneksyon.",
    onRetry: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    val isNetworkIssue = errorMessage.contains("internet", ignoreCase = true) ||
            errorMessage.contains("koneksyon", ignoreCase = true) ||
            errorMessage.contains("server", ignoreCase = true) ||
            errorMessage.contains("timeout", ignoreCase = true)

    val headlineTitle = if (isNetworkIssue) {
        "Walang Koneksyon sa Internet"
    } else {
        "Hindi Na-proseso ang Reseta"
    }

    val retryCta = if (isNetworkIssue) {
        "Subukang Muli (Retry)"
    } else {
        "Subukan Muling I-scan"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ResetaTopBar(
            title = if (isNetworkIssue) "Koneksyon sa Network" else "May Naging Problema",
            onBackClick = onBackToHome
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFFEBEE),
                modifier = Modifier.size(80.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isNetworkIssue) Icons.Default.WifiOff else Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = ResetaDanger,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = headlineTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ResetaInk,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            ResetaCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ResetaInkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryResetaButton(
                text = retryCta,
                onClick = onRetry,
                icon = Icons.Default.Refresh
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryResetaButton(
                text = "Bumalik sa Home",
                onClick = onBackToHome
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen Error - Network or Scan Failure")
@Composable
fun ScreenErrorPreview() {
    ResetaPHTheme {
        ScreenError()
    }
}
