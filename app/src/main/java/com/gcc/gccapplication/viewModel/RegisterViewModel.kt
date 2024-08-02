package com.gcc.gccapplication.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

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

        if (password != confirmPassword) {
            onFailure("Passwords do not match")
            return
        }

        val passwordHash = hashString(password, "SHA-256")
        auth.createUserWithEmailAndPassword(email, passwordHash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the UID from the created user
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        // Prepare the user data to be saved in Firestore
                        val userData = mapOf(
                            "uid" to uid,
                            "name" to fullName,
                            "email" to email,
                            "role" to "user"
                        )

                        // Save the UID and role in Firestore under the "users" collection
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

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}
