package com.example.budgetx

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.budgetx.Database.Transaction
import com.example.budgetx.Database.TransactionDatabase
import com.example.budgetx.databinding.FragmentGraphBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val database = TransactionDatabase.getDatabase(requireContext())
        val repository = TransactionRepository(database.transactionDao(), database.splitTransactionDao())
        viewModel = ViewModelProvider(requireActivity(), TransactionViewModelFactory(repository))
            .get(TransactionViewModel::class.java)

        // Observe data and set up UI
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            updateStats(transactions)
            setupPieChart(transactions)
        }
    }

    private fun updateStats(transactions: List<Transaction>) {
        var totalIncome = 0.0
        var totalExpense = 0.0

        transactions.forEach { transaction ->
            if (transaction.type == "Income") {
                totalIncome += transaction.amount
            } else {
                totalExpense += transaction.amount
            }
        }

        val totalBalance = totalIncome - totalExpense

        binding.textTotalBalance.text = "Total Balance: $${"%.2f".format(totalBalance)}"
        binding.textIncome.text = "Income: $${"%.2f".format(totalIncome)}"
        binding.textExpense.text = "Expense: $${"%.2f".format(totalExpense)}"
    }

    private fun setupPieChart(transactions: List<Transaction>) {
        val categoryMap = mutableMapOf<String, Float>()

        // Aggregate data by category
        transactions.forEach { transaction ->
            categoryMap[transaction.category] =
                categoryMap.getOrDefault(transaction.category, 0f) + transaction.amount.toFloat()
        }

        // Prepare PieEntry list
        val entries = categoryMap.map { (category, amount) ->
            PieEntry(amount, category)
        }

        // Create PieDataSet
        val pieDataSet = PieDataSet(entries, "Categories")

        // Define colors for PieChart
        val colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.greenPie),
            ContextCompat.getColor(requireContext(), R.color.bluePie),
            ContextCompat.getColor(requireContext(), R.color.violetPie),
            ContextCompat.getColor(requireContext(), R.color.yellowPie),
            ContextCompat.getColor(requireContext(), R.color.orangePie),
            ContextCompat.getColor(requireContext(), R.color.redPie),
            ContextCompat.getColor(requireContext(), R.color.pinkPie),
            ContextCompat.getColor(requireContext(), R.color.darkBluePie)
        ).take(entries.size) // Ensure the color list matches the number of entries

        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.TRANSPARENT
        pieDataSet.valueTextSize = 14f

        // Set the space between slices
        pieDataSet.sliceSpace = 4f // Change this value to control the space

        // Create PieData
        val pieData = PieData(pieDataSet)

        // Configure PieChart
        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 70f
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.TRANSPARENT)
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(14f)
            setDrawEntryLabels(false)
            legend.isEnabled = false // Disable the default legend
            invalidate()
        }

        // Update the textSelectedCategory TextView with categories and amounts
        val categoriesText = categoryMap.map { (category, amount) ->
            "$category: $${"%.2f".format(amount)}"
        }.joinToString("\n")

        binding.textSelectedCategory.text = categoriesText

        binding.textSelectedCategory.setLineSpacing(40f, 1f)

        // Manually add a custom legend below the PieChart (only category names and colors)
        binding.legendContainer.apply {
            // Remove all views before adding new ones
            removeAllViews()

            // Set LinearLayout to vertical orientation for the legend
            orientation = LinearLayout.VERTICAL

            // Add each category to the legend with color indicators (without amounts)
            categoryMap.keys.zip(colors) { category, color ->
                val legendItem = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = 26 // Add spacing on the left
                        marginEnd = 16 // Add spacing on the right
                        topMargin = 16
                    bottomMargin = 10// Add spacing between items
                    }

                    val colorView = View(requireContext()).apply {
                        setBackgroundColor(color)
                        layoutParams = LinearLayout.LayoutParams(
                            32, 32 // Color indicator size
                        ).apply {
                            marginEnd = 32 // Spacing between color and text
                        }
                    }

                    val textView = TextView(requireContext()).apply {
                        text = category
                        setTextColor(Color.WHITE)
                        textSize = 12f
                    }

                    addView(colorView)
                    addView(textView)
                }

                addView(legendItem)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
