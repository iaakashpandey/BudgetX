/*import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.budgetx.Database.TransactionDatabase
import com.example.budgetx.TransactionRepository
import com.example.budgetx.TransactionViewModel*/

package com.example.budgetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TransactionViewModelFactory(private val repository: TransactionRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*class TransactionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            val transactionDao = TransactionDatabase.get(application).transactionDao()
            val repository = TransactionRepository(transactionDao)
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/
