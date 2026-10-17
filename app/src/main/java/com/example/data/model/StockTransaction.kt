package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_transactions")
data class StockTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val productSku: String,
    val type: String, // "IN" for Purchase, "OUT" for Sale
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val partyName: String, // Supplier name if IN, Customer name if OUT
    val timestamp: Long = System.currentTimeMillis(),
    val referenceNo: String = "",
    val notes: String = ""
)
