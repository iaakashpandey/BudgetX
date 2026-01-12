package com.example.budgetx

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetx.databinding.FragmentAddPeopleBinding
import com.google.firebase.firestore.FirebaseFirestore

    class AddPeopleFragment : Fragment() {

        private var _binding: FragmentAddPeopleBinding? = null
        private val binding get() = _binding!!
        private val viewModel: SplitTransactionViewModel by activityViewModels() // Use existing ViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentAddPeopleBinding.inflate(inflater, container, false)

            setupObservers()
            setupClickListeners()

            return binding.root
        }

        private fun setupObservers() {
            viewModel.selectedUser.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.textUserName.text = "User Found: ${user.userName}"
                    binding.textUserName.visibility = View.VISIBLE
                    binding.btnAddUser.visibility = View.VISIBLE
                } else {
                    binding.textUserName.text = "User not found!"
                    binding.textUserName.visibility = View.VISIBLE
                    binding.btnAddUser.visibility = View.GONE
                }
            }
        }

        private fun setupClickListeners() {
            binding.btnSearch.setOnClickListener {
                val enteredUserId = binding.textInputSearch.text.toString().trim()
                if (enteredUserId.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a User ID", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.searchUser(enteredUserId)
                }
            }

            binding.btnAddUser.setOnClickListener {
                viewModel.selectedUser.value?.let { user ->
                    val resultBundle = Bundle().apply {
                        putString("selectedUserId", user.userId)
                        putString("selectedUsername", user.userName)
                    }
                    parentFragmentManager.setFragmentResult("selectedUser", resultBundle)
                    parentFragmentManager.popBackStack()
                } ?: Toast.makeText(requireContext(), "No user selected!", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
