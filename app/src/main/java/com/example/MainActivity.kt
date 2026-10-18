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
import androidx.compose.material.icons.filled.Menu
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
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
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

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        MainDashboardScaffold(
            onSignOut = {
                AuthManager.signOut(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {
                        currentUser = null
                    },
                    scope = coroutineScope
                )
            }
        )
    }
}

@Composable
fun MainDashboardScaffold(onSignOut: () -> Unit) {
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

    // Intercept back button to navigate to home if in a subscreen
    BackHandler(enabled = currentScreen != "home") {
        currentScreen = "home"
    }

    val screenTitle = when (currentScreen) {
        "home" -> "ARAVALLI STEEL PVC"
        "admin" -> "व्यवस्थापक पैनल (Admin Panel)"
        "products" -> "उत्पाद एवं स्टॉक (Products)"
        "tools" -> "माप कैलकुलेटर (Calculators)"
        "orders" -> "ऑर्डर और प्रोजेक्ट (Orders)"
        "estimates" -> "कोटेशन सूची (Quotations)"
        "customers" -> "ग्राहक एवं विजिटर (Customers)"
        "attendance" -> "कारीगर हाजिरी (Attendance)"
        "finance" -> "दुकान रोकड़ बही (Cashbook)"
        "catalogue" -> "डिजिटल कैटलॉग (Catalogue)"
        "ai_tools" -> "अरावल्ली जेमिनी AI टूल्स"
        "settings" -> "व्यापार सेटिंग्स (Settings)"
        else -> "मेनू और मॉड्यूल (Menu)"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
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
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyPrimary,
                contentColor = Color.White
            ) {
                val isHi = currentLang == "hi"
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(if (isHi) "होम" else "Home", fontSize = 11.sp) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = currentScreen == "products",
                    onClick = { currentScreen = "products" },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = "Products") },
                    label = { Text(if (isHi) "उत्पाद" else "Products", fontSize = 11.sp) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = currentScreen == "tools",
                    onClick = { currentScreen = "tools" },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Tools") },
                    label = { Text(if (isHi) "टूल्स" else "Tools", fontSize = 11.sp) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = currentScreen == "orders",
                    onClick = { currentScreen = "orders" },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Orders") },
                    label = { Text(if (isHi) "ऑर्डर" else "Orders", fontSize = 11.sp) },
                    colors = navItemColors()
                )
                NavigationBarItem(
                    selected = currentScreen in listOf("more", "admin", "estimates", "customers", "attendance", "finance", "catalogue", "settings", "ai_tools"),
                    onClick = { currentScreen = "more" },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                    label = { Text(if (isHi) "मेनू" else "Menu", fontSize = 11.sp) },
                    colors = navItemColors()
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                    onNavigateToAdmin = { currentScreen = "admin" }
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

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = NavyPrimary,
    selectedTextColor = GoldAccent,
    indicatorColor = GoldAccent,
    unselectedIconColor = Color.LightGray,
    unselectedTextColor = Color.LightGray
)

@Composable
fun MoreMenuScreen(
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "सभी व्यापार मॉड्यूल (All Business Modules)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        item {
            MenuItemCard(
                title = "🛡️ अरावल्ली एडमिन कंट्रोल सेंटर (Admin Panel)",
                subtitle = "स्टाफ व कारीगर, मास्टर दर सूची, जीएसटी, ऑर्डर अनुमोदन व सिस्टम डेटा नियंत्रण",
                icon = Icons.Default.AdminPanelSettings,
                onClick = { onNavigate("admin") }
            )
        }

        item {
            MenuItemCard(
                title = "✨ अरावल्ली जेमिनी AI टूल्स (AI Marketing & Design)",
                subtitle = "AI पोस्टर, उत्पाद विवरण, बहुभाषी कैप्शन, और ग्राहक चैट सहायक",
                icon = Icons.Default.AutoAwesome,
                onClick = { onNavigate("ai_tools") }
            )
        }

        item {
            MenuItemCard(
                title = "कोटेशन एवं प्राक्कलन (Estimates & Quotations)",
                subtitle = "माप जोड़ें, जीएसटी और एडवांस के साथ कोटेशन बनाएं",
                icon = Icons.Default.Description,
                onClick = { onNavigate("estimates") }
            )
        }
        item {
            MenuItemCard(
                title = "ग्राहक एवं साइट विजिट (Customers & Site Visits)",
                subtitle = "पूछताछ, विजिटर रजिस्टर और फॉलो-अप ट्रैकिंग",
                icon = Icons.Default.Group,
                onClick = { onNavigate("customers") }
            )
        }
        item {
            MenuItemCard(
                title = "कारीगर हाजिरी एवं वेतन (Worker Attendance & Wages)",
                subtitle = "दैनिक उपस्थिति, ओवरटाइम और एडवांस वेतन पर्ची",
                icon = Icons.Default.Engineering,
                onClick = { onNavigate("attendance") }
            )
        }
        item {
            MenuItemCard(
                title = "दुकान रोकड़ बही (Finance & Cashbook)",
                subtitle = "दैनिक आय, व्यय, बिल और नकद/यूपीआई लेन-देन",
                icon = Icons.Default.AccountBalanceWallet,
                onClick = { onNavigate("finance") }
            )
        }
        item {
            MenuItemCard(
                title = "डिजिटल कैटलॉग एवं पोस्टर (Catalogue & Marketing)",
                subtitle = "ग्राहकों को व्हाट्सएप पर भेजने हेतु उत्पाद प्रचार संदेश",
                icon = Icons.Default.Campaign,
                onClick = { onNavigate("catalogue") }
            )
        }
        item {
            MenuItemCard(
                title = "व्यापार सेटिंग्स व APK गाइड (Settings & APK Guide)",
                subtitle = "दुकान का पता, फोन, जीएसटी, भाषा और APK डाउनलोड निर्देश",
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(NavySecondary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
