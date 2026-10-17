package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.StockTransaction
import com.example.ui.BestSellingItem
import com.example.ui.MonthlyComparison
import com.example.ui.components.BestSellingLeaderboard
import com.example.ui.components.PurchasesVsSalesChart
import com.example.ui.components.StockValueTrendChart
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ReportsScreen(
    stockValuePoints: List<Pair<String, Double>>,
    monthlyData: List<MonthlyComparison>,
    bestSellingItems: List<BestSellingItem>,
    products: List<Product>,
    transactions: List<StockTransaction>,
    onExportAuditLog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("Last 30 Days") }
    val timeframes = listOf("Last 7 Days", "Last 30 Days", "Quarter to Date", "Full Year")

    val totalStockVal = products.sumOf { it.quantityOnHand * it.unitPrice }
    val totalPurchasesVal = transactions.filter { it.type == "IN" }.sumOf { it.totalAmount }
    val totalSalesVal = transactions.filter { it.type == "OUT" }.sumOf { it.totalAmount }
    val netMarginVal = totalSalesVal - totalPurchasesVal

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Inventory Intelligence & Reports",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Slate900
                    )
                    Text(
                        text = "Warehouse asset valuation trends, purchase/sales ratios, and best-sellers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onExportAuditLog,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_reports_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV")
                    }
                }
            }
        }

        // Timeframe selector chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timeframes.forEach { tf ->
                    val isSelected = selectedTimeframe == tf
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTimeframe = tf },
                        label = { Text(tf) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Slate700,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceWhite
                        )
                    )
                }
            }
        }

        // 4 KPI Summary Pill Cards
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CURRENT VALUATION", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${String.format(Locale.US, "%,.2f", totalStockVal)}", style = MaterialTheme.typography.titleLarge, color = Slate900)
                    }

                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Slate200))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL PURCHASES (IN)", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${String.format(Locale.US, "%,.2f", totalPurchasesVal)}", style = MaterialTheme.typography.titleLarge, color = Slate700)
                    }

                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Slate200))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL SALES (OUT)", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${String.format(Locale.US, "%,.2f", totalSalesVal)}", style = MaterialTheme.typography.titleLarge, color = SuccessGreenDark)
                    }

                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Slate200))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("OPERATIONAL DELTA", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (netMarginVal >= 0) "+$" else "-$"}${String.format(Locale.US, "%,.2f", kotlin.math.abs(netMarginVal))}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (netMarginVal >= 0) SuccessGreenDark else DangerRedDark
                        )
                    }
                }
            }
        }

        // Chart 1: Stock Value Over Time
        item {
            StockValueTrendChart(
                points = stockValuePoints,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Chart 2: Purchases vs Sales Monthly Trend Chart
        item {
            PurchasesVsSalesChart(
                data = monthlyData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Chart 3: Best-Selling Products Leaderboard
        item {
            BestSellingLeaderboard(
                items = bestSellingItems,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
