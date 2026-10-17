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
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockOutScreen(
    products: List<Product>,
    prefillProductId: Long?,
    onRecordSale: (
        productId: Long,
        quantity: Int,
        salePrice: Double,
        customerName: String,
        referenceNo: String,
        notes: String
    ) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Exclude or warn if out of stock
    var selectedProductId by remember(prefillProductId, products) {
        val found = products.find { it.id == prefillProductId }
        mutableStateOf(found?.id ?: products.firstOrNull { it.quantityOnHand > 0 }?.id ?: products.firstOrNull()?.id ?: 0L)
    }
    var productDropdownExpanded by remember { mutableStateOf(false) }
    val selectedProduct = products.find { it.id == selectedProductId } ?: products.firstOrNull()

    var quantityStr by remember(selectedProduct) {
        val initQty = if ((selectedProduct?.quantityOnHand ?: 0) >= 1) "1" else "0"
        mutableStateOf(initQty)
    }

    var salePriceStr by remember(selectedProduct) {
        mutableStateOf(selectedProduct?.unitPrice?.toString() ?: "0.00")
    }

    var customerName by remember { mutableStateOf("") }
    var referenceNo by remember { mutableStateOf("SO-88" + (20..99).random()) }
    var notes by remember { mutableStateOf("") }

    val quantity = quantityStr.toIntOrNull() ?: 0
    val salePrice = salePriceStr.toDoubleOrNull() ?: 0.0
    val availableStock = selectedProduct?.quantityOnHand ?: 0
    val costPrice = selectedProduct?.costPrice ?: 0.0

    val isInsufficientStock = quantity > availableStock || availableStock <= 0
    val totalRevenue = quantity * salePrice
    val profit = (salePrice - costPrice) * quantity
    val remainingStock = (availableStock - quantity).coerceAtLeast(0)

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
                    .background(SuccessGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PointOfSale,
                    contentDescription = null,
                    tint = SuccessGreenDark,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Stock Out (Sales Dispatch)",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Slate900
                )
                Text(
                    text = "Fulfill customer orders, decrease inventory, and calculate revenue",
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
                // Product Selection
                Text("Select Item from Inventory *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = productDropdownExpanded,
                    onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedProduct != null) {
                            "${selectedProduct.sku} — ${selectedProduct.name} (Stock: ${selectedProduct.quantityOnHand})"
                        } else "Select a product",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("stock_out_product_select"),
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
                            val canSell = prod.quantityOnHand > 0
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(prod.name, style = MaterialTheme.typography.bodyMedium)
                                            Text("${prod.sku} • $${prod.unitPrice}", style = MaterialTheme.typography.labelSmall, color = Slate400)
                                        }
                                        Text(
                                            text = if (canSell) "${prod.quantityOnHand} avail" else "Out of stock",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (canSell) SuccessGreenDark else DangerRed
                                        )
                                    }
                                },
                                onClick = {
                                    selectedProductId = prod.id
                                    salePriceStr = prod.unitPrice.toString()
                                    quantityStr = if (prod.quantityOnHand > 0) "1" else "0"
                                    productDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quantity Sold & Cap controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity Sold / Dispatched *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                    Text(
                        text = "Available in warehouse: $availableStock units",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (availableStock <= 0) DangerRedDark else Slate500
                    )
                }
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
                            .testTag("stock_out_quantity_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = isInsufficientStock,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isInsufficientStock) DangerRed else Slate700,
                            unfocusedBorderColor = if (isInsufficientStock) DangerRed else Slate200
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedButton(
                        onClick = { quantityStr = availableStock.toString() },
                        enabled = availableStock > 0,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Max ($availableStock)")
                    }
                }

                // Insufficient Stock Alert Banner (Blocks Sale!)
                if (isInsufficientStock && selectedProduct != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DangerRedLight)
                            .border(1.dp, DangerRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = DangerRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (availableStock <= 0) {
                                    "Item is completely OUT OF STOCK. Cannot process sale."
                                } else {
                                    "Insufficient stock! Requested $quantity units, but only $availableStock units available."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = DangerRedDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sale Price & Customer Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit Sale Price ($) *", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = salePriceStr,
                            onValueChange = { salePriceStr = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("stock_out_price_input"),
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
                        Text("Customer / Client (Optional)", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            placeholder = { Text("e.g., Acme Logistics, Direct Customer", color = Slate400) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("stock_out_customer_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Invoice Ref & Notes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sales Order / Invoice #", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = referenceNo,
                            onValueChange = { referenceNo = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dispatch Notes", style = MaterialTheme.typography.titleSmall, color = Slate700)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("e.g., Express shipping, payment received", color = Slate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Slate700,
                                unfocusedBorderColor = Slate200
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sales Financial Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreenLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SALE OUTFLOW & PROFIT REVENUE",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreenDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gross Sale Revenue:", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            Text("$${String.format(Locale.US, "%,.2f", totalRevenue)}", style = MaterialTheme.typography.titleMedium, color = SuccessGreenDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Gross Profit:", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            Text("$${String.format(Locale.US, "%,.2f", profit)}", style = MaterialTheme.typography.titleMedium, color = Slate900)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Remaining Stock Level:", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            Text("$remainingStock units remaining", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
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
                            if (selectedProduct != null && quantity > 0 && !isInsufficientStock) {
                                onRecordSale(
                                    selectedProduct.id,
                                    quantity,
                                    salePrice,
                                    customerName,
                                    referenceNo,
                                    notes
                                )
                            }
                        },
                        enabled = selectedProduct != null && quantity > 0 && !isInsufficientStock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInsufficientStock) Slate400 else Slate700
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("record_sale_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isInsufficientStock) "Blocked (Insufficient Stock)" else "Record Sale & Decrease Stock",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
