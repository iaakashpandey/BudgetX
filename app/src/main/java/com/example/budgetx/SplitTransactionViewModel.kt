package com.example.budgetx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SplitTransactionViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _selectedUser = MutableLiveData<Person?>()
    val selectedUser: LiveData<Person?> get() = _selectedUser

    private val _addedUsers = MutableLiveData<List<Person>>(emptyList())  // Use List<Person>
    val addedUsers: LiveData<List<Person>> get() = _addedUsers

    fun searchUser(userId: String) {
        firestore.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val user = Person(
                        document.getString("userId") ?: "",
                        document.getString("name") ?: "Unknown"
                    )
                    _selectedUser.value = user
                } else {
                    _selectedUser.value = null
                }
            }
            .addOnFailureListener {
                _selectedUser.value = null
            }
    }

    fun addUser(user: Person) {
        val currentList = _addedUsers.value.orEmpty().toMutableList() // Convert to mutable list
        if (currentList.none { it.userId == user.userId }) {
            currentList.add(user)
            _addedUsers.value = currentList.toList() // Assign a new list to trigger LiveData updates
        }
    }

    fun removeUser(user: Person) {
        val currentList = _addedUsers.value.orEmpty().toMutableList()
        currentList.removeAll { it.userId == user.userId }
        _addedUsers.value = currentList.toList()
    }

    fun clearUsers() {
        _addedUsers.value = emptyList()
    }
}


