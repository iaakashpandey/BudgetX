package com.example.budgetx

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetx.databinding.FragmentNameBinding

class NameFragment : Fragment() {

    private var _binding : FragmentNameBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNameBinding.inflate(inflater, container, false)
        return binding.root
         }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val savedName = sharedPreferences.getString("name", "")
        binding.editTextName.setText(savedName)



        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveNameInPreferences(name)
                updateHomeFragmentText(name)
                navigateBackToSettingFragment()
            }
        }
    }
    private fun updateHomeFragmentText(name: String) {
        val homeFragment = parentFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) as? HomeFragment
        homeFragment?.updateName(name)
    }
    private fun saveNameInPreferences(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.apply()

    }
    private fun navigateBackToSettingFragment() {
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}