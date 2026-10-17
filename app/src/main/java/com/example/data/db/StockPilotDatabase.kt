package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ProductDao
import com.example.data.dao.StockTransactionDao
import com.example.data.dao.SupplierDao
import com.example.data.model.Product
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, Supplier::class, StockTransaction::class],
    version = 1,
    exportSchema = false
)
abstract class StockPilotDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun supplierDao(): SupplierDao
    abstract fun stockTransactionDao(): StockTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: StockPilotDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): StockPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockPilotDatabase::class.java,
                    "stockpilot_database.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: StockPilotDatabase) {
            val supplierDao = database.supplierDao()
            val productDao = database.productDao()
            val transactionDao = database.stockTransactionDao()

            // 1. Initial Suppliers
            val apexSupplier = Supplier(
                id = 1,
                name = "Apex Electronics Ltd",
                contactPerson = "Marcus Vance",
                email = "orders@apexelectronics.com",
                phone = "+1 (555) 019-2834",
                address = "100 Innovation Way, Austin, TX",
                categories = "Hardware, Sensors, Scanners"
            )
            val nordicSupplier = Supplier(
                id = 2,
                name = "Nordic Hardware Corp",
                contactPerson = "Astrid Lindgren",
                email = "astrid@nordichardware.io",
                phone = "+1 (555) 014-4921",
                address = "45 Industrial Blvd, Seattle, WA",
                categories = "Tools, Safety Equipment, Mounts"
            )
            val pacificSupplier = Supplier(
                id = 3,
                name = "Pacific Paper & Packaging",
                contactPerson = "David Chen",
                email = "dchen@pacificpack.com",
                phone = "+1 (555) 018-8372",
                address = "220 Harbor Dr, Long Beach, CA",
                categories = "Packaging, Boxes, Labels"
            )
            val bytegearSupplier = Supplier(
                id = 4,
                name = "ByteGear Logistics",
                contactPerson = "Elena Rostova",
                email = "supply@bytegear.net",
                phone = "+1 (555) 012-3490",
                address = "88 Logistics Parkway, Chicago, IL",
                categories = "Peripherals, Cables, Hubs"
            )

            supplierDao.insertAll(listOf(apexSupplier, nordicSupplier, pacificSupplier, bytegearSupplier))

            // 2. Initial Products
            val now = System.currentTimeMillis()
            val products = listOf(
                Product(
                    id = 1,
                    sku = "SCAN-WL-01",
                    name = "Pro Wireless Barcode Scanner",
                    category = "Hardware",
                    quantityOnHand = 24,
                    unitPrice = 149.99,
                    costPrice = 85.00,
                    reorderThreshold = 10,
                    supplierId = 1,
                    supplierName = "Apex Electronics Ltd",
                    iconName = "scanner",
                    updatedAt = now
                ),
                Product(
                    id = 2,
                    sku = "PRN-TH-80",
                    name = "Thermal Receipt Printer 80mm",
                    category = "Hardware",
                    quantityOnHand = 14,
                    unitPrice = 189.50,
                    costPrice = 110.00,
                    reorderThreshold = 5,
                    supplierId = 1,
                    supplierName = "Apex Electronics Ltd",
                    iconName = "print",
                    updatedAt = now
                ),
                Product(
                    id = 3,
                    sku = "LBL-4X6-500",
                    name = "Direct Thermal Labels 4x6\" (500ct)",
                    category = "Packaging",
                    quantityOnHand = 160,
                    unitPrice = 14.50,
                    costPrice = 6.20,
                    reorderThreshold = 35,
                    supplierId = 3,
                    supplierName = "Pacific Paper & Packaging",
                    iconName = "label",
                    updatedAt = now
                ),
                Product(
                    id = 4,
                    sku = "BOX-HD-LG",
                    name = "Heavy Duty Corrugated Box Large",
                    category = "Packaging",
                    quantityOnHand = 6, // Low stock!
                    unitPrice = 5.20,
                    costPrice = 2.10,
                    reorderThreshold = 25,
                    supplierId = 3,
                    supplierName = "Pacific Paper & Packaging",
                    iconName = "box",
                    updatedAt = now
                ),
                Product(
                    id = 5,
                    sku = "GLV-ERG-L",
                    name = "Ergonomic Grip Work Gloves (L)",
                    category = "Safety",
                    quantityOnHand = 4, // Low stock!
                    unitPrice = 18.00,
                    costPrice = 8.50,
                    reorderThreshold = 15,
                    supplierId = 2,
                    supplierName = "Nordic Hardware Corp",
                    iconName = "shield",
                    updatedAt = now
                ),
                Product(
                    id = 6,
                    sku = "HUB-USB-07",
                    name = "High-Speed Powered USB 3.0 Hub",
                    category = "Peripherals",
                    quantityOnHand = 0, // Out of stock!
                    unitPrice = 42.00,
                    costPrice = 21.00,
                    reorderThreshold = 12,
                    supplierId = 4,
                    supplierName = "ByteGear Logistics",
                    iconName = "hub",
                    updatedAt = now
                ),
                Product(
                    id = 7,
                    sku = "RFID-SM-500",
                    name = "Smart EPC Gen2 RFID Tags (500pk)",
                    category = "Hardware",
                    quantityOnHand = 3, // Low stock!
                    unitPrice = 129.00,
                    costPrice = 68.00,
                    reorderThreshold = 10,
                    supplierId = 1,
                    supplierName = "Apex Electronics Ltd",
                    iconName = "nfc",
                    updatedAt = now
                ),
                Product(
                    id = 8,
                    sku = "TPE-STR-06",
                    name = "Fiberglass Reinforced Packing Tape 6pk",
                    category = "Packaging",
                    quantityOnHand = 52,
                    unitPrice = 24.00,
                    costPrice = 12.00,
                    reorderThreshold = 15,
                    supplierId = 3,
                    supplierName = "Pacific Paper & Packaging",
                    iconName = "layers",
                    updatedAt = now
                ),
                Product(
                    id = 9,
                    sku = "CBL-C6-25",
                    name = "Cat6 Gigabit Ethernet Cable 25ft",
                    category = "Peripherals",
                    quantityOnHand = 40,
                    unitPrice = 15.99,
                    costPrice = 6.80,
                    reorderThreshold = 18,
                    supplierId = 4,
                    supplierName = "ByteGear Logistics",
                    iconName = "cable",
                    updatedAt = now
                ),
                Product(
                    id = 10,
                    sku = "TIE-IND-100",
                    name = "UV Heavy Duty Cable Ties 100pk",
                    category = "Tools",
                    quantityOnHand = 95,
                    unitPrice = 9.50,
                    costPrice = 3.40,
                    reorderThreshold = 25,
                    supplierId = 2,
                    supplierName = "Nordic Hardware Corp",
                    iconName = "build",
                    updatedAt = now
                )
            )

            productDao.insertAll(products)

            // 3. Initial Transactions
            val dayMs = 86400000L
            val transactions = listOf(
                StockTransaction(
                    id = 1,
                    productId = 1,
                    productName = "Pro Wireless Barcode Scanner",
                    productSku = "SCAN-WL-01",
                    type = "IN",
                    quantity = 25,
                    unitPrice = 85.00,
                    totalAmount = 2125.00,
                    partyName = "Apex Electronics Ltd",
                    timestamp = now - (dayMs * 2),
                    referenceNo = "PO-2026-081",
                    notes = "Monthly batch restock"
                ),
                StockTransaction(
                    id = 2,
                    productId = 1,
                    productName = "Pro Wireless Barcode Scanner",
                    productSku = "SCAN-WL-01",
                    type = "OUT",
                    quantity = 1,
                    unitPrice = 149.99,
                    totalAmount = 149.99,
                    partyName = "Metro Logistics Dep.",
                    timestamp = now - (dayMs * 1),
                    referenceNo = "SO-8812",
                    notes = "Expedited fulfillment"
                ),
                StockTransaction(
                    id = 3,
                    productId = 3,
                    productName = "Direct Thermal Labels 4x6\" (500ct)",
                    productSku = "LBL-4X6-500",
                    type = "OUT",
                    quantity = 20,
                    unitPrice = 14.50,
                    totalAmount = 290.00,
                    partyName = "Apex Freight Services",
                    timestamp = now - (3600000L * 6),
                    referenceNo = "SO-8813",
                    notes = "Regular warehouse order"
                ),
                StockTransaction(
                    id = 4,
                    productId = 2,
                    productName = "Thermal Receipt Printer 80mm",
                    productSku = "PRN-TH-80",
                    type = "OUT",
                    quantity = 2,
                    unitPrice = 189.50,
                    totalAmount = 379.00,
                    partyName = "Kiosk Network Inc.",
                    timestamp = now - (3600000L * 2),
                    referenceNo = "SO-8814",
                    notes = "POS deployment upgrade"
                ),
                StockTransaction(
                    id = 5,
                    productId = 4,
                    productName = "Heavy Duty Corrugated Box Large",
                    productSku = "BOX-HD-LG",
                    type = "OUT",
                    quantity = 30,
                    unitPrice = 5.20,
                    totalAmount = 156.00,
                    partyName = "Prime Dispatch Hub",
                    timestamp = now - (1800000L),
                    referenceNo = "SO-8815",
                    notes = "Bulk packaging pack"
                )
            )

            transactionDao.insertAll(transactions)
        }
    }
}
