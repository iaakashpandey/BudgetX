package com.example.budgetx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetx.databinding.BottomSheetIncomeCategoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IncomeCategoryBottomSheet(private val listener: OnIncomeCategorySelectedListener) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetIncomeCategoryBinding? = null
    private val binding get() = _binding!!

    interface OnIncomeCategorySelectedListener {
        fun onIncomeCategorySelected(category: String, iconResId: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetIncomeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutSalary.setOnClickListener {
            listener.onIncomeCategorySelected("Salary", R.drawable.salary)
            dismiss()
        }

        binding.layoutBusiness.setOnClickListener {
            listener.onIncomeCategorySelected("Business", R.drawable.business)
            dismiss()
        }

        binding.layoutInvestment.setOnClickListener {
            listener.onIncomeCategorySelected("Investment", R.drawable.investment)
            dismiss()
        }

        binding.layoutOther.setOnClickListener {
            listener.onIncomeCategorySelected("Other", R.drawable.other)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

