package com.gcc.gccapplication.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            onFailure("Please fill in all fields")
            return
        }

        if (password.length < 8) {
            onFailure("Password must be at least 8 characters long")
            return
        }

        if (password != confirmPassword) {
            onFailure("Passwords do not match")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        val userData = mapOf(
                            "uid" to uid,
                            "name" to fullName,
                            "email" to email,
                            "role" to "user",
                            "address" to ""
                        )

                        firestore.collection("users").document(uid).set(userData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure("Failed to save user data: ${e.message}")
                            }
                    } else {
                        onFailure("Failed to retrieve user UID")
                    }
                } else {
                    onFailure("Registration failed: ${task.exception?.message}")
                }
            }
    }
}
