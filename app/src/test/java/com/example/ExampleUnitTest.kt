package com.example

import com.example.data.model.Product
import com.example.data.model.StockStatus
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun product_stockStatus_correctlyIdentifiesLevels() {
        val inStockProduct = Product(
            id = 1,
            sku = "TEST-01",
            name = "Test In Stock",
            category = "Test",
            quantityOnHand = 50,
            unitPrice = 100.0,
            costPrice = 60.0,
            reorderThreshold = 10,
            supplierId = 1,
            supplierName = "Supplier A"
        )
        assertEquals(StockStatus.IN_STOCK, inStockProduct.stockStatus)
        assertFalse(inStockProduct.isLowStock)
        assertFalse(inStockProduct.isOutOfStock)
        assertEquals(5000.0, inStockProduct.totalValue, 0.01)
        assertEquals(40.0, inStockProduct.profitMarginPercent, 0.01)

        val lowStockProduct = inStockProduct.copy(quantityOnHand = 8)
        assertEquals(StockStatus.LOW_STOCK, lowStockProduct.stockStatus)
        assertTrue(lowStockProduct.isLowStock)
        assertFalse(lowStockProduct.isOutOfStock)

        val outOfStockProduct = inStockProduct.copy(quantityOnHand = 0)
        assertEquals(StockStatus.OUT_OF_STOCK, outOfStockProduct.stockStatus)
        assertFalse(outOfStockProduct.isLowStock)
        assertTrue(outOfStockProduct.isOutOfStock)
    }

    @Test
    fun product_profitMarginCalculation() {
        val product = Product(
            id = 2,
            sku = "MARGIN-01",
            name = "Margin Test",
            category = "General",
            quantityOnHand = 10,
            unitPrice = 200.0,
            costPrice = 150.0,
            reorderThreshold = 5,
            supplierId = 1,
            supplierName = "Supplier B"
        )
        assertEquals(25.0, product.profitMarginPercent, 0.01)
        assertEquals(2000.0, product.totalValue, 0.01)
    }
}
