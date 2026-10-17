package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.ui.components.AdminSidebar
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockPilotApp(
    viewModel: StockPilotViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val rawProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val bestSellers by viewModel.bestSellingProducts.collectAsStateWithLifecycle()
    val monthlyTrend by viewModel.monthlyTrend.collectAsStateWithLifecycle()
    val stockValueTrend by viewModel.stockValueTrend.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStockStatus.collectAsStateWithLifecycle()

    val editingProductId by viewModel.editingProductId.collectAsStateWithLifecycle()
    val prefillStockProductId by viewModel.prefillStockProductId.collectAsStateWithLifecycle()

    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val isSnackbarError by viewModel.isSnackbarError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Handle back button: if not on dashboard or login, go back to dashboard
    BackHandler(enabled = isLoggedIn && currentScreen != NavScreen.DASHBOARD) {
        viewModel.navigateTo(NavScreen.DASHBOARD)
    }

    // React to snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { email, pass ->
                viewModel.login(email, pass)
            }
        )
        return
    }

    // Adaptive container
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isDesktopOrTablet = maxWidth >= 840.dp

        if (isDesktopOrTablet) {
            // Desktop-first layout: Persistent Sidebar on left + Content on right
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = if (isSnackbarError) DangerRedDark else Slate800,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AdminSidebar(
                        currentScreen = currentScreen,
                        lowStockCount = lowStockProducts.size,
                        currentUser = currentUser,
                        onNavigate = { screen -> viewModel.navigateTo(screen) },
                        onLogout = { viewModel.logout() }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Slate50)
                    ) {
                        ScreenContent(
                            currentScreen = currentScreen,
                            viewModel = viewModel,
                            products = products,
                            rawProducts = rawProducts,
                            suppliers = suppliers,
                            transactions = transactions,
                            recentTransactions = recentTransactions,
                            lowStockProducts = lowStockProducts,
                            metrics = metrics,
                            bestSellers = bestSellers,
                            monthlyTrend = monthlyTrend,
                            stockValueTrend = stockValueTrend,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            selectedStatus = selectedStatus,
                            editingProductId = editingProductId,
                            prefillStockProductId = prefillStockProductId
                        )
                    }
                }
            }
        } else {
            // Compact layout (Phone): Drawer for sidebar
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = Slate900,
                        modifier = Modifier.width(280.dp)
                    ) {
                        AdminSidebar(
                            currentScreen = currentScreen,
                            lowStockCount = lowStockProducts.size,
                            currentUser = currentUser,
                            onNavigate = { screen ->
                                viewModel.navigateTo(screen)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onLogout = {
                                coroutineScope.launch { drawerState.close() }
                                viewModel.logout()
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = currentScreen.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { coroutineScope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("appbar_drawer_toggle")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open Navigation Menu",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = if (isSnackbarError) DangerRedDark else Slate800,
                                contentColor = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Slate50)
                    ) {
                        ScreenContent(
                            currentScreen = currentScreen,
                            viewModel = viewModel,
                            products = products,
                            rawProducts = rawProducts,
                            suppliers = suppliers,
                            transactions = transactions,
                            recentTransactions = recentTransactions,
                            lowStockProducts = lowStockProducts,
                            metrics = metrics,
                            bestSellers = bestSellers,
                            monthlyTrend = monthlyTrend,
                            stockValueTrend = stockValueTrend,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            selectedStatus = selectedStatus,
                            editingProductId = editingProductId,
                            prefillStockProductId = prefillStockProductId
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    currentScreen: NavScreen,
    viewModel: StockPilotViewModel,
    products: List<Product>,
    rawProducts: List<Product>,
    suppliers: List<com.example.data.model.Supplier>,
    transactions: List<com.example.data.model.StockTransaction>,
    recentTransactions: List<com.example.data.model.StockTransaction>,
    lowStockProducts: List<Product>,
    metrics: DashboardMetrics,
    bestSellers: List<BestSellingItem>,
    monthlyTrend: List<MonthlyComparison>,
    stockValueTrend: List<Pair<String, Double>>,
    searchQuery: String,
    selectedCategory: String?,
    selectedStatus: com.example.data.model.StockStatus?,
    editingProductId: Long?,
    prefillStockProductId: Long?
) {
    when (currentScreen) {
        NavScreen.LOGIN -> {
            LoginScreen(
                onLoginSuccess = { email, pass -> viewModel.login(email, pass) }
            )
        }
        NavScreen.DASHBOARD -> {
            DashboardScreen(
                metrics = metrics,
                lowStockProducts = lowStockProducts,
                recentTransactions = recentTransactions,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        }
        NavScreen.PRODUCTS -> {
            ProductsScreen(
                products = products,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                selectedStatus = selectedStatus,
                onSearchChange = { viewModel.searchQuery.value = it },
                onCategoryChange = { viewModel.selectedCategory.value = it },
                onStatusChange = { viewModel.selectedStockStatus.value = it },
                onAddProduct = { viewModel.navigateTo(NavScreen.ADD_EDIT_PRODUCT) },
                onEditProduct = { id -> viewModel.navigateTo(NavScreen.ADD_EDIT_PRODUCT, id) },
                onStockIn = { id -> viewModel.navigateTo(NavScreen.STOCK_IN, id) },
                onStockOut = { id -> viewModel.navigateTo(NavScreen.STOCK_OUT, id) },
                onDeleteProduct = { prod -> viewModel.deleteProduct(prod) }
            )
        }
        NavScreen.ADD_EDIT_PRODUCT -> {
            val existing = rawProducts.find { it.id == editingProductId }
            AddEditProductScreen(
                productId = editingProductId,
                existingProduct = existing,
                suppliers = suppliers,
                onSaveProduct = { id, sku, name, cat, unitP, costP, qty, threshold, supId, supName, icon ->
                    viewModel.saveProduct(id, sku, name, cat, unitP, costP, qty, threshold, supId, supName, icon)
                },
                onCancel = { viewModel.navigateTo(NavScreen.PRODUCTS) }
            )
        }
        NavScreen.STOCK_IN -> {
            StockInScreen(
                products = rawProducts,
                suppliers = suppliers,
                prefillProductId = prefillStockProductId,
                onRecordPurchase = { prodId, qty, supName, costP, ref, notes ->
                    viewModel.recordPurchase(prodId, qty, supName, costP, ref, notes)
                },
                onCancel = { viewModel.navigateTo(NavScreen.DASHBOARD) }
            )
        }
        NavScreen.STOCK_OUT -> {
            StockOutScreen(
                products = rawProducts,
                prefillProductId = prefillStockProductId,
                onRecordSale = { prodId, qty, saleP, custName, ref, notes ->
                    viewModel.recordSale(prodId, qty, saleP, custName, ref, notes)
                },
                onCancel = { viewModel.navigateTo(NavScreen.DASHBOARD) }
            )
        }
        NavScreen.SUPPLIERS -> {
            SuppliersScreen(
                suppliers = suppliers,
                products = rawProducts,
                onSaveSupplier = { id, name, contact, email, phone, addr, cats ->
                    viewModel.saveSupplier(id, name, contact, email, phone, addr, cats)
                },
                onDeleteSupplier = { sup -> viewModel.deleteSupplier(sup) },
                onViewProduct = { prodId -> viewModel.navigateTo(NavScreen.ADD_EDIT_PRODUCT, prodId) }
            )
        }
        NavScreen.LOW_STOCK -> {
            LowStockScreen(
                lowStockProducts = lowStockProducts,
                onReorderProduct = { prod -> viewModel.reorderProduct(prod) }
            )
        }
        NavScreen.REPORTS -> {
            ReportsScreen(
                stockValuePoints = stockValueTrend,
                monthlyData = monthlyTrend,
                bestSellingItems = bestSellers,
                products = rawProducts,
                transactions = transactions,
                onExportAuditLog = {
                    viewModel.navigateTo(NavScreen.REPORTS)
                }
            )
        }
    }
}
