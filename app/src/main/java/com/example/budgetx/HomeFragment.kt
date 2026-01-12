package com.example.budgetx

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetx.Adapter.RecyclerViewAdapter
import com.example.budgetx.Database.DatabaseProvider
import com.example.budgetx.Database.Transaction
import com.example.budgetx.Database.TransactionDatabase
import com.example.budgetx.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecyclerViewAdapter

    private lateinit var database: TransactionDatabase
    private lateinit var repository: TransactionRepository
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Database and Repository
        val database = TransactionDatabase.getDatabase(requireContext())
        val repository = TransactionRepository(database.transactionDao(), database.splitTransactionDao())

        // Initialize ViewModel
        val factory = TransactionViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(TransactionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecyclerViewAdapter(emptyList()) { transaction -> openAddTransactionFragment(transaction) }
        binding.homeRecyclerView.adapter = adapter

        updateNextFragmentVisibility()

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                updateNextFragmentVisibility()
            }
        })

        binding.textNextFragment.setOnClickListener {
            val fragment = TransactionFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        // Attach swipe-to-delete functionality
        attachSwipeToDelete()

        // Load the saved name from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString("name", "")
        binding.textName.text = savedName ?: "Default Name"

        // Observe the transactions
        observeTransactions()

        // Floating Action Button for adding new transactions
        binding.imageFAB.setOnClickListener {
            val fragment = AddTransactionFragment()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_down)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        // Navigate to TransactionFragment
        binding.textNextFragment.setOnClickListener {
            val fragment = TransactionFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateNextFragmentVisibility() {
        if (adapter.itemCount >= 14) {
            binding.textNextFragment.visibility = View.VISIBLE
        } else {
            binding.textNextFragment.visibility = View.GONE
        }
    }


    private fun attachSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transactionToDelete = adapter.getItemAtPosition(position)

                // Remove transaction and show undo layout
                deleteTransactionWithUndo(transactionToDelete, position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_delete))
                    .addActionIcon(R.drawable.trash)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.homeRecyclerView)
    }

    private fun deleteTransactionWithUndo(transaction: Transaction, position: Int) {
        // Remove transaction from the adapter
        adapter.removeItemAtPosition(position)

        // Inflate the custom undo layout
        binding.layoutUndo.visibility = View.VISIBLE

        // Update totals temporarily
        updateTotals(transaction, false)

        // Set up the undo button functionality
        binding.layoutUndo.setOnClickListener {
            // Reinsert the transaction into the database
            viewModel.insertTransaction(transaction)

            // Update totals back
            updateTotals(transaction, true)

            // Hide the undo layout
            binding.layoutUndo.visibility = View.GONE
        }

        // Automatically hide the undo layout after a delay
        binding.layoutUndo.postDelayed({
            if (binding.layoutUndo.visibility == View.VISIBLE) {
                binding.layoutUndo.visibility = View.GONE
                viewModel.deleteTransaction(transaction) // Permanently delete the transaction from the database
            }
        }, 3000) // 3-second delay
    }

    private fun updateTotals(transaction: Transaction, isUndo: Boolean) {
        val amountChange = if (isUndo) transaction.amount else -transaction.amount
        if (transaction.type == "Income") {
            binding.textTotalIncomeAmount.text =
                String.format("%.2f", (binding.textTotalIncomeAmount.text.toString().toDouble() + amountChange))
        } else {
            binding.textTotalExpenseAmount.text =
                String.format("%.2f", (binding.textTotalExpenseAmount.text.toString().toDouble() + amountChange))
        }
        val totalBalance = binding.textTotalIncomeAmount.text.toString().toDouble() -
                binding.textTotalExpenseAmount.text.toString().toDouble()
        binding.textTotalAmount.text = String.format("%.2f", totalBalance)
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
                val limitedTransactions = transactions.take(14)
                adapter.updateTransactions(limitedTransactions)
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.textTotalIncomeAmount.text = String.format("%.2f", income)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.textTotalExpenseAmount.text = String.format("%.2f", expense)
        }

        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.textTotalAmount.text = String.format("%.2f", balance)
        }
    }

    private fun openAddTransactionFragment(transaction: Transaction) {
        val fragment = AddTransactionFragment()
        val bundle = Bundle()
        bundle.putParcelable("transaction", transaction)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun updateName(name: String) {
        binding.textView.text = name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



