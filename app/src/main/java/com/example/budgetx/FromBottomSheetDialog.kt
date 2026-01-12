package com.example.budgetx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetx.databinding.BottomSheetFromBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FromBottomSheetDialog(private val listener: OnFromSelectedListener) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFromBinding? = null
    private val binding get() = _binding!!

    interface OnFromSelectedListener {
        fun onFromSelected(from: String, iconResId: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFromBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutCash.setOnClickListener {
            listener.onFromSelected("Cash", R.drawable.cash)
            dismiss()
        }

        binding.layoutBank.setOnClickListener {
            listener.onFromSelected("Bank", R.drawable.bank)
            dismiss()
        }

        binding.layoutOther.setOnClickListener {
            listener.onFromSelected("Other", R.drawable.other)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

