package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val name: String,
    val category: String,
    val quantityOnHand: Int,
    val unitPrice: Double, // Selling price
    val costPrice: Double, // Purchase cost
    val reorderThreshold: Int,
    val supplierId: Long,
    val supplierName: String,
    val iconName: String = "box",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isLowStock: Boolean
        get() = quantityOnHand in 1..reorderThreshold

    val isOutOfStock: Boolean
        get() = quantityOnHand <= 0

    val stockStatus: StockStatus
        get() = when {
            quantityOnHand <= 0 -> StockStatus.OUT_OF_STOCK
            quantityOnHand <= reorderThreshold -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }

    val totalValue: Double
        get() = quantityOnHand * unitPrice

    val profitMarginPercent: Double
        get() = if (unitPrice > 0) ((unitPrice - costPrice) / unitPrice) * 100.0 else 0.0
}

enum class StockStatus(val label: String) {
    IN_STOCK("In Stock"),
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock")
}
