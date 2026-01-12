package com.example.budgetx

import SplitTransactionAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetx.databinding.FragmentSplitTransactionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplitTransactionFragment : Fragment() {

    private var _binding: FragmentSplitTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SplitTransactionViewModel by activityViewModels()
    private var currentUserId: String? = null
    private lateinit var adapter: SplitTransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplitTransactionBinding.inflate(inflater, container, false)
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("SplitTransactionFragment", "FirebaseAuth returned null user ID")
            return binding.root
        }

        setupRecyclerView()
        observeViewModel()

        binding.fabAddPerson.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddPeopleFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnSplitAmount.setOnClickListener {
            splitTransaction()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle user selection from AddPeopleFragment
        parentFragmentManager.setFragmentResultListener("selectedUser", this) { _, bundle ->
            val userId = bundle.getString("selectedUserId")
            if (!userId.isNullOrEmpty()) {
                viewModel.searchUser(userId)  // Fetch user from Firestore
            } else {
                Log.e("SplitTransactionFragment", "Received empty userId from bundle")
                Toast.makeText(requireContext(), "Invalid User ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = SplitTransactionAdapter(mutableListOf())
        binding.recyclerViewSplitTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSplitTransactions.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (viewModel.addedUsers.value?.any { it.userId == user.userId } == false) {
                    viewModel.addUser(user)  // Properly add user to the list
                } else {
                    Toast.makeText(requireContext(), "User already added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.addedUsers.observe(viewLifecycleOwner) { users ->
            adapter.updateList(users.toMutableList())
        }
    }

    private fun splitTransaction() {
        val amountText = binding.etTotalAmount.text.toString()
        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val users = viewModel.addedUsers.value.orEmpty().toList()
        if (users.isEmpty()) {
            Toast.makeText(requireContext(), "No users to split with", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
            Log.e("SplitTransactionFragment", "User ID is null when splitting transaction")
            return
        }

        val splitAmount = amount / users.size
        val batch = FirebaseFirestore.getInstance().batch()
        val splitRef = FirebaseFirestore.getInstance().collection("splitTransactions")

        for (user in users) {
            val newDoc = splitRef.document()
            val transactionData = mapOf(
                "userId" to user.userId,
                "username" to user.userName,
                "amountOwed" to splitAmount,
                "splitBy" to currentUserId
            )
            batch.set(newDoc, transactionData)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Transaction split successfully", Toast.LENGTH_SHORT).show()
                viewModel.clearUsers()  // Clear users after splitting
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to split transaction", Toast.LENGTH_SHORT).show()
                Log.e("SplitTransactionFragment", "Failed to write split transaction to Firestore", it)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



