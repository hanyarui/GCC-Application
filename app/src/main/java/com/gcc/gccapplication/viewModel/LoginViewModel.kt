package com.gcc.gccapplication.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(
        context: Context,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val passwordHash = hashString(password, "SHA-256")
        auth.signInWithEmailAndPassword(email, passwordHash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Ambil UID pengguna
                        val uid = user.uid

                        // Ambil data role dari Firestore berdasarkan UID
                        firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val role = document.getString("role") ?: "user"
                                    val fullName = document.getString("name") ?: "Unknown"
                                    val address = document.getString("address") ?: "Unknown"

                                    // Logging address
                                    Log.d("LoginViewModel", "Saving address: $address")

                                    // Buat token yang mencakup email, passwordHash, fullName, dan role
                                    val token = generateToken(email, passwordHash, fullName, role)

                                    // Simpan token dan data lainnya ke SharedPreferences
                                    val userPreferences = UserPreferences(context)
                                    userPreferences.saveToken(token)
                                    userPreferences.saveEmail(email)
                                    userPreferences.saveFullName(fullName)
                                    userPreferences.saveRole(role)
                                    userPreferences.saveAddress(address)

                                    onSuccess()
                                } else {
                                    onFailure("Role not found for user")
                                }
                            }
                            .addOnFailureListener { e ->
                                onFailure("Failed to retrieve role: ${e.message}")
                            }
                    } else {
                        onFailure("User not found")
                    }
                } else {
                    onFailure("Login failed: ${task.exception?.message}")
                    println(task.exception?.message)
                }
            }
    }

    private fun generateToken(email: String, passwordHash: String, fullName: String, role: String): String {
        val combinedString = email + passwordHash + fullName + role
        return hashString(combinedString, "SHA-256")
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}
