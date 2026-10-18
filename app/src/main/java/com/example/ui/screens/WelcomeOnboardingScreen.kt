package com.example.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun WelcomeOnboardingScreen(
    onGetStarted: () -> Unit,
    onSkipToLogin: () -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPageData(
            headline = "Furniture Made for Real Comfort",
            subtitle = "Premium quality steel, PVC, aluminium and modern furniture for your home, office and commercial spaces.",
            category = "STEEL | PVC | ALUMINIUM | INTERIOR"
        ),
        OnboardingPageData(
            headline = "Precision Steel & Glass Railings",
            subtitle = "Architectural grade SS 304/316 railings, toughened glass balconies and custom designer staircases in Modasa, Gujarat.",
            category = "CUSTOM FABRICATION & ARCHITECTURAL"
        ),
        OnboardingPageData(
            headline = "100% Waterproof PVC Interiors",
            subtitle = "Termite-free, fire-retardant PVC modular kitchens, designer wardrobes, TV units and luxury wall panels built to last.",
            category = "MODULAR KITCHEN & HOME INTERIOR"
        )
    )

    val activePage = pages[currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("welcome_onboarding_screen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D4ED8), // Vibrant Royal Blue top
                        Color(0xFF093DB3),
                        Color(0xFF00277A)  // Deep Royal Blue bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BRAND LOGO (Matches Left Phone in Reference Design)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Peak / Roof Emblem
                Canvas(modifier = Modifier.size(width = 38.dp, height = 22.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.5f, 0f)
                        lineTo(size.width, size.height)
                        lineTo(size.width * 0.8f, size.height)
                        lineTo(size.width * 0.5f, size.height * 0.35f)
                        lineTo(size.width * 0.2f, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, color = Color.White)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ARAVALLI",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "STEEL PVC FURNITURE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle divider bar
                Text(
                    text = activePage.category,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }

            // CENTER HERO ILLUSTRATION (Matches Armchair Showcase in Reference Photo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.Center
            ) {
                ArmchairShowcaseArt(pageIndex = currentPage)
            }

            // BOTTOM SECTION: Headline, Subtitle, Dots & "Get Started ->" Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = activePage.headline,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = activePage.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Carousel Dots (3 dots, active dot is white pill)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        val width by animateDpAsState(targetValue = if (isSelected) 24.dp else 8.dp, label = "dot_width")
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.35f)
                                )
                                .clickable { currentPage = index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // White "Get Started ->" Pill Button (Matches Reference Phone 1)
                Button(
                    onClick = {
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            onGetStarted()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(30.dp))
                        .testTag("onboarding_get_started_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = RoyalBluePrimary
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (currentPage == pages.lastIndex) "Get Started" else "Next",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlueDark
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = RoyalBluePrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Proceed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Skip to Sign In",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .clickable { onSkipToLogin() }
                        .padding(8.dp)
                )
            }
        }
    }
}

private data class OnboardingPageData(
    val headline: String,
    val subtitle: String,
    val category: String
)

@Composable
private fun ArmchairShowcaseArt(pageIndex: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val w = size.width
        val h = size.height

        // Background soft radial highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2563EB).copy(alpha = 0.45f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.48f),
                radius = w * 0.45f
            )
        )

        // Floor reflection oval
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1E3A8A).copy(alpha = 0.6f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.85f),
                radius = w * 0.42f
            ),
            topLeft = Offset(w * 0.12f, h * 0.72f),
            size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.22f)
        )

        // Floor Lamp on right (as in photo)
        val lampX = w * 0.82f
        val lampBaseY = h * 0.78f
        drawLine(
            color = Color(0xFFF59E0B),
            start = Offset(lampX, lampBaseY),
            end = Offset(lampX, h * 0.22f),
            strokeWidth = 3f
        )
        drawCircle(
            color = Color(0xFFFEF3C7),
            radius = 20f,
            center = Offset(lampX, h * 0.22f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF3C7).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(lampX, h * 0.22f),
                radius = 45f
            )
        )

        // Indoor Plant Pot on left (as in photo)
        val plantX = w * 0.16f
        val plantBaseY = h * 0.8f
        drawOval(
            color = Color.White,
            topLeft = Offset(plantX - 22f, plantBaseY - 35f),
            size = androidx.compose.ui.geometry.Size(44f, 55f)
        )
        // Green leaves
        drawCircle(color = Color(0xFF10B981), radius = 16f, center = Offset(plantX - 10f, plantBaseY - 45f))
        drawCircle(color = Color(0xFF059669), radius = 18f, center = Offset(plantX + 8f, plantBaseY - 55f))
        drawCircle(color = Color(0xFF34D399), radius = 14f, center = Offset(plantX, plantBaseY - 65f))

        // Center Armchair (Royal Blue Velvet Cushion & Golden Legs)
        val cx = w * 0.5f
        val cy = h * 0.52f

        // 4 Gold Legs
        drawLine(color = Color(0xFFF59E0B), start = Offset(cx - 70f, cy + 50f), end = Offset(cx - 95f, cy + 95f), strokeWidth = 5f)
        drawLine(color = Color(0xFFF59E0B), start = Offset(cx + 70f, cy + 50f), end = Offset(cx + 95f, cy + 95f), strokeWidth = 5f)
        drawLine(color = Color(0xFFD97706), start = Offset(cx - 40f, cy + 45f), end = Offset(cx - 50f, cy + 85f), strokeWidth = 4f)
        drawLine(color = Color(0xFFD97706), start = Offset(cx + 40f, cy + 45f), end = Offset(cx + 50f, cy + 85f), strokeWidth = 4f)

        // Chair Backrest (Plush Curved Velvet)
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1D4ED8), Color(0xFF1E40AF), Color(0xFF172554))
            ),
            topLeft = Offset(cx - 85f, cy - 85f),
            size = androidx.compose.ui.geometry.Size(170f, 130f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f)
        )

        // Tufted grooves on backrest
        drawLine(color = Color(0xFF1E3A8A), start = Offset(cx - 40f, cy - 75f), end = Offset(cx - 40f, cy + 15f), strokeWidth = 3f)
        drawLine(color = Color(0xFF1E3A8A), start = Offset(cx, cy - 80f), end = Offset(cx, cy + 15f), strokeWidth = 3f)
        drawLine(color = Color(0xFF1E3A8A), start = Offset(cx + 40f, cy - 75f), end = Offset(cx + 40f, cy + 15f), strokeWidth = 3f)

        // Arms (Left & Right curved)
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))),
            topLeft = Offset(cx - 105f, cy - 25f),
            size = androidx.compose.ui.geometry.Size(35f, 65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF2563EB))),
            topLeft = Offset(cx + 70f, cy - 25f),
            size = androidx.compose.ui.geometry.Size(35f, 65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )

        // Main Seat Cushion (Plush Pillow)
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))),
            topLeft = Offset(cx - 82f, cy + 10f),
            size = androidx.compose.ui.geometry.Size(164f, 45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(22f, 22f)
        )

        // Accent Throw Pillow (Cream White / Light Gold)
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))),
            topLeft = Offset(cx - 38f, cy - 10f),
            size = androidx.compose.ui.geometry.Size(76f, 42f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
        )
    }
}
