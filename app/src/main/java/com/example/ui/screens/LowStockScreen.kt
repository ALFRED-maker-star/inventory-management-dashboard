package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.ui.components.StockStatusBadge
import com.example.ui.theme.*

@Composable
fun LowStockScreen(
    lowStockProducts: List<Product>,
    onReorderProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Low Stock & Reorder Queue",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Slate900
                )
                Text(
                    text = "Inventory items currently below minimum safety thresholds requiring purchase requisitions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }

            if (lowStockProducts.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentOrangeLight)
                        .border(1.dp, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${lowStockProducts.size} SKUs Require Restock",
                        style = MaterialTheme.typography.titleSmall,
                        color = AccentOrangeDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Table Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (lowStockProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SuccessGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreenDark,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "All Inventory Levels Healthy!",
                            style = MaterialTheme.typography.titleLarge,
                            color = Slate900
                        )
                        Text(
                            text = "No products are currently below their designated reorder thresholds.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate500
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PRODUCT & SKU", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(2f))
                        Text("CATEGORY", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.1f))
                        Text("ON HAND / MIN", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.2f))
                        Text("DEFICIT", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1f))
                        Text("SUPPLIER", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.5f))
                        Text("STATUS", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1f))
                        Text("QUICK ACTION", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.4f))
                    }

                    HorizontalDivider(color = Slate200, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(lowStockProducts, key = { it.id }) { product ->
                            val deficit = (product.reorderThreshold - product.quantityOnHand).coerceAtLeast(0)
                            val suggestedReorder = ((product.reorderThreshold * 2) - product.quantityOnHand).coerceAtLeast(10)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product Name & SKU
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Slate900,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = product.sku,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                }

                                // Category
                                Box(modifier = Modifier.weight(1.1f)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Slate100)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = product.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )
                                    }
                                }

                                // On Hand vs Min
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(
                                        text = "${product.quantityOnHand} / ${product.reorderThreshold}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (product.quantityOnHand <= 0) DangerRedDark else AccentOrangeDark
                                    )
                                    Text(
                                        text = "Safety min: ${product.reorderThreshold}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                }

                                // Deficit
                                Text(
                                    text = "-$deficit units",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )

                                // Supplier
                                Text(
                                    text = product.supplierName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate700,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1.5f)
                                )

                                // Status
                                Box(modifier = Modifier.weight(1f)) {
                                    StockStatusBadge(status = product.stockStatus)
                                }

                                // Quick Reorder Action
                                Box(modifier = Modifier.weight(1.4f)) {
                                    Button(
                                        onClick = { onReorderProduct(product) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("reorder_button_${product.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Reorder (+$suggestedReorder)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = Slate100, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}
