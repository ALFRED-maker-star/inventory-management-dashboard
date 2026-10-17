package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.Supplier
import com.example.ui.theme.*
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productId: Long?,
    existingProduct: Product?,
    suppliers: List<Supplier>,
    onSaveProduct: (
        id: Long,
        sku: String,
        name: String,
        category: String,
        unitPrice: Double,
        costPrice: Double,
        quantityOnHand: Int,
        reorderThreshold: Int,
        supplierId: Long,
        supplierName: String,
        iconName: String
    ) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(existingProduct) { mutableStateOf(existingProduct?.name ?: "") }
    var sku by remember(existingProduct) { mutableStateOf(existingProduct?.sku ?: "") }
    var category by remember(existingProduct) { mutableStateOf(existingProduct?.category ?: "Hardware") }
    var unitPriceStr by remember(existingProduct) { mutableStateOf(existingProduct?.unitPrice?.toString() ?: "49.99") }
    var costPriceStr by remember(existingProduct) { mutableStateOf(existingProduct?.costPrice?.toString() ?: "25.00") }
    var quantityStr by remember(existingProduct) { mutableStateOf(existingProduct?.quantityOnHand?.toString() ?: "20") }
    var reorderThresholdStr by remember(existingProduct) { mutableStateOf(existingProduct?.reorderThreshold?.toString() ?: "10") }
    var selectedSupplierId by remember(existingProduct, suppliers) {
        mutableStateOf(existingProduct?.supplierId ?: suppliers.firstOrNull()?.id ?: 1L)
    }
    var supplierExpanded by remember { mutableStateOf(false) }
    var selectedIcon by remember(existingProduct) { mutableStateOf(existingProduct?.iconName ?: "box") }

    val selectedSupplier = suppliers.find { it.id == selectedSupplierId } ?: suppliers.firstOrNull()

    // Calculated metrics
    val unitPrice = unitPriceStr.toDoubleOrNull() ?: 0.0
    val costPrice = costPriceStr.toDoubleOrNull() ?: 0.0
    val profit = (unitPrice - costPrice).coerceAtLeast(0.0)
    val marginPercent = if (unitPrice > 0) (profit / unitPrice) * 100.0 else 0.0

    val iconOptions = listOf(
        "box" to Icons.Default.Inventory2,
        "scanner" to Icons.Default.QrCodeScanner,
        "print" to Icons.Default.Print,
        "label" to Icons.Default.Label,
        "shield" to Icons.Default.Shield,
        "cable" to Icons.Default.Cable,
        "build" to Icons.Default.Build,
        "hub" to Icons.Default.Hub
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, Slate200, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (existingProduct == null) "Create New Product SKU" else "Edit Product: ${existingProduct.name}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Slate900
                )
                Text(
                    text = "Configure inventory specification, pricing model, and reorder levels",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Section: Identity
                Text(
                    text = "PRODUCT DETAILS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Product Name
                Text("Product Name *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_input"),
                    placeholder = { Text("e.g., Wireless Barcode Scanner Gen 3") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Slate700,
                        unfocusedBorderColor = Slate200
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // SKU with Auto-generate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Stock Keeping Unit (SKU) *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it.uppercase() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_sku_input"),
                            placeholder = { Text("e.g., SCAN-WL-02") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val catCode = category.take(3).uppercase()
                            val randNum = Random.nextInt(100, 999)
                            sku = "$catCode-$randNum"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate SKU")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category & Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Category", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_category_input"),
                            placeholder = { Text("e.g., Hardware, Packaging, Tools") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Product Icon / Asset", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            items(iconOptions) { (key, iconVector) ->
                                val isSelected = selectedIcon == key
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Slate700 else Slate100)
                                        .clickable { selectedIcon = key },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = key,
                                        tint = if (isSelected) Color.White else Slate600,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Slate200, thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))

                // Section: Financials & Margins
                Text(
                    text = "PRICING & VALUATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit Selling Price ($) *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = unitPriceStr,
                            onValueChange = { unitPriceStr = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_unit_price_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cost of Goods / Purchase Cost ($) *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = costPriceStr,
                            onValueChange = { costPriceStr = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_cost_price_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Margin calculation pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Projected Gross Margin: $${String.format(Locale.US, "%.2f", profit)} per unit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate700
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (marginPercent > 30) SuccessGreenLight else AccentOrangeLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${String.format(Locale.US, "%.1f", marginPercent)}% Margin",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (marginPercent > 30) SuccessGreenDark else AccentOrangeDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Slate200, thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))

                // Section: Inventory Thresholds & Supplier
                Text(
                    text = "STOCK THRESHOLDS & SUPPLIER",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Initial / Current Quantity", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_quantity_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reorder Threshold (Alert Level)", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = reorderThresholdStr,
                            onValueChange = { reorderThresholdStr = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_reorder_threshold_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Supplier Dropdown
                Text("Assigned Supplier *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = supplierExpanded,
                    onExpandedChange = { supplierExpanded = !supplierExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSupplier?.name ?: "Select Supplier",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("product_supplier_dropdown"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Slate700,
                            unfocusedBorderColor = Slate200
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = supplierExpanded,
                        onDismissRequest = { supplierExpanded = false }
                    ) {
                        suppliers.forEach { sup ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(sup.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(sup.categories, style = MaterialTheme.typography.labelSmall, color = Slate400)
                                    }
                                },
                                onClick = {
                                    selectedSupplierId = sup.id
                                    supplierExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Actions: Save & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val targetId = existingProduct?.id ?: 0L
                            val finalQty = quantityStr.toIntOrNull() ?: 0
                            val finalThreshold = reorderThresholdStr.toIntOrNull() ?: 10
                            onSaveProduct(
                                targetId,
                                sku,
                                name,
                                category,
                                unitPrice,
                                costPrice,
                                finalQty,
                                finalThreshold,
                                selectedSupplierId,
                                selectedSupplier?.name ?: "Primary Supplier",
                                selectedIcon
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("save_product_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (existingProduct == null) "Create Product SKU" else "Save Changes",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
