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
        noHp: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Lakukan validasi password
        val validationMessage = validatePassword(password, confirmPassword)
        if (validationMessage != null) {
            onFailure(validationMessage)
            return
        }

        // Melakukan registrasi dengan email dan password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        // Menyimpan data pengguna ke Firestore
                        val userData = mapOf(
                            "uid" to uid,
                            "name" to fullName,
                            "email" to email,
                            "phone_number" to noHp,
                            "role" to if (email.matches(Regex("^\\d{5,10}.*@webmail\\.uad\\.ac\\.id$"))) "admin" else "user",

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

    // Fungsi untuk validasi password
    private fun validatePassword(password: String, confirmPassword: String): String? {
        if (password.length < 8) {
            return "Password must be at least 8 characters long"
        }

        if (!password.any { it.isUpperCase() }) {
            return "Password must contain at least one uppercase letter"
        }

        if (password != confirmPassword) {
            return "Password and confirm password do not match"
        }

        return null // Tidak ada kesalahan
    }
}
