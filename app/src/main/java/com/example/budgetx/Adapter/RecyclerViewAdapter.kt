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

class RecyclerViewAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.TransactionViewHolder>() {

    private var fullList: List<Transaction> = transactions

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageTransactionItem: ImageView = itemView.findViewById(R.id.imageTransactionItem)
        val categoryTextView: TextView = itemView.findViewById(R.id.textTransactionName)
        val dateTextView: TextView = itemView.findViewById(R.id.textTransactionDate)
        val amountTextView: TextView = itemView.findViewById(R.id.textTransactionAmount)
    }

    fun submitList(newTransactions: List<Transaction>) {
        fullList = newTransactions
        transactions = newTransactions
        notifyDataSetChanged()
    }

    fun filterList(filteredTransactions: List<Transaction>) {
        transactions = filteredTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set the transaction details in the views
        holder.imageTransactionItem.setImageResource(getCategoryIcon(transaction.category))
        holder.categoryTextView.text = transaction.category
        holder.dateTextView.text = transaction.date
        holder.amountTextView.text = "${transaction.amount}"

        // Change the text color based on transaction type
        val colorRes = if (transaction.type == "Income") R.color.light_green else R.color.white_dark
        holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, colorRes))

        // Handle item click to pass the transaction
        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

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

    fun updateTransactions(newTransactions: List<Transaction>) {
        fullList = newTransactions
        transactions = newTransactions
        notifyDataSetChanged()
    }

    fun resetFilter() {
        transactions = fullList
        notifyDataSetChanged()
    }

    fun updateList(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
    fun removeItemAtPosition(position: Int) {
        transactions = transactions.toMutableList().apply {
            removeAt(position)
        }
        notifyItemRemoved(position)

    }

    fun insertItemAtPosition(position: Int, transaction: Transaction) {
        transactions = transactions.toMutableList().apply { add(position, transaction) }
        notifyItemInserted(position)
    }

    fun getItemAtPosition(position: Int): Transaction {
        return transactions[position]
    }

    fun addItemAtPosition(lastDeletedPosition: Int, it: Transaction) {

    }
}



