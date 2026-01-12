package com.example.budgetx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetx.Database.SplitTransaction
import com.example.budgetx.Database.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // LiveData for all transactions
    val allTransactions: LiveData<List<Transaction>> = repository.getAllTransactions().asLiveData()

    // Real-time total income and expense
    val totalIncome: LiveData<Double> = repository.getTotalIncome().asLiveData()
    val totalExpense: LiveData<Double> = repository.getTotalExpense().asLiveData()

    // Calculate total balance dynamically using MediatorLiveData
    val totalBalance: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(totalIncome) { income ->
            value = (income ?: 0.0) - (totalExpense.value ?: 0.0)
        }
        addSource(totalExpense) { expense ->
            value = (totalIncome.value ?: 0.0) - (expense ?: 0.0)
        }
    }

    // Insert a new transaction
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    // Update an existing transaction
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    // Delete an existing transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // Fetch transactions by type
    fun getTransactionsByType(type: String): LiveData<List<Transaction>> {
        return repository.getTransactionsByType(type).asLiveData()
    }

    // Fetch transactions by category
    fun getTransactionsByCategory(category: String): LiveData<List<Transaction>> {
        return repository.getTransactionsByCategory(category).asLiveData()
    }

    // Fetch transactions by date
    fun getTransactionsByDate(startDate: String, endDate: String): LiveData<List<Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate).asLiveData()
    }

    // Fetch transactions by payment mode
    fun getTransactionsByPaymentMode(paymentMode: String): LiveData<List<Transaction>> {
        return repository.getTransactionsByPaymentMode(paymentMode).asLiveData()
    }

    // Clear all transactions
    fun clearAllTransactions() {
        viewModelScope.launch {
            repository.clearAllTransactions()
        }
    }

    // Split transaction methods
    fun insertSplitTransaction(splitTransaction: SplitTransaction) = viewModelScope.launch {
        repository.insertSplitTransaction(splitTransaction)
    }

    fun getSplitTransactionsByTransactionId(transactionId: Int): LiveData<List<SplitTransaction>> {
        return repository.getSplitTransactionsByTransactionId(transactionId).asLiveData()
    }

    fun deleteSplitTransactionsByTransactionId(transactionId: Int) = viewModelScope.launch {
        repository.deleteSplitTransactionsByTransactionId(transactionId)
    }
}

