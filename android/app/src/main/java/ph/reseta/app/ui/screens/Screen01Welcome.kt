package ph.reseta.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.reseta.app.R
import ph.reseta.app.theme.ResetaPHTheme
import ph.reseta.app.theme.ResetaPrimary

/**
 * SCREEN 1: Splash / Welcome
 * Pixel-faithful recreation of Stitch & Mockup Screen 1.
 */
@Composable
fun Screen01Welcome(
    onContinue: () -> Unit = {},
    onAlreadyHaveAccount: () -> Unit = onContinue
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- TOP: BRAND IDENTITY ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            // Medical Cross with embedded leaf emblem
            Image(
                painter = painterResource(id = R.drawable.ic_reseta_logo),
                contentDescription = "ResetaPH Logo",
                modifier = Modifier.size(84.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Brand Wordmark
            Text(
                text = "ResetaPH",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
                color = ResetaPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline in Filipino
            Text(
                text = "Mas mura. Mas madali. Mas malusog.",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E8262),
                textAlign = TextAlign.Center
            )
        }

        // --- CENTER: HERO ARTWORK & FLOATING CAPSULE ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Background Organic Mint Fluid Waves
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scaleX = size.width / 390f
                val scaleY = size.height / 380f

                // Layer 1 (Subtle back wave)
                val path1 = Path().apply {
                    moveTo(-20f * scaleX, 230f * scaleY)
                    cubicTo(50f * scaleX, 170f * scaleY, 140f * scaleX, 180f * scaleY, 205f * scaleX, 215f * scaleY)
                    cubicTo(275f * scaleX, 250f * scaleY, 340f * scaleX, 220f * scaleY, 410f * scaleX, 175f * scaleY)
                    lineTo(410f * scaleX, size.height)
                    lineTo(-20f * scaleX, size.height)
                    close()
                }
                drawPath(path1, color = Color(0xFFD7F1E6).copy(alpha = 0.5f))

                // Layer 2 (Mid wave)
                val path2 = Path().apply {
                    moveTo(-20f * scaleX, 270f * scaleY)
                    cubicTo(60f * scaleX, 210f * scaleY, 160f * scaleX, 215f * scaleY, 230f * scaleX, 250f * scaleY)
                    cubicTo(300f * scaleX, 285f * scaleY, 360f * scaleX, 255f * scaleY, 410f * scaleX, 220f * scaleY)
                    lineTo(410f * scaleX, size.height)
                    lineTo(-20f * scaleX, size.height)
                    close()
                }
                drawPath(path2, color = Color(0xFFC5EBDC).copy(alpha = 0.8f))

                // Layer 3 (Foreground wave)
                val path3 = Path().apply {
                    moveTo(-30f * scaleX, 295f * scaleY)
                    cubicTo(70f * scaleX, 240f * scaleY, 175f * scaleX, 250f * scaleY, 245f * scaleX, 280f * scaleY)
                    cubicTo(315f * scaleX, 310f * scaleY, 360f * scaleX, 285f * scaleY, 420f * scaleX, 260f * scaleY)
                    lineTo(420f * scaleX, size.height)
                    lineTo(-30f * scaleX, size.height)
                    close()
                }
                drawPath(path3, color = Color(0xFFB3E4D1).copy(alpha = 0.95f))
            }

            // Tilted 3D Capsule Pill Graphic with soft shadow
            Box(
                modifier = Modifier
                    .offset(y = (-14).dp)
                    .rotate(-32f)
            ) {
                // Drop shadow underneath
                Box(
                    modifier = Modifier
                        .size(width = 138.dp, height = 72.dp)
                        .offset(y = 10.dp)
                        .background(
                            color = Color(0xFF0D5C4D).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(36.dp)
                        )
                )

                // The Capsule Pill Body
                Row(
                    modifier = Modifier
                        .size(width = 138.dp, height = 72.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(36.dp), spotColor = Color(0xFF0D5C4D))
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color.White)
                ) {
                    // Left half (Dark Forest Green with gloss highlight)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(ResetaPrimary)
                    ) {
                        // Gloss reflection sheen bar
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 14.dp)
                                .size(width = 36.dp, height = 9.dp)
                                .rotate(-6f)
                                .background(
                                    color = Color.White.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(4.5.dp)
                                )
                        )
                    }

                    // Right half (Crisp White with sparkle star and dot)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        // 4-point sparkle star in dark green
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val w = size.width
                            val h = size.height
                            val starPath = Path().apply {
                                moveTo(w * 0.5f, 0f)
                                lineTo(w * 0.6f, h * 0.38f)
                                lineTo(w, h * 0.5f)
                                lineTo(w * 0.6f, h * 0.62f)
                                lineTo(w * 0.5f, h)
                                lineTo(w * 0.4f, h * 0.62f)
                                lineTo(0f, h * 0.5f)
                                lineTo(w * 0.4f, h * 0.38f)
                                close()
                            }
                            drawPath(starPath, color = ResetaPrimary)
                        }

                        // Secondary tiny sparkle dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 10.dp)
                                .size(5.dp)
                                .background(
                                    color = ResetaPrimary.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        // --- BOTTOM: ACTIONS ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Primary Pill Button ("Get Started")
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ResetaPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 1.dp
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Link ("I already have an account")
            Text(
                text = "I already have an account",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF657E76),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAlreadyHaveAccount
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Screen 1 - Splash / Welcome")
@Composable
fun Screen01WelcomePreview() {
    ResetaPHTheme {
        Screen01Welcome()
    }
}
