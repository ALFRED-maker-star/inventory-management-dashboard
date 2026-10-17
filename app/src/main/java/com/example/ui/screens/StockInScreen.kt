package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.Supplier
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInScreen(
    products: List<Product>,
    suppliers: List<Supplier>,
    prefillProductId: Long?,
    onRecordPurchase: (
        productId: Long,
        quantity: Int,
        supplierName: String,
        costPrice: Double,
        referenceNo: String,
        notes: String
    ) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProductId by remember(prefillProductId, products) {
        mutableStateOf(prefillProductId ?: products.firstOrNull()?.id ?: 0L)
    }
    var productDropdownExpanded by remember { mutableStateOf(false) }

    val selectedProduct = products.find { it.id == selectedProductId } ?: products.firstOrNull()

    var quantityStr by remember(selectedProduct) {
        val suggested = if (selectedProduct != null && selectedProduct.isLowStock) {
            ((selectedProduct.reorderThreshold * 2) - selectedProduct.quantityOnHand).coerceAtLeast(10)
        } else 20
        mutableStateOf("$suggested")
    }

    var selectedSupplierId by remember(selectedProduct, suppliers) {
        mutableStateOf(selectedProduct?.supplierId ?: suppliers.firstOrNull()?.id ?: 0L)
    }
    var supplierDropdownExpanded by remember { mutableStateOf(false) }
    val selectedSupplier = suppliers.find { it.id == selectedSupplierId } ?: suppliers.firstOrNull()

    var unitCostStr by remember(selectedProduct) {
        mutableStateOf(selectedProduct?.costPrice?.toString() ?: "10.00")
    }

    val todayDateStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var referenceNo by remember {
        mutableStateOf("PO-2026-" + (100..999).random())
    }
    var notes by remember { mutableStateOf("") }

    val quantity = quantityStr.toIntOrNull() ?: 0
    val unitCost = unitCostStr.toDoubleOrNull() ?: 0.0
    val totalCost = quantity * unitCost
    val currentStock = selectedProduct?.quantityOnHand ?: 0
    val newProjectedStock = currentStock + quantity

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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Stock In (Purchase Goods Receipt)",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Slate900
                )
                Text(
                    text = "Receive inventory from suppliers and automatically update warehouse stock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                // Product Selection Dropdown
                Text("Select Product *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = productDropdownExpanded,
                    onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedProduct != null) "${selectedProduct.sku} — ${selectedProduct.name} (On Hand: ${selectedProduct.quantityOnHand})" else "Select a product",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("stock_in_product_select"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Slate700,
                            unfocusedBorderColor = Slate200
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = productDropdownExpanded,
                        onDismissRequest = { productDropdownExpanded = false }
                    ) {
                        products.forEach { prod ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(prod.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                            Text("SKU: ${prod.sku} • Supplier: ${prod.supplierName}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                        }
                                        Text("${prod.quantityOnHand} in stock", style = MaterialTheme.typography.labelMedium, color = Slate700)
                                    }
                                },
                                onClick = {
                                    selectedProductId = prod.id
                                    unitCostStr = prod.costPrice.toString()
                                    productDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quantity and Quick Stepper Buttons
                Text("Quantity Received *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stock_in_quantity_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Slate700,
                            unfocusedBorderColor = Slate200
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Quick increment buttons
                    listOf(10, 25, 50, 100).forEach { inc ->
                        OutlinedButton(
                            onClick = {
                                val current = quantityStr.toIntOrNull() ?: 0
                                quantityStr = (current + inc).toString()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+$inc", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Supplier & Unit Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Supplier *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = supplierDropdownExpanded,
                            onExpandedChange = { supplierDropdownExpanded = !supplierDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedSupplier?.name ?: "Select Supplier",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Slate700,
                                    unfocusedBorderColor = Slate200
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = supplierDropdownExpanded,
                                onDismissRequest = { supplierDropdownExpanded = false }
                            ) {
                                suppliers.forEach { sup ->
                                    DropdownMenuItem(
                                        text = { Text(sup.name) },
                                        onClick = {
                                            selectedSupplierId = sup.id
                                            supplierDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit Cost ($) *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = unitCostStr,
                            onValueChange = { unitCostStr = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("stock_in_unit_cost_input"),
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

                Spacer(modifier = Modifier.height(20.dp))

                // Date & PO Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Receipt Date", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = todayDateStr,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate400) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Purchase Order / Reference #", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = referenceNo,
                            onValueChange = { referenceNo = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("stock_in_ref_input"),
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

                // Notes
                Text("Inspection & Receiving Notes", style = MaterialTheme.typography.titleSmall, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("e.g., Inspected intact, verified batch lot and packaging", color = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Slate700,
                        unfocusedBorderColor = Slate200
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Live Impact Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ORDER IMPACT PREVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1D4ED8),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Inventory Stock Change:", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            Text("$currentStock units  ➔  $newProjectedStock units (+ $quantity)", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1D4ED8))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Purchase Inflow Cost:", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            Text("$${String.format(Locale.US, "%,.2f", totalCost)}", style = MaterialTheme.typography.titleMedium, color = Slate900)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (selectedProduct != null && quantity > 0) {
                                onRecordPurchase(
                                    selectedProduct.id,
                                    quantity,
                                    selectedSupplier?.name ?: selectedProduct.supplierName,
                                    unitCost,
                                    referenceNo,
                                    notes
                                )
                            }
                        },
                        enabled = selectedProduct != null && quantity > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("record_purchase_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Record Purchase & Increase Stock", color = Color.White)
                    }
                }
            }
        }
    }
}
