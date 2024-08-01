package com.gcc.gccapplication.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(
        context: Context,
        email: String,
        password: String,
        fullName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val passwordHash = hashString(password, "SHA-256")
        auth.signInWithEmailAndPassword(email, passwordHash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val token = generateToken(email, passwordHash, fullName)
                        val userPreferences = UserPreferences(context)
                        userPreferences.saveToken(token)
                        userPreferences.saveEmail(email)
                        userPreferences.saveFullName(fullName)
                        onSuccess()
                    } else {
                        onFailure("User not found")
                    }
                } else {
                    onFailure("Login failed: ${task.exception?.message}")
                }
            }
    }

    private fun generateToken(email: String, passwordHash: String, fullName: String): String {
        val combinedString = email + passwordHash + fullName
        return hashString(combinedString, "SHA-256")
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}