package com.example.budgetx

import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetx.Adapter.RecyclerViewAdapter
import com.example.budgetx.Database.Transaction
import com.example.budgetx.Database.TransactionDatabase
import com.example.budgetx.databinding.FragmentTransactionBinding
import com.google.android.material.chip.ChipGroup
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TransactionViewModel
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var chipGroupFilter: ChipGroup
    private var lastDeletedTransaction: Transaction? = null
    private var lastDeletedPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipGroupFilter = binding.chipGroupFilter

        // Initialize the repository and ViewModel
        val application = requireActivity().application
        val database = TransactionDatabase.getDatabase(requireContext())
        val repository = TransactionRepository(database.transactionDao(), database.splitTransactionDao())
        viewModel = ViewModelProvider(requireActivity(), TransactionViewModelFactory(repository))
            .get(TransactionViewModel::class.java)

        setupRecyclerView()
        setupObserver()
        setupSearchView()
        setupChipGroupFilter()
    }

    private fun setupSearchView() {
        binding.layoutSearch.setOnClickListener {
            binding.textInputSearch.requestFocus()
        }

        binding.textInputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filterTransactions(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupChipGroupFilter() {
        chipGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            filterTransactions(binding.textInputSearch.text.toString())

            // Update chip styles dynamically
            group.forEach { chip ->
                val chipView = chip as com.google.android.material.chip.Chip // Cast to Chip
                if (chip.id == checkedId) {
                    // Apply selected styles: White background and black text
                    chipView.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.white_dark)
                    chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                } else {
                    // Apply unselected styles: Black background and white text
                    chipView.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.black)
                    chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_dark))
                }
            }
        }

        // Set default selection to "All" at the start
        chipGroupFilter.check(R.id.chipAll)
    }

    private fun filterTransactions(query: String) {
        val allTransactions = viewModel.allTransactions.value.orEmpty()

        // Filter based on chip selection
        val filteredTransactions = when (chipGroupFilter.checkedChipId) {
            R.id.chipIncome -> allTransactions.filter { it.type == "Income" }
            R.id.chipExpense -> allTransactions.filter { it.type == "Expense" }
            R.id.chipAll -> allTransactions
            else -> allTransactions
        }

        // Further filter based on the search query
        val finalFilteredTransactions = filteredTransactions.filter {
            it.category.contains(query, ignoreCase = true) ||
                    it.paymentMode.contains(query, ignoreCase = true)
        }

        // Update the RecyclerView
        recyclerViewAdapter.filterList(finalFilteredTransactions)
    }

    private fun setupObserver() {
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("TransactionFragment", "Transactions: $transactions")
            filterTransactions(binding.textInputSearch.text.toString()) // Refresh filter whenever data changes
        }
    }

    private fun setupRecyclerView() {
        recyclerViewAdapter = RecyclerViewAdapter(listOf()) { transaction ->
            val fragment = AddTransactionFragment()
            val bundle = Bundle().apply {
                putParcelable("transaction", transaction)
            }
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerViewTransactionFragment.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // No vertical movement
            ItemTouchHelper.LEFT // Swipe only left
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transactionToDelete = recyclerViewAdapter.getItemAtPosition(position)
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
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTransactionFragment)
    }

    private fun deleteTransactionWithUndo(transaction: Transaction, position: Int) {
        lastDeletedTransaction = transaction
        lastDeletedPosition = position

        viewModel.deleteTransaction(transaction)

        binding.layoutUndo.visibility = View.VISIBLE
        binding.textUndoMessage.text = "Transaction deleted"

        binding.textUndoBottom.setOnClickListener {
            undoDelete()
        }

        recyclerViewAdapter.removeItemAtPosition(position)

        binding.root.postDelayed({
            binding.layoutUndo.visibility = View.GONE
        }, 5000)
    }

    private fun undoDelete() {
        lastDeletedTransaction?.let {
            viewModel.insertTransaction(it)
            recyclerViewAdapter.addItemAtPosition(lastDeletedPosition, it)
            binding.layoutUndo.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

