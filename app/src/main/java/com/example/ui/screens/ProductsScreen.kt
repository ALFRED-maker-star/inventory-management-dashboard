package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.StockStatus
import com.example.ui.NavScreen
import com.example.ui.components.StockStatusBadge
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ProductsScreen(
    products: List<Product>,
    searchQuery: String,
    selectedCategory: String?,
    selectedStatus: StockStatus?,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onStatusChange: (StockStatus?) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit,
    onStockIn: (Long) -> Unit,
    onStockOut: (Long) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // Derive categories from products
    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct().sorted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Product Inventory",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Slate900
                )
                Text(
                    text = "Manage SKU catalog, price points, and active inventory levels",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }

            Button(
                onClick = onAddProduct,
                colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_product_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ Add Product", style = MaterialTheme.typography.labelMedium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search & Filter Toolbar Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Slate200, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("products_search_input"),
                        placeholder = { Text("Search by product name, SKU, or supplier...", color = Slate400) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate400)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Slate700,
                            unfocusedBorderColor = Slate200
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips: Category and Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )

                    categories.forEach { cat ->
                        val isSelected = (cat == "All" && selectedCategory == null) || selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryChange(if (cat == "All") null else cat) },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Slate700,
                                selectedLabelColor = Color.White,
                                containerColor = Slate100,
                                labelColor = Slate700
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(Slate300)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )

                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onStatusChange(null) },
                        label = { Text("All Statuses") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Slate700,
                            selectedLabelColor = Color.White
                        )
                    )

                    StockStatus.values().forEach { status ->
                        val isSelected = selectedStatus == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { onStatusChange(if (isSelected) null else status) },
                            label = { Text(status.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (status) {
                                    StockStatus.IN_STOCK -> SuccessGreen
                                    StockStatus.LOW_STOCK -> AccentOrange
                                    StockStatus.OUT_OF_STOCK -> DangerRed
                                },
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Table Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No products found matching filters",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate700
                        )
                        Text(
                            text = "Try adjusting your search query or reset the category filter",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = {
                            onSearchChange("")
                            onCategoryChange(null)
                            onStatusChange(null)
                        }) {
                            Text("Reset All Filters")
                        }
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
                        Text("SKU", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1f))
                        Text("PRODUCT NAME", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(2f))
                        Text("CATEGORY", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.1f))
                        Text("ON HAND", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1f))
                        Text("UNIT PRICE", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1f))
                        Text("STATUS", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.1f))
                        Text("ACTIONS", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.4f))
                    }

                    HorizontalDivider(color = Slate200, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(products, key = { it.id }) { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditProduct(product.id) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // SKU
                                Text(
                                    text = product.sku,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Slate700,
                                    modifier = Modifier.weight(1f)
                                )

                                // Name & Supplier
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Slate900,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Supplier: ${product.supplierName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400,
                                        maxLines = 1
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

                                // Quantity on hand
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${product.quantityOnHand} units",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when {
                                            product.quantityOnHand <= 0 -> DangerRedDark
                                            product.isLowStock -> AccentOrangeDark
                                            else -> Slate900
                                        }
                                    )
                                    Text(
                                        text = "Min: ${product.reorderThreshold}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                }

                                // Unit Price
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "$${String.format(Locale.US, "%.2f", product.unitPrice)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "Cost: $${String.format(Locale.US, "%.2f", product.costPrice)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                }

                                // Status Badge
                                Box(modifier = Modifier.weight(1.1f)) {
                                    StockStatusBadge(status = product.stockStatus)
                                }

                                // Actions
                                Row(
                                    modifier = Modifier.weight(1.4f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onStockIn(product.id) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("stock_in_item_${product.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddShoppingCart,
                                            contentDescription = "Stock In",
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onStockOut(product.id) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("stock_out_item_${product.id}"),
                                        enabled = product.quantityOnHand > 0
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PointOfSale,
                                            contentDescription = "Stock Out",
                                            tint = if (product.quantityOnHand > 0) SuccessGreenDark else Slate300,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEditProduct(product.id) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("edit_product_${product.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Product",
                                            tint = Slate600,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { productToDelete = product },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("delete_product_${product.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Product",
                                            tint = DangerRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = Slate100, thickness = 1.dp)
                        }
                    }

                    // Table Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing ${products.size} products",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                        val totalVal = products.sumOf { it.quantityOnHand * it.unitPrice }
                        Text(
                            text = "Filtered Valuation: $${String.format(Locale.US, "%,.2f", totalVal)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate700
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product SKU: ${product.sku}?") },
            text = { Text("Are you sure you want to permanently delete \"${product.name}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(product)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
