package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.formatInr
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBlueLight
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.viewmodel.AravalliViewModel

@Composable
fun HomeScreen(
    viewModel: AravalliViewModel,
    onNavigateToEstimates: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onNavigateToAiTools: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToProductDetail: (Product) -> Unit = {}
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.products.collectAsState()
    val isHi = currentLang == "hi"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val defaultReferenceProducts = remember {
        listOf(
            Product(
                id = "ref-1",
                name = "Stainless Steel Railing",
                category = "Steel Railing",
                description = "Architectural grade SS 304/316 glass railing with polished chrome clamps for staircases & balconies.",
                sellingPrice = 1850.0,
                unit = "sq.ft",
                stockQuantity = 45.0
            ),
            Product(
                id = "ref-2",
                name = "PVC Wardrobe",
                category = "PVC Furniture",
                description = "100% waterproof, termite-free luxury PVC wardrobe closet in white and oak laminate finish.",
                sellingPrice = 18500.0,
                unit = "unit",
                stockQuantity = 12.0
            ),
            Product(
                id = "ref-3",
                name = "Aluminium Glass Balcony",
                category = "Aluminium Glass",
                description = "Modern toughened glass balcony with Jindal aluminium frame and heavy duty fittings.",
                sellingPrice = 2800.0,
                unit = "sq.ft",
                stockQuantity = 20.0
            ),
            Product(
                id = "ref-4",
                name = "TV Unit & Interior",
                category = "Interior",
                description = "Contemporary living room entertainment console and wooden paneling.",
                sellingPrice = 16999.0,
                unit = "unit",
                stockQuantity = 8.0
            ),
            Product(
                id = "ref-5",
                name = "Premium Comfort Chair",
                category = "Interior",
                description = "Luxury royal blue & orange accent comfort armchair with high quality fabric and brass legs.",
                sellingPrice = 12499.0,
                unit = "piece",
                stockQuantity = 15.0
            )
        )
    }

    val displayProducts = if (products.isNotEmpty()) {
        (products + defaultReferenceProducts).distinctBy { it.name }
    } else {
        defaultReferenceProducts
    }

    val filteredProducts = displayProducts.filter { prod ->
        val matchesCategory = selectedCategory == "All" || prod.category.contains(selectedCategory, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() || prod.name.contains(searchQuery, ignoreCase = true) || prod.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. ROYAL BLUE CURVED HEADER (Matches Center Phone in Reference Design)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(RoyalBluePrimary, RoyalBlueDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Menu Icon
                        IconButton(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }

                        // Center: ARAVALLI STEEL PVC Logo with Peak
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Canvas(modifier = Modifier.size(width = 28.dp, height = 14.dp)) {
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ARAVALLI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "STEEL PVC FURNITURE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        // Right: Cart icon with Badge (count = 2) & Admin
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onNavigateToEstimates,
                                modifier = Modifier.size(40.dp)
                            ) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = GoldAccent,
                                            contentColor = RoyalBlueDark
                                        ) {
                                            Text("2", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Cart / Quotes",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. WELCOME GREETING & SEARCH BAR
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = if (isHi) "नमस्ते," else "Hello,",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isHi) "अरावल्ली फर्नीचर में आपका स्वागत है" else "Welcome to Aravalli",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = if (isHi) "आपका सपनों का घर, हमारा आधुनिक डिजाइन" else "Your Dream Space, Our Design",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar with Filter Icon (Matches Center Phone in Reference Photo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .testTag("home_search_bar"),
                        placeholder = {
                            Text(
                                text = if (isHi) "रेलिंग, पीवीसी फर्नीचर, बालकनी खोजें..." else "Search for furniture, railing, etc...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = RoyalBluePrimary)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = RoyalBluePrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedCategory = if (selectedCategory == "All") "Steel Railing" else "All"
                            },
                        color = RoyalBluePrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. CATEGORIES ROW (4 Icon Cards: Steel Railing, PVC Furniture, Aluminium Glass, Interior)
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        CategoryCard(
                            title = "Steel\nRailing",
                            icon = Icons.Default.TableRestaurant,
                            isSelected = selectedCategory == "Steel Railing" || selectedCategory == "All",
                            onClick = { selectedCategory = if (selectedCategory == "Steel Railing") "All" else "Steel Railing" }
                        )
                    }
                    item {
                        CategoryCard(
                            title = "PVC\nFurniture",
                            icon = Icons.Default.DoorFront,
                            isSelected = selectedCategory == "PVC Furniture",
                            onClick = { selectedCategory = if (selectedCategory == "PVC Furniture") "All" else "PVC Furniture" }
                        )
                    }
                    item {
                        CategoryCard(
                            title = "Aluminium\nGlass",
                            icon = Icons.Default.Window,
                            isSelected = selectedCategory == "Aluminium Glass",
                            onClick = { selectedCategory = if (selectedCategory == "Aluminium Glass") "All" else "Aluminium Glass" }
                        )
                    }
                    item {
                        CategoryCard(
                            title = "Interior\nDesign",
                            icon = Icons.Default.Home,
                            isSelected = selectedCategory == "Interior",
                            onClick = { selectedCategory = if (selectedCategory == "Interior") "All" else "Interior" }
                        )
                    }
                }
            }
        }

        // 4. QUICK ACTIONS STRIP (Admin, New Quote, Calculators, Attendance)
        item {
            Column(modifier = Modifier.padding(top = 18.dp, bottom = 6.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        QuickActionButton(
                            label = if (isHi) "🛡️ एडमिन पैनल" else "🛡️ Admin Panel",
                            onClick = onNavigateToAdmin,
                            isHighlighted = true
                        )
                    }
                    item {
                        QuickActionButton(
                            label = if (isHi) "+ नया कोटेशन" else "+ New Quote",
                            onClick = onNavigateToEstimates
                        )
                    }
                    item {
                        QuickActionButton(
                            label = if (isHi) "📏 माप कैलकुलेटर" else "📏 Calculators",
                            onClick = onNavigateToTools
                        )
                    }
                    item {
                        QuickActionButton(
                            label = if (isHi) "👥 ग्राहक" else "👥 Customers",
                            onClick = onNavigateToCustomers
                        )
                    }
                    item {
                        QuickActionButton(
                            label = if (isHi) "👷 हाजिरी" else "👷 Attendance",
                            onClick = onNavigateToAttendance
                        )
                    }
                    item {
                        QuickActionButton(
                            label = if (isHi) "📑 कैटलॉग" else "📑 Catalogue",
                            onClick = onNavigateToCatalogue
                        )
                    }
                }
            }
        }

        // 5. "POPULAR PRODUCTS" HEADER WITH "VIEW ALL ->"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHi) "लोकप्रिय उत्पाद (Popular Products)" else "Popular Products",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "View All →",
                    style = MaterialTheme.typography.labelMedium,
                    color = RoyalBluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateToProducts() }
                        .padding(4.dp)
                )
            }
        }

        // 6. 2-COLUMN POPULAR PRODUCTS GRID (Matches Center Phone in Reference Photo)
        items(filteredProducts.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ProductGridCard(
                    product = pair[0],
                    badge = when (pair[0].name) {
                        "Stainless Steel Railing" -> "Best Seller"
                        "PVC Wardrobe" -> "Premium"
                        "Aluminium Glass Balcony" -> "Trending"
                        else -> "Modern"
                    },
                    modifier = Modifier.weight(1f),
                    onCardClick = { onNavigateToProductDetail(pair[0]) },
                    onBuyNowClick = { onNavigateToProductDetail(pair[0]) }
                )

                if (pair.size > 1) {
                    ProductGridCard(
                        product = pair[1],
                        badge = when (pair[1].name) {
                            "PVC Wardrobe" -> "Premium"
                            "Aluminium Glass Balcony" -> "Trending"
                            "TV Unit & Interior" -> "Modern"
                            else -> "Featured"
                        },
                        modifier = Modifier.weight(1f),
                        onCardClick = { onNavigateToProductDetail(pair[1]) },
                        onBuyNowClick = { onNavigateToProductDetail(pair[1]) }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 82.dp, height = 86.dp)
            .shadow(if (isSelected) 6.dp else 1.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        color = if (isSelected) RoyalBluePrimary else Color.White,
        shape = RoundedCornerShape(18.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else RoyalBluePrimary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else Color(0xFF1E293B),
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 11.sp,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun ProductGridCard(
    product: Product,
    badge: String,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onBuyNowClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable { onCardClick() },
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column {
            // Product Image Thumbnail with Badge & Heart Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFF1F5F9))
            ) {
                // Mini graphical artwork
                ProductThumbnailArt(category = product.category)

                // Top Left: Badge (e.g. Best Seller / Premium)
                Surface(
                    color = when (badge) {
                        "Best Seller" -> RoyalBluePrimary
                        "Premium" -> Color(0xFF2563EB)
                        "Trending" -> Color(0xFF0D9488)
                        else -> Color(0xFF475569)
                    },
                    shape = RoundedCornerShape(bottomEnd = 10.dp, topStart = 18.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Top Right: Favorite Heart
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { isFavorite = !isFavorite },
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Info Details
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatInr(product.sellingPrice)}${if (product.unit.isNotBlank()) " / ${product.unit}" else ""}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBluePrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // "Buy Now" Royal Blue Pill Button
                Button(
                    onClick = onBuyNowClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBluePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(17.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Buy Now",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductThumbnailArt(category: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Subtle gradient background
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
            )
        )

        val cx = w * 0.5f
        val cy = h * 0.55f

        when {
            category.contains("Railing", ignoreCase = true) -> {
                // Glass panel
                drawRoundRect(
                    color = Color(0xFF93C5FD).copy(alpha = 0.5f),
                    topLeft = Offset(cx - 50f, cy - 35f),
                    size = androidx.compose.ui.geometry.Size(100f, 60f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                // SS Handrail
                drawLine(color = Color(0xFF475569), start = Offset(cx - 60f, cy - 40f), end = Offset(cx + 60f, cy - 40f), strokeWidth = 6f)
                drawLine(color = Color(0xFF64748B), start = Offset(cx - 40f, cy - 40f), end = Offset(cx - 40f, cy + 30f), strokeWidth = 5f)
                drawLine(color = Color(0xFF64748B), start = Offset(cx + 40f, cy - 40f), end = Offset(cx + 40f, cy + 30f), strokeWidth = 5f)
            }
            category.contains("Wardrobe", ignoreCase = true) || category.contains("PVC", ignoreCase = true) -> {
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cx - 45f, cy - 45f),
                    size = androidx.compose.ui.geometry.Size(90f, 85f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                drawLine(color = Color(0xFFCBD5E1), start = Offset(cx, cy - 45f), end = Offset(cx, cy + 40f), strokeWidth = 2f)
                drawCircle(color = Color(0xFFD97706), radius = 3f, center = Offset(cx - 8f, cy - 5f))
                drawCircle(color = Color(0xFFD97706), radius = 3f, center = Offset(cx + 8f, cy - 5f))
            }
            else -> {
                // Chair / Interior
                drawRoundRect(
                    color = Color(0xFF1D4ED8),
                    topLeft = Offset(cx - 35f, cy - 35f),
                    size = androidx.compose.ui.geometry.Size(70f, 55f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = Color(0xFF3B82F6),
                    topLeft = Offset(cx - 30f, cy + 5f),
                    size = androidx.compose.ui.geometry.Size(60f, 20f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
                drawLine(color = Color(0xFFF59E0B), start = Offset(cx - 25f, cy + 25f), end = Offset(cx - 32f, cy + 45f), strokeWidth = 3f)
                drawLine(color = Color(0xFFF59E0B), start = Offset(cx + 25f, cy + 25f), end = Offset(cx + 32f, cy + 45f), strokeWidth = 3f)
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    onClick: () -> Unit,
    isHighlighted: Boolean = false
) {
    Surface(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isHighlighted) RoyalBluePrimary else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (isHighlighted) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) Color.White else Color(0xFF1E293B),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}
