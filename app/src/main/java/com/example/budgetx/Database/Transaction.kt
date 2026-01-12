package com.example.budgetx.Database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val type: String,
    val date: String,
    val category: String,
    val paymentMode: String,
    val note: String? = null
)  : Parcelable

@Parcelize
@Entity(tableName = "split_transaction_table")
data class SplitTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val transactionId: Int,
    val friendName: String,
    var friendAmount: Double,
    val status: String
) : Parcelable

