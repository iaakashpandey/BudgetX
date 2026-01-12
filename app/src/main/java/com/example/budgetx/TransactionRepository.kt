package com.example.budgetx

import com.example.budgetx.Database.SplitTransaction
import com.example.budgetx.Database.SplitTransactionDao
import com.example.budgetx.Database.Transaction
import com.example.budgetx.Database.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao,
                            private val splitTransactionDao: SplitTransactionDao
) {

    // Fetch total income
    fun getTotalIncome(): Flow<Double> {
        return transactionDao.getTotalIncome()
    }

    // Fetch total expense
    fun getTotalExpense(): Flow<Double> {
        return transactionDao.getTotalExpense()
    }

    // Fetch all transactions
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    // Fetch transactions by type
    fun getTransactionsByType(type: String): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsByType(type)
    }

    // Fetch transactions by category
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsByCategory(category)
    }

    // Fetch transactions by date range
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    // Fetch transactions by payment mode
    fun getTransactionsByPaymentMode(paymentMode: String): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsByPaymentMode(paymentMode)
    }

    // Insert a new transaction
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    // Update an existing transaction
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    // Delete a specific transaction
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    // Clear all transactions
    suspend fun clearAllTransactions() {
        transactionDao.clearAllTransactions()
    }

    // Split transaction methods
    suspend fun insertSplitTransaction(splitTransaction: SplitTransaction) {
        splitTransactionDao.insertSplitTransaction(splitTransaction)
    }

    fun getSplitTransactionsByTransactionId(transactionId: Int): Flow<List<SplitTransaction>> {
        return splitTransactionDao.getSplitTransactionsByTransactionId(transactionId)
    }

    suspend fun deleteSplitTransactionsByTransactionId(transactionId: Int) {
        splitTransactionDao.deleteSplitTransactionsByTransactionId(transactionId)
    }
}

