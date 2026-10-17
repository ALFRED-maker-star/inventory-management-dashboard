package com.example.data.repository

import com.example.data.dao.ProductDao
import com.example.data.dao.StockTransactionDao
import com.example.data.dao.SupplierDao
import com.example.data.model.Product
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val productDao: ProductDao,
    private val supplierDao: SupplierDao,
    private val transactionDao: StockTransactionDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allSuppliers: Flow<List<Supplier>> = supplierDao.getAllSuppliers()
    val allTransactions: Flow<List<StockTransaction>> = transactionDao.getAllTransactions()
    val recentTransactions: Flow<List<StockTransaction>> = transactionDao.getRecentTransactions(20)
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()
    val lowStockCount: Flow<Int> = productDao.getLowStockCount()
    val totalProductCount: Flow<Int> = productDao.getProductCount()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    suspend fun addProduct(product: Product): Long = productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun addSupplier(supplier: Supplier): Long = supplierDao.insertSupplier(supplier)

    suspend fun updateSupplier(supplier: Supplier) = supplierDao.updateSupplier(supplier)

    suspend fun deleteSupplier(supplier: Supplier) = supplierDao.deleteSupplier(supplier)

    suspend fun recordPurchase(
        productId: Long,
        quantity: Int,
        supplierName: String,
        unitCost: Double,
        referenceNo: String = "",
        notes: String = ""
    ): Result<Unit> {
        if (quantity <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be greater than zero"))
        }
        val product = productDao.getProductById(productId)
            ?: return Result.failure(IllegalStateException("Product not found"))

        val totalAmount = quantity * unitCost
        val transaction = StockTransaction(
            productId = productId,
            productName = product.name,
            productSku = product.sku,
            type = "IN",
            quantity = quantity,
            unitPrice = unitCost,
            totalAmount = totalAmount,
            partyName = supplierName.ifBlank { product.supplierName },
            timestamp = System.currentTimeMillis(),
            referenceNo = referenceNo,
            notes = notes
        )

        transactionDao.insertTransaction(transaction)
        val updatedProduct = product.copy(
            quantityOnHand = product.quantityOnHand + quantity,
            costPrice = if (unitCost > 0) unitCost else product.costPrice,
            updatedAt = System.currentTimeMillis()
        )
        productDao.updateProduct(updatedProduct)
        return Result.success(Unit)
    }

    suspend fun recordSale(
        productId: Long,
        quantity: Int,
        salePrice: Double,
        customerName: String = "",
        referenceNo: String = "",
        notes: String = ""
    ): Result<Unit> {
        if (quantity <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be greater than zero"))
        }
        val product = productDao.getProductById(productId)
            ?: return Result.failure(IllegalStateException("Product not found"))

        if (product.quantityOnHand < quantity) {
            return Result.failure(
                IllegalStateException("Insufficient stock! Available: ${product.quantityOnHand}, Requested: $quantity")
            )
        }

        val totalAmount = quantity * salePrice
        val transaction = StockTransaction(
            productId = productId,
            productName = product.name,
            productSku = product.sku,
            type = "OUT",
            quantity = quantity,
            unitPrice = salePrice,
            totalAmount = totalAmount,
            partyName = customerName.ifBlank { "Direct Customer" },
            timestamp = System.currentTimeMillis(),
            referenceNo = referenceNo,
            notes = notes
        )

        transactionDao.insertTransaction(transaction)
        val updatedProduct = product.copy(
            quantityOnHand = product.quantityOnHand - quantity,
            updatedAt = System.currentTimeMillis()
        )
        productDao.updateProduct(updatedProduct)
        return Result.success(Unit)
    }
}
