package com.gcc.gccapplication.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        // Login with the raw password (no manual hashing)
        auth.signInWithEmailAndPassword(email, password)
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
                                    val fcmToken = document.getString("fcmToken")?: "Unknown"


                                    // Simpan token dan data lainnya ke SharedPreferences
                                    val userPreferences = UserPreferences(context)
                                    userPreferences.saveEmail(email)
                                    userPreferences.saveFCMtoken(fcmToken)
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
                }
            }
    }
}
