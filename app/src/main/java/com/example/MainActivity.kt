package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Product
import com.example.ui.auth.AuthManager
import com.example.ui.components.AravalliTopAppBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CatalogueScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.EstimatesScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.WelcomeOnboardingScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SteelLight
import com.example.ui.viewmodel.AravalliViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AppRootContent()
            }
        }
    }
}

@Composable
fun AppRootContent() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isDemoLoggedIn by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { currentUser = Firebase.auth.currentUser },
            onUnauthenticated = { /* stay on AuthScreen */ },
            scope = coroutineScope
        )
    }

    when {
        showOnboarding -> {
            WelcomeOnboardingScreen(
                onGetStarted = { showOnboarding = false },
                onSkipToLogin = { showOnboarding = false }
            )
        }
        currentUser == null && !isDemoLoggedIn -> {
            AuthScreen(
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                },
                onDemoLogin = {
                    isDemoLoggedIn = true
                }
            )
        }
        else -> {
            MainDashboardScaffold(
                onSignOut = {
                    isDemoLoggedIn = false
                    AuthManager.signOut(
                        context = context,
                        credentialManager = credentialManager,
                        onSignOutComplete = {
                            currentUser = null
                        },
                        scope = coroutineScope
                    )
                },
                onShowOnboarding = { showOnboarding = true }
            )
        }
    }
}

@Composable
fun MainDashboardScaffold(
    onSignOut: () -> Unit,
    onShowOnboarding: () -> Unit = {}
) {
    val viewModel: AravalliViewModel = viewModel()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    var currentScreen by remember { mutableStateOf("home") }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }

    // Intercept back button to navigate to home or dismiss detail view
    BackHandler(enabled = selectedProductForDetail != null || currentScreen != "home") {
        if (selectedProductForDetail != null) {
            selectedProductForDetail = null
        } else {
            currentScreen = "home"
        }
    }

    val screenTitle = when {
        selectedProductForDetail != null -> selectedProductForDetail?.name ?: "उत्पाद विवरण (Details)"
        currentScreen == "home" -> "ARAVALLI STEEL PVC"
        currentScreen == "admin" -> "व्यवस्थापक पैनल (Admin Panel)"
        currentScreen == "products" -> "उत्पाद सूची (Products)"
        currentScreen == "tools" -> "माप कैलकुलेटर (Calculators)"
        currentScreen == "orders" -> "ऑर्डर और प्रोजेक्ट (Orders)"
        currentScreen == "estimates" -> "कोटेशन सूची (Quotations)"
        currentScreen == "customers" -> "ग्राहक एवं विजिटर (Customers)"
        currentScreen == "attendance" -> "कारीगर हाजिरी (Attendance)"
        currentScreen == "finance" -> "दुकान रोकड़ बही (Cashbook)"
        currentScreen == "catalogue" -> "डिजिटल कैटलॉग (Catalogue)"
        currentScreen == "ai_tools" -> "अरावल्ली जेमिनी AI टूल्स"
        currentScreen == "settings" -> "व्यापार सेटिंग्स व अकाउंट (Account)"
        else -> "मेनू और मॉड्यूल (Menu)"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (selectedProductForDetail == null) {
                AravalliTopAppBar(
                    title = screenTitle,
                    subtitle = "श्याम आर्केड, मेघराज रोड, मोडासा",
                    currentLang = currentLang,
                    onToggleLang = {
                        val nextLang = if (currentLang == "hi") "en" else "hi"
                        viewModel.setLanguage(nextLang)
                    },
                    onAdminClick = { currentScreen = "admin" }
                )
            }
        },
        bottomBar = {
            if (selectedProductForDetail == null) {
                // EXACT BOTTOM NAVIGATION REQUESTED: Home | Catalog | Estimate | Orders | Account
                NavigationBar(
                    containerColor = RoyalBluePrimary,
                    contentColor = Color.White
                ) {
                    val isHi = currentLang == "hi"

                    // 1. Home
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(if (isHi) "होम" else "Home", fontSize = 11.sp) },
                        colors = navItemColors()
                    )

                    // 2. Catalog
                    NavigationBarItem(
                        selected = currentScreen == "catalogue" || currentScreen == "products",
                        onClick = { currentScreen = "catalogue" },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "Catalog") },
                        label = { Text(if (isHi) "कैटलॉग" else "Catalog", fontSize = 11.sp) },
                        colors = navItemColors()
                    )

                    // 3. Estimate
                    NavigationBarItem(
                        selected = currentScreen == "estimates" || currentScreen == "tools",
                        onClick = { currentScreen = "estimates" },
                        icon = { Icon(Icons.Default.Description, contentDescription = "Estimate") },
                        label = { Text(if (isHi) "कोटेशन" else "Estimate", fontSize = 11.sp) },
                        colors = navItemColors()
                    )

                    // 4. Orders
                    NavigationBarItem(
                        selected = currentScreen == "orders",
                        onClick = { currentScreen = "orders" },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = "Orders") },
                        label = { Text(if (isHi) "ऑर्डर" else "Orders", fontSize = 11.sp) },
                        colors = navItemColors()
                    )

                    // 5. Account (Settings, WhatsApp, Business Info)
                    NavigationBarItem(
                        selected = currentScreen == "settings" || currentScreen == "more" || currentScreen == "admin",
                        onClick = { currentScreen = "settings" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                        label = { Text(if (isHi) "अकाउंट" else "Account", fontSize = 11.sp) },
                        colors = navItemColors()
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (selectedProductForDetail != null) PaddingValues(0.dp) else innerPadding)
        ) {
            if (selectedProductForDetail != null) {
                ProductDetailScreen(
                    product = selectedProductForDetail!!,
                    onNavigateBack = { selectedProductForDetail = null },
                    onGetQuote = {
                        selectedProductForDetail = null
                        currentScreen = "estimates"
                    }
                )
            } else {
                when (currentScreen) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToEstimates = { currentScreen = "estimates" },
                        onNavigateToOrders = { currentScreen = "orders" },
                        onNavigateToCustomers = { currentScreen = "customers" },
                        onNavigateToAttendance = { currentScreen = "attendance" },
                        onNavigateToTools = { currentScreen = "tools" },
                        onNavigateToProducts = { currentScreen = "products" },
                        onNavigateToCatalogue = { currentScreen = "catalogue" },
                        onNavigateToAiTools = { currentScreen = "ai_tools" },
                        onNavigateToAdmin = { currentScreen = "admin" },
                        onNavigateToProductDetail = { prod -> selectedProductForDetail = prod }
                    )
                    "admin" -> AdminPanelScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = "home" }
                    )
                    "products" -> ProductsScreen(viewModel = viewModel)
                    "tools" -> ToolsScreen(
                        viewModel = viewModel,
                        onNavigateToEstimates = { currentScreen = "estimates" }
                    )
                    "orders" -> OrdersScreen(viewModel = viewModel)
                    "estimates" -> EstimatesScreen(viewModel = viewModel)
                    "customers" -> CustomersScreen(viewModel = viewModel)
                    "attendance" -> AttendanceScreen(viewModel = viewModel)
                    "finance" -> FinanceScreen(viewModel = viewModel)
                    "catalogue" -> CatalogueScreen(viewModel = viewModel)
                    "ai_tools" -> AiToolsScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(
                        viewModel = viewModel,
                        onSignOutClicked = onSignOut
                    )
                    "more" -> MoreMenuScreen(
                        onNavigate = { screen -> currentScreen = screen },
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = RoyalBluePrimary,
    selectedTextColor = Color.White,
    indicatorColor = Color.White,
    unselectedIconColor = Color.White.copy(alpha = 0.7f),
    unselectedTextColor = Color.White.copy(alpha = 0.7f)
)

