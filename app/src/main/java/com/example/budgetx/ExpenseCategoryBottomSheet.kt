package com.example.budgetx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetx.databinding.BottomSheetExpenseCategoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExpenseCategoryBottomSheet(private val listener: OnExpenseCategorySelectedListener) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetExpenseCategoryBinding? = null
    private val binding get() = _binding!!

    interface OnExpenseCategorySelectedListener {
        fun onExpenseCategorySelected(category: String, iconResId: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetExpenseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutFood.setOnClickListener {
            listener.onExpenseCategorySelected("Food", R.drawable.food)
            dismiss()
        }

        binding.layoutRent.setOnClickListener {
            listener.onExpenseCategorySelected("Rent", R.drawable.rent)
            dismiss()
        }

        binding.layoutTravel.setOnClickListener {
            listener.onExpenseCategorySelected("Travel", R.drawable.travel)
            dismiss()
        }

        binding.layoutOther.setOnClickListener {
            listener.onExpenseCategorySelected("Other", R.drawable.other)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


