package com.example.budgetx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    private val _authError = MutableLiveData<String>()
    val authError: LiveData<String> get() = _authError

    private val _customUserId = MutableLiveData<String>()
    val customUserId: LiveData<String> get() = _customUserId

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Generate custom random user ID
    private fun generateRandomId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..5)
            .map { chars.random() }
            .joinToString("")
    }

    // Sign In with Google
    fun signInWithGoogle(account: GoogleSignInAccount?) {
        account?.let {
            val credential = GoogleAuthProvider.getCredential(it.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            val userId = user.uid
                            val customId = generateRandomId()

                            // Save custom user ID to Firestore
                            val userRef = firestore.collection("users").document(userId)
                            val userData = hashMapOf(
                                "customUserId" to customId,
                                "email" to user.email,
                                "name" to user.displayName
                            )

                            userRef.set(userData)
                                .addOnSuccessListener {
                                    _authStatus.postValue("Login successful!")
                                    _customUserId.postValue(customId)
                                }
                                .addOnFailureListener { e ->
                                    _authError.postValue("Error saving user data: ${e.message}")
                                }
                        }
                    } else {
                        _authError.postValue("Authentication failed: ${task.exception?.message}")
                    }
                }
        }
    }
}