@Composable
fun MoreMenuScreen(
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("more_menu_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MenuItemCard(
                title = "🛡️ अरावल्ली एडमिन कंट्रोल सेंटर (Admin Panel)",
                subtitle = "स्टाफ व कारीगर, मास्टर रेट्स व जीएसटी, ऑर्डर अप्रूवल, डेटा बैकअप व स्टोर सेटिंग्स",
                icon = Icons.Default.AdminPanelSettings,
                iconTint = GoldAccent,
                onClick = { onNavigate("admin") }
            )
        }

        item {
            MenuItemCard(
                title = "✨ अरावल्ली जेमिनी AI टूल्स (AI Marketing & Design)",
                subtitle = "AI पोस्टर, उत्पाद विवरण, बहुभाषी विज्ञापन और बिक्री सहायक",
                icon = Icons.Default.AutoAwesome,
                iconTint = GoldAccent,
                onClick = { onNavigate("ai_tools") }
            )
        }

        item {
            MenuItemCard(
                title = "👥 ग्राहक एवं विजिटर लीड्स (Customers)",
                subtitle = "नए ग्राहक, फोन नंबर, पता, साइट माप और प्रोजेक्ट स्थिति",
                icon = Icons.Default.Group,
                onClick = { onNavigate("customers") }
            )
        }

        item {
            MenuItemCard(
                title = "👷 कारीगर हाजिरी व मजदूरी (Worker Attendance)",
                subtitle = "दैनिक हाजिरी, ओवर-टाइम, एडवांस भुगतान और वेतन गणना",
                icon = Icons.Default.Engineering,
                onClick = { onNavigate("attendance") }
            )
        }

        item {
            MenuItemCard(
                title = "💰 दुकान रोकड़ बही (Cashbook & Finance)",
                subtitle = "दैनिक आय, खर्च, सामग्री खरीद और शुद्ध लाभ रिपोर्ट",
                icon = Icons.Default.AccountBalanceWallet,
                onClick = { onNavigate("finance") }
            )
        }

        item {
            MenuItemCard(
                title = "📖 डिजिटल कैटलॉग व डिजाइन (Catalogue)",
                subtitle = "ग्राहक को फोटो दिखाने और शेयर करने के लिए उत्पाद गैलरी",
                icon = Icons.Default.Inventory,
                onClick = { onNavigate("catalogue") }
            )
        }

        item {
            MenuItemCard(
                title = "⚙️ व्यापार सेटिंग्स व प्रोफाइल (Business Settings)",
                subtitle = "जीएसटी नंबर, दुकान का पता, नियम व शर्तें और बैकअप",
                icon = Icons.Default.Settings,
                onClick = { onNavigate("settings") }
            )
        }
    }
}

@Composable
fun MenuItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = NavyPrimary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = SteelLight
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
