package com.example.budgetx

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetx.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Request code for Google sign-in

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Make sure to add web client ID in strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up click listener for Google Sign-In button
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Toast.makeText(this, "Sign-in successful", Toast.LENGTH_SHORT).show()
                        generateCustomUserId(user.uid)
                    }
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun generateCustomUserId(firebaseUserId: String) {
        // Generate a random user ID (or use any logic you prefer)
        val customUserId = generateRandomUserId()

        // Save the custom user ID to Firestore along with other user details
        val user = hashMapOf(
            "userId" to customUserId,
            "firebaseUserId" to firebaseUserId,
            "email" to auth.currentUser?.email,
            "name" to auth.currentUser?.displayName
        )

        db.collection("users")
            .document(firebaseUserId)  // Use Firebase UID as the document ID
            .set(user)
            .addOnSuccessListener {
                // Custom user ID saved successfully, navigate to MainActivity
                Toast.makeText(this, "User ID saved, navigating to MainActivity", Toast.LENGTH_SHORT).show()
                navigateToMain(customUserId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving user ID: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateRandomUserId(): String {
        // Generate a random user ID (this can be modified based on your needs)
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..5).map { chars.random() }.joinToString("")
    }

    private fun navigateToMain(customUserId: String) {

        Log.d("LoginActivity", "Navigating to MainActivity with customUserId: $customUserId")
        // Pass the custom user ID to MainActivity via Intent
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_ID", customUserId)
        }
        startActivity(intent)
        finish()  // Close the LoginActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}



