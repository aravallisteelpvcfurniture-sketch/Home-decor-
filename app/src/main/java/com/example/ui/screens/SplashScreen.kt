package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }
    val pulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Entrance animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        // Subtle pulse
        pulse.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(2200)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D4ED8), // Vibrant Royal Blue top (matching reference)
                        Color(0xFF093DB3),
                        Color(0xFF00277A)  // Deep Royal Blue bottom
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background geometric circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.width * 0.75f,
                center = Offset(size.width * 0.5f, size.height * 0.35f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.5f, size.height * 0.35f)
            )
        }

        Column(
            modifier = Modifier
                .padding(32.dp)
                .scale(scale.value * pulse.value)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo Emblem with Royal Blue and White Aesthetic
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .testTag("splash_logo_surface"),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val w = size.width
                        val h = size.height

                        // Roof / Mountain chevron peak (Aravalli peak)
                        val chevronPath = Path().apply {
                            moveTo(w * 0.15f, h * 0.55f)
                            lineTo(w * 0.50f, h * 0.20f)
                            lineTo(w * 0.85f, h * 0.55f)
                            lineTo(w * 0.72f, h * 0.55f)
                            lineTo(w * 0.50f, h * 0.33f)
                            lineTo(w * 0.28f, h * 0.55f)
                            close()
                        }
                        drawPath(chevronPath, color = Color(0xFF0A4BC6))

                        // Lower steel beam line
                        drawLine(
                            color = Color(0xFFD69E2E), // Gold accent
                            start = Offset(w * 0.22f, h * 0.65f),
                            end = Offset(w * 0.78f, h * 0.65f),
                            strokeWidth = 10f
                        )

                        // Foundation pillar
                        drawLine(
                            color = Color(0xFF0A4BC6),
                            start = Offset(w * 0.50f, h * 0.65f),
                            end = Offset(w * 0.50f, h * 0.85f),
                            strokeWidth = 10f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title
            Text(
                text = "ARAVALLI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "STEEL PVC FURNITURE",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFBBF24), // Warm gold highlight
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Steel Railings • PVC Kitchens • Balconies • Interior",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Location Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "📍 Shyam Arcade, Modasa, Gujarat",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Bottom Loading / Ready Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(alpha.value)
        ) {
            Text(
                text = "Made in Modasa • 100% Quality Guaranteed",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
