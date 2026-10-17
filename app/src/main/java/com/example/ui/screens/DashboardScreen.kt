package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.StockTransaction
import com.example.ui.DashboardMetrics
import com.example.ui.NavScreen
import com.example.ui.components.StatCard
import com.example.ui.components.TransactionTypeBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    metrics: DashboardMetrics,
    lowStockProducts: List<Product>,
    recentTransactions: List<StockTransaction>,
    onNavigate: (NavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Warehouse Dashboard",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Slate900
                    )
                    Text(
                        text = "Real-time stock valuation, order velocity, and alerts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )
                }

                // Quick Action Bar
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onNavigate(NavScreen.STOCK_IN) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate700),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate300),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dashboard_stock_in_btn")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Stock In", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(NavScreen.STOCK_OUT) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate700),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate300),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dashboard_stock_out_btn")
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Stock Out", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = { onNavigate(NavScreen.ADD_EDIT_PRODUCT) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dashboard_add_product_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add Product", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
            }
        }

        // Low-stock alert banner
        if (metrics.lowStockCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onNavigate(NavScreen.LOW_STOCK) }
                        .testTag("low_stock_alert_banner"),
                    colors = CardDefaults.cardColors(containerColor = AccentOrangeLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Alert",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Inventory Reorder Alert: ${metrics.lowStockCount} items below threshold",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AccentOrangeDark
                                )
                                Text(
                                    text = "Critical stock deficit detected. Review reorder list to avoid stockout.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigate(NavScreen.LOW_STOCK) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Review & Restock",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4 Stat Cards
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth > 700.dp
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "TOTAL PRODUCTS",
                            value = "${metrics.totalProducts}",
                            subtitle = "SKUs in catalog",
                            icon = Icons.Default.Inventory2,
                            iconBgColor = Slate100,
                            iconTintColor = Slate700,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(NavScreen.PRODUCTS) }
                        )
                        StatCard(
                            title = "TOTAL STOCK VALUE",
                            value = "$${String.format(Locale.US, "%,.2f", metrics.totalStockValue)}",
                            subtitle = "Warehouse assets",
                            icon = Icons.Default.AttachMoney,
                            iconBgColor = Color(0xFFEFF6FF),
                            iconTintColor = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(NavScreen.REPORTS) }
                        )
                        StatCard(
                            title = "LOW STOCK ITEMS",
                            value = "${metrics.lowStockCount}",
                            subtitle = "Action required",
                            icon = Icons.Default.WarningAmber,
                            iconBgColor = AccentOrangeLight,
                            iconTintColor = AccentOrangeDark,
                            trendText = if (metrics.lowStockCount > 0) "Urgent" else "Optimal",
                            trendColor = if (metrics.lowStockCount > 0) AccentOrangeDark else SuccessGreenDark,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(NavScreen.LOW_STOCK) }
                        )
                        StatCard(
                            title = "TODAY'S SALES",
                            value = "$${String.format(Locale.US, "%,.2f", metrics.todaySales)}",
                            subtitle = "Recorded volume",
                            icon = Icons.Default.TrendingUp,
                            iconBgColor = SuccessGreenLight,
                            iconTintColor = SuccessGreenDark,
                            trendText = "+Healthy",
                            trendColor = SuccessGreenDark,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(NavScreen.STOCK_OUT) }
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "PRODUCTS",
                                value = "${metrics.totalProducts}",
                                subtitle = "Active SKUs",
                                icon = Icons.Default.Inventory2,
                                iconBgColor = Slate100,
                                iconTintColor = Slate700,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(NavScreen.PRODUCTS) }
                            )
                            StatCard(
                                title = "STOCK VALUE",
                                value = "$${String.format(Locale.US, "%,.0f", metrics.totalStockValue)}",
                                subtitle = "Assets",
                                icon = Icons.Default.AttachMoney,
                                iconBgColor = Color(0xFFEFF6FF),
                                iconTintColor = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(NavScreen.REPORTS) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "LOW STOCK",
                                value = "${metrics.lowStockCount}",
                                subtitle = "Needs Reorder",
                                icon = Icons.Default.WarningAmber,
                                iconBgColor = AccentOrangeLight,
                                iconTintColor = AccentOrangeDark,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(NavScreen.LOW_STOCK) }
                            )
                            StatCard(
                                title = "TODAY'S SALES",
                                value = "$${String.format(Locale.US, "%,.0f", metrics.todaySales)}",
                                subtitle = "Recorded",
                                icon = Icons.Default.TrendingUp,
                                iconBgColor = SuccessGreenLight,
                                iconTintColor = SuccessGreenDark,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(NavScreen.STOCK_OUT) }
                            )
                        }
                    }
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Inventory Movements",
                        style = MaterialTheme.typography.titleLarge,
                        color = Slate900
                    )
                    Text(
                        text = "Latest logged purchases (Stock In) and customer sales (Stock Out)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }

                TextButton(onClick = { onNavigate(NavScreen.REPORTS) }) {
                    Text("View Full Ledger", color = Slate700, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = Slate700)
                }
            }
        }

        // Transactions Feed Table
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (recentTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No stock transactions recorded yet.", color = Slate400)
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("DATE & TIME", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.3f))
                            Text("TYPE", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.2f))
                            Text("PRODUCT / SKU", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(2f))
                            Text("QTY", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(0.7f))
                            Text("TOTAL AMOUNT", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.2f))
                            Text("PARTY / REF", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.5f))
                        }

                        HorizontalDivider(color = Slate200, thickness = 1.dp)

                        recentTransactions.take(8).forEachIndexed { index, tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 1) Slate50 else SurfaceWhite)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateFormat.format(Date(tx.timestamp)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600,
                                    modifier = Modifier.weight(1.3f)
                                )

                                Box(modifier = Modifier.weight(1.2f)) {
                                    TransactionTypeBadge(type = tx.type)
                                }

                                Column(modifier = Modifier.weight(2f)) {
                                    Text(
                                        text = tx.productName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Slate900,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = tx.productSku,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                }

                                Text(
                                    text = "${if (tx.type == "IN") "+" else "-"}${tx.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (tx.type == "IN") Color(0xFF2563EB) else SuccessGreenDark,
                                    modifier = Modifier.weight(0.7f)
                                )

                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", tx.totalAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate900,
                                    modifier = Modifier.weight(1.2f)
                                )

                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        text = tx.partyName.ifBlank { "-" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate700,
                                        maxLines = 1
                                    )
                                    if (tx.referenceNo.isNotBlank()) {
                                        Text(
                                            text = tx.referenceNo,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400
                                        )
                                    }
                                }
                            }
                            if (index < recentTransactions.size - 1) {
                                HorizontalDivider(color = Slate100, thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
