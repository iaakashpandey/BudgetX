package com.example.budgetx.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetx.Database.Transaction
import com.example.budgetx.R

class TransactionFragmentAdapter  : RecyclerView.Adapter<TransactionFragmentAdapter.TransactionViewHolder>(){

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageTransactionItem: ImageView = itemView.findViewById(R.id.imageTransactionItem)
        val nameTextView: TextView = itemView.findViewById(R.id.textTransactionName)
        val dateTextView: TextView = itemView.findViewById(R.id.textTransactionDate)
        val amountTextView: TextView = itemView.findViewById(R.id.textTransactionAmount)
    }

    private var transactionList: List<Transaction> = listOf()

    fun submitList(transactions: List<Transaction>) {
        transactionList = transactions
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.imageTransactionItem.setImageResource(getCategoryIcon(transaction.category))
        holder.nameTextView.text = transaction.category
        holder.dateTextView.text = transaction.date
        holder.amountTextView.text = "${transaction.amount}"

        // Set color for amount based on transaction type (income/expense)
        if (transaction.type == "Income") {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))  // Green for income
        } else {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white_dark))  // Color for expenses
        }
    }

    override fun getItemCount(): Int = transactionList.size

    private fun getCategoryIcon(category: String): Int {
        return when (category) {
            "Food" -> R.drawable.food
            "Rent" -> R.drawable.rent
            "Entertainment" -> R.drawable.travel
            "Travel" -> R.drawable.travel
            "Bills" -> R.drawable.cash
            "Salary" -> R.drawable.salary
            "Investment" -> R.drawable.investment
            "Business" -> R.drawable.business
            else -> R.drawable.other
        }
    }
    fun addItemAtPosition(lastDeletedPosition: Int, it: Transaction) {

    }

}