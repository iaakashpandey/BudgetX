package com.example.budgetx.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = 'Income'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = 'Expense'")
    fun getTotalExpense(): Flow<Double>

    @Query("SELECT * FROM transaction_table ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE type = :type ORDER BY date DESC")
    fun getAllTransactionsByType(type: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE category = :category ORDER BY date DESC")
    fun getAllTransactionsByCategory(category: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = :type")
    fun getTotalAmountByType(type: String): Flow<Double>

    @Query("SELECT * FROM transaction_table WHERE paymentMode = :paymentMode ORDER BY date DESC")
    fun getAllTransactionsByPaymentMode(paymentMode: String): Flow<List<Transaction>>

    @Query("DELETE FROM transaction_table")
    suspend fun clearAllTransactions()
}
@Dao
interface SplitTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplitTransaction(splitTransaction: SplitTransaction)

    @Query("SELECT * FROM split_transaction_table WHERE transactionId = :transactionId")
    fun getSplitTransactionsByTransactionId(transactionId: Int): Flow<List<SplitTransaction>>

    @Query("DELETE FROM split_transaction_table WHERE transactionId = :transactionId")
    suspend fun deleteSplitTransactionsByTransactionId(transactionId: Int)
}
