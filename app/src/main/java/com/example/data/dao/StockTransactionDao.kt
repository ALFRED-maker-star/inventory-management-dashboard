package com.example.data.dao

import androidx.room.*
import com.example.data.model.StockTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTransactionDao {
    @Query("SELECT * FROM stock_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions WHERE productId = :productId ORDER BY timestamp DESC")
    fun getTransactionsForProduct(productId: Long): Flow<List<StockTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: StockTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<StockTransaction>)

    @Query("SELECT * FROM stock_transactions WHERE type = 'OUT' AND timestamp >= :sinceTimestamp")
    suspend fun getSalesSince(sinceTimestamp: Long): List<StockTransaction>

    @Query("SELECT * FROM stock_transactions WHERE type = 'IN' AND timestamp >= :sinceTimestamp")
    suspend fun getPurchasesSince(sinceTimestamp: Long): List<StockTransaction>
}
