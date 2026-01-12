package com.example.budgetx

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.budgetx.Database.DatabaseProvider
import com.example.budgetx.Database.Transaction
import com.example.budgetx.databinding.FragmentAddTransactionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionFragment : Fragment(),
    FromBottomSheetDialog.OnFromSelectedListener,
    ExpenseCategoryBottomSheet.OnExpenseCategorySelectedListener,
    IncomeCategoryBottomSheet.OnIncomeCategorySelectedListener {

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

    private fun getPaymentIcon(category: String): Int {
        return when (category) {
            "Cash" -> R.drawable.cash
            "Bank" -> R.drawable.bank
            else -> R.drawable.other
        }
    }

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var repository: TransactionRepository

    private var selectedDate: String = ""
    private var isExpenseSelected = true // Default to Expense
    private var selectedCategory: String? = null
    private var selectedFrom: String? = null

    private var transaction: Transaction? = null // For editing an existing transaction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        val transactionDao = DatabaseProvider.getDatabase(requireContext()).transactionDao()
        val splitTransactionDao = DatabaseProvider.getDatabase(requireContext()).splitTransactionDao()  // This should exist in your DatabaseProvider

        repository = TransactionRepository(transactionDao, splitTransactionDao)
        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(repository)
        )[TransactionViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transaction = arguments?.getParcelable("transaction") // Retrieve transaction if editing

        setupDefaultDate()
        setupListeners()

        // Prefill data if editing
        transaction?.let {
            prefillData(it)
        } ?: run {
            highlightButton(binding.btnExpense, binding.btnIncome) // Default to Expense
        }

        binding.btnSplit.setOnClickListener{
            val splitTransactionFragment = SplitTransactionFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, splitTransactionFragment)
                .addToBackStack(null)
                .commit()

        }
    }

    private fun prefillData(transaction: Transaction) {
        binding.editTextAmount.setText(transaction.amount.toString())
        binding.textDate.text = transaction.date
        binding.EditTextCategory.text = transaction.category
        binding.EditTextFrom.text = transaction.paymentMode
        binding.etNote.setText(transaction.note)
        binding.imageCategory.setImageResource(getCategoryIcon(transaction.category))
        binding.imageFrom.setImageResource(getPaymentIcon(transaction.paymentMode))

        selectedCategory = transaction.category
        selectedFrom = transaction.paymentMode
        selectedDate = transaction.date
        isExpenseSelected = transaction.type == "Expense"

        if (isExpenseSelected) {
            highlightButton(binding.btnExpense, binding.btnIncome)
        } else {
            highlightButton(binding.btnIncome, binding.btnExpense)
        }
    }

    private fun highlightButton(selectedButton: AppCompatButton, unselectedButton: AppCompatButton) {
        selectedButton.setBackgroundResource(R.drawable.rectangular_background_button)
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        unselectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        unselectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
    }

    private fun setupDefaultDate() {
        if (transaction == null) { // Set today's date only for new transactions
            val today = Calendar.getInstance()
            updateSelectedDate(today)
        }
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnExpense.setOnClickListener {
            isExpenseSelected = true
            selectedCategory = null
            binding.EditTextCategory.text = "Select Category"
            highlightButton(binding.btnExpense, binding.btnIncome)
        }

        binding.btnIncome.setOnClickListener {
            isExpenseSelected = false
            selectedCategory = null
            binding.EditTextCategory.text = "Select Category"
            highlightButton(binding.btnIncome, binding.btnExpense)
        }

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }

        binding.layoutFrom.setOnClickListener {
            val fromBottomSheet = FromBottomSheetDialog(this)
            fromBottomSheet.show(parentFragmentManager, "FromBottomSheet")
        }

        binding.layoutCategory.setOnClickListener {
            if (isExpenseSelected) {
                val expenseCategoryBottomSheet = ExpenseCategoryBottomSheet(this)
                expenseCategoryBottomSheet.show(parentFragmentManager, "ExpenseCategoryBottomSheet")
            } else {
                val incomeCategoryBottomSheet = IncomeCategoryBottomSheet(this)
                incomeCategoryBottomSheet.show(parentFragmentManager, "IncomeCategoryBottomSheet")
            }
        }

        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateSelectedDate(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateSelectedDate(calendar: Calendar) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = sdf.format(calendar.time)
        binding.textDate.text = selectedDate
    }

    private fun saveTransaction() {
        val amountText = binding.editTextAmount.text.toString()
        val note = binding.etNote.text.toString()
        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedFrom.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select a payment mode", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategory.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDouble()
        val type = if (isExpenseSelected) "Expense" else "Income"

        val newTransaction = transaction?.copy(
            amount = amount,
            type = type,
            date = selectedDate,
            category = selectedCategory!!,
            paymentMode = selectedFrom!!,
            note = note
        ) ?: Transaction(
            amount = amount,
            type = type,
            date = selectedDate,
            category = selectedCategory!!,
            paymentMode = selectedFrom!!,
            note = note
        )

        if (transaction == null) {
            transactionViewModel.insertTransaction(newTransaction)
        } else {
            transactionViewModel.updateTransaction(newTransaction)
        }

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onFromSelected(from: String, iconResId: Int) {
        selectedFrom = from
        binding.EditTextFrom.text = from
        binding.imageFrom.setImageResource(iconResId)
    }

    override fun onExpenseCategorySelected(category: String, iconResId: Int) {
        selectedCategory = category
        binding.EditTextCategory.text = category
        binding.imageCategory.setImageResource(iconResId)
    }

    override fun onIncomeCategorySelected(category: String, iconResId: Int) {
        selectedCategory = category
        binding.EditTextCategory.text = category
        binding.imageCategory.setImageResource(iconResId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





