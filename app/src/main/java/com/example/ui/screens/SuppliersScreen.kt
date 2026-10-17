package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Supplier
import com.example.ui.theme.*

@Composable
fun SuppliersScreen(
    suppliers: List<Supplier>,
    products: List<Product>,
    onSaveSupplier: (id: Long, name: String, contactPerson: String, email: String, phone: String, address: String, categories: String) -> Unit,
    onDeleteSupplier: (Supplier) -> Unit,
    onViewProduct: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingSupplier by remember { mutableStateOf<Supplier?>(null) }
    var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }
    var expandedSupplierId by remember { mutableStateOf<Long?>(null) }

    val filteredSuppliers = remember(suppliers, searchQuery) {
        if (searchQuery.isBlank()) suppliers
        else suppliers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.contactPerson.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true) ||
            it.categories.contains(searchQuery, ignoreCase = true)
        }
    }

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
                    text = "Supplier Directory",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Slate900
                )
                Text(
                    text = "Vendor profiles, contracts, contacts, and linked catalog items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }

            Button(
                onClick = {
                    editingSupplier = null
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_supplier_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ Add Supplier", style = MaterialTheme.typography.labelMedium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search suppliers by company name, contact person, or category...", color = Slate400) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("suppliers_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Slate700,
                unfocusedBorderColor = Slate200
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Table Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (filteredSuppliers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No suppliers found.", color = Slate500)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate100)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SUPPLIER NAME", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(2f))
                        Text("CONTACT PERSON", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.3f))
                        Text("EMAIL & PHONE", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(2f))
                        Text("LOCATION", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.8f))
                        Text("LINKED PRODUCTS", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1.2f))
                        Text("ACTIONS", style = MaterialTheme.typography.labelSmall, color = Slate600, modifier = Modifier.weight(1f))
                    }

                    HorizontalDivider(color = Slate200, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredSuppliers, key = { it.id }) { supplier ->
                            val linkedProducts = products.filter { it.supplierId == supplier.id }
                            val isExpanded = expandedSupplierId == supplier.id

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedSupplierId = if (isExpanded) null else supplier.id
                                        }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Supplier Name & Categories
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(
                                            text = supplier.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Slate900
                                        )
                                        Text(
                                            text = supplier.categories,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400
                                        )
                                    }

                                    // Contact Person
                                    Text(
                                        text = supplier.contactPerson,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Slate700,
                                        modifier = Modifier.weight(1.3f)
                                    )

                                    // Email & Phone
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(
                                            text = supplier.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF2563EB)
                                        )
                                        Text(
                                            text = supplier.phone,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate500
                                        )
                                    }

                                    // Address
                                    Text(
                                        text = supplier.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600,
                                        modifier = Modifier.weight(1.8f)
                                    )

                                    // Linked Products Count with expansion chip
                                    Box(modifier = Modifier.weight(1.2f)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (linkedProducts.isNotEmpty()) Slate100 else Color.Transparent)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Inventory2,
                                                    contentDescription = null,
                                                    tint = Slate600,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${linkedProducts.size} SKUs",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Slate700
                                                )
                                            }
                                        }
                                    }

                                    // Actions
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                editingSupplier = supplier
                                                showDialog = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = Slate600,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { supplierToDelete = supplier },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = DangerRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Expanded linked products view
                                if (isExpanded) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Slate100)
                                            .padding(horizontal = 24.dp, vertical = 12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "CATALOG ITEMS SUPPLIED BY ${supplier.name.uppercase()}:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Slate600
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            if (linkedProducts.isEmpty()) {
                                                Text("No products currently assigned to this vendor.", style = MaterialTheme.typography.bodySmall, color = Slate400)
                                            } else {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    linkedProducts.forEach { prod ->
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(SurfaceWhite)
                                                                .border(1.dp, Slate200, RoundedCornerShape(6.dp))
                                                                .clickable { onViewProduct(prod.id) }
                                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                                        ) {
                                                            Column {
                                                                Text(prod.name, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                                                Text("SKU: ${prod.sku} • Stock: ${prod.quantityOnHand}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
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

    // Add / Edit Supplier Modal Dialog
    if (showDialog) {
        var name by remember { mutableStateOf(editingSupplier?.name ?: "") }
        var contact by remember { mutableStateOf(editingSupplier?.contactPerson ?: "") }
        var email by remember { mutableStateOf(editingSupplier?.email ?: "") }
        var phone by remember { mutableStateOf(editingSupplier?.phone ?: "") }
        var address by remember { mutableStateOf(editingSupplier?.address ?: "") }
        var categories by remember { mutableStateOf(editingSupplier?.categories ?: "General Logistics") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (editingSupplier == null) "Add Supplier" else "Edit Supplier: ${editingSupplier?.name}")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Company Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("supplier_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Contact Person") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Location / Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = categories,
                        onValueChange = { categories = it },
                        label = { Text("Supplied Categories") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetId = editingSupplier?.id ?: 0L
                        onSaveSupplier(targetId, name, contact, email, phone, address, categories)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                    modifier = Modifier.testTag("supplier_save_submit")
                ) {
                    Text("Save Supplier")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete supplier confirmation dialog
    supplierToDelete?.let { supplier ->
        AlertDialog(
            onDismissRequest = { supplierToDelete = null },
            title = { Text("Delete Supplier: ${supplier.name}?") },
            text = { Text("Are you sure you want to remove this vendor from your supplier registry?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSupplier(supplier)
                        supplierToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { supplierToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
