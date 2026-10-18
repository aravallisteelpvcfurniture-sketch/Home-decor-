package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBlueLight
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun ProductDetailScreen(
    product: Product,
    onNavigateBack: () -> Unit,
    onGetQuote: (Product) -> Unit,
    onAddToCart: (Product) -> Unit = {}
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    val displayPrice = if (product.sellingPrice > 0) product.sellingPrice else 12499.0
    val mrp = (displayPrice * 1.25).toInt()
    val discountPercent = 20

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("product_detail_screen")
            .background(Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // TOP HERO SECTION (Image Gallery + Overlay Buttons)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                // Product Graphic / Gallery Showcase
                ProductHeroIllustration(
                    category = product.category,
                    productName = product.name,
                    pageIndex = selectedImageIndex
                )

                // Top Overlay Action Bar: Back, Favorite, Share
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back circular button
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .clickable { onNavigateBack() },
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Action buttons (Heart & Share)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .clickable { isFavorite = !isFavorite },
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF1E293B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .clickable {
                                    shareProduct(context, product)
                                },
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color(0xFF1E293B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // 5 Dot Gallery Indicators at Bottom of Image
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (0..4).forEach { index ->
                        val isSelected = index == selectedImageIndex
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) RoyalBluePrimary else Color(0xFF94A3B8).copy(alpha = 0.5f)
                                )
                                .clickable { selectedImageIndex = index }
                        )
                    }
                }
            }

            // WHITE ROUNDED BOTTOM CARD (Matches Right Phone in Reference Design)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Category Badge
                    Surface(
                        color = RoyalBlueLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = product.category.ifBlank { "Aravalli Premium Series" },
                            style = MaterialTheme.typography.labelSmall,
                            color = RoyalBluePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Product Title
                    Text(
                        text = product.name.ifBlank { "Premium Comfort Chair" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    // Tag row: "Stylish | Durable | Long Lasting"
                    Text(
                        text = "Stylish  |  Durable  |  Long Lasting",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Description
                    Text(
                        text = product.description.ifBlank {
                            "Experience ultimate comfort and lasting quality crafted by Aravalli Steel & PVC. Engineered with high-grade materials, precision joinery, and modern aesthetic design. Perfect for your home living room, modular kitchen, office, or luxury architectural spaces in Modasa, Gujarat."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF475569),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // 4 ROUND SPECIFICATION ICONS (Matching Reference Phone 3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecChip(icon = Icons.Default.Palette, label = "Premium\nFabric / SS")
                        SpecChip(icon = Icons.Default.Security, label = "Strong\nFrame")
                        SpecChip(icon = Icons.Default.Handyman, label = "Modern\nDesign")
                        SpecChip(icon = Icons.Default.Home, label = "Home &\nOffice")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // PRICING ROW: Price + Strikethrough MRP + Discount Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatInr(displayPrice),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBluePrimary
                            )
                            if (product.unit.isNotBlank()) {
                                Text(
                                    text = " / ${product.unit}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "₹ $mrp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }

                        Surface(
                            color = RoyalBlueLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$discountPercent% OFF",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBluePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ACTION BUTTON: "🛒 Add to Cart" / "📑 Get Instant Quotation"
                    Button(
                        onClick = {
                            onAddToCart(product)
                            onGetQuote(product)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(8.dp, RoundedCornerShape(27.dp))
                            .testTag("product_add_to_cart_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalBluePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "कोटेशन प्राप्त करें (Get Instant Quote)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // WhatsApp Direct Enquiry
                    OutlinedButton(
                        onClick = {
                            openWhatsApp(
                                context = context,
                                phone = "+919825012345",
                                message = "नमस्ते अरावल्ली! मुझे '${product.name}' के बारे में अधिक जानकारी व कीमत जाननी है।"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "💬 व्हाट्सएप पर पूछताछ (WhatsApp Enquiry)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F5132)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecChip(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = RoyalBlueLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalBluePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155),
            fontSize = 10.sp,
            lineHeight = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ProductHeroIllustration(category: String, productName: String, pageIndex: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val w = size.width
        val h = size.height

        // Studio gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
            )
        )

        // Drop shadow oval for floor
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF64748B).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.78f),
                radius = w * 0.4f
            ),
            topLeft = Offset(w * 0.15f, h * 0.68f),
            size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.2f)
        )

        val cx = w * 0.5f
        val cy = h * 0.48f

        when {
            category.contains("Railing", ignoreCase = true) -> {
                // Stainless Steel Railing with Glass
                // Glass Panel
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF93C5FD).copy(alpha = 0.6f), Color(0xFF60A5FA).copy(alpha = 0.3f))
                    ),
                    topLeft = Offset(cx - 130f, cy - 80f),
                    size = androidx.compose.ui.geometry.Size(260f, 150f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
                // SS Handrail
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFF1F5F9), Color(0xFF94A3B8), Color(0xFFF8FAFC), Color(0xFF64748B))
                    ),
                    topLeft = Offset(cx - 150f, cy - 100f),
                    size = androidx.compose.ui.geometry.Size(300f, 20f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
                // SS Pillars
                drawLine(color = Color(0xFF94A3B8), start = Offset(cx - 110f, cy - 100f), end = Offset(cx - 110f, cy + 90f), strokeWidth = 10f)
                drawLine(color = Color(0xFF94A3B8), start = Offset(cx + 110f, cy - 100f), end = Offset(cx + 110f, cy + 90f), strokeWidth = 10f)
                drawLine(color = Color(0xFF94A3B8), start = Offset(cx, cy - 100f), end = Offset(cx, cy + 90f), strokeWidth = 10f)
            }
            category.contains("Wardrobe", ignoreCase = true) || category.contains("PVC", ignoreCase = true) -> {
                // PVC Wardrobe Closet
                drawRoundRect(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))),
                    topLeft = Offset(cx - 110f, cy - 110f),
                    size = androidx.compose.ui.geometry.Size(220f, 210f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                )
                // Vertical divider
                drawLine(color = Color(0xFF94A3B8), start = Offset(cx, cy - 110f), end = Offset(cx, cy + 100f), strokeWidth = 4f)
                // Handles
                drawLine(color = Color(0xFFD97706), start = Offset(cx - 14f, cy - 15f), end = Offset(cx - 14f, cy + 30f), strokeWidth = 5f)
                drawLine(color = Color(0xFFD97706), start = Offset(cx + 14f, cy - 15f), end = Offset(cx + 14f, cy + 30f), strokeWidth = 5f)
            }
            else -> {
                // Luxury Accent Chair (Orange / Royal Blue as in reference)
                val chairColor = if (pageIndex % 2 == 0) Color(0xFFEA580C) else Color(0xFF1D4ED8)
                val chairDark = if (pageIndex % 2 == 0) Color(0xFFC2410C) else Color(0xFF1E3A8A)

                // Legs
                drawLine(color = Color(0xFFB45309), start = Offset(cx - 55f, cy + 45f), end = Offset(cx - 75f, cy + 85f), strokeWidth = 5f)
                drawLine(color = Color(0xFFB45309), start = Offset(cx + 55f, cy + 45f), end = Offset(cx + 75f, cy + 85f), strokeWidth = 5f)

                // Backrest
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(chairColor, chairDark)),
                    topLeft = Offset(cx - 75f, cy - 85f),
                    size = androidx.compose.ui.geometry.Size(150f, 120f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f, 30f)
                )
                // Seat
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(chairColor.copy(alpha = 0.9f), chairDark)),
                    topLeft = Offset(cx - 70f, cy + 10f),
                    size = androidx.compose.ui.geometry.Size(140f, 40f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }
        }
    }
}

private fun shareProduct(context: Context, product: Product) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            """
            *${product.name}*
            ब्रांड: अरावल्ली स्टील व पीवीसी फर्नीचर, मोडासा
            कैटेगरी: ${product.category}
            कीमत: ₹${product.sellingPrice} ${product.unit}
            
            श्याम आर्केड, मेघराज रोड, मोडासा, गुजरात
            कॉल / व्हाट्सएप: +91 98250 12345
            """.trimIndent()
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Product via")
    context.startActivity(shareIntent)
}
