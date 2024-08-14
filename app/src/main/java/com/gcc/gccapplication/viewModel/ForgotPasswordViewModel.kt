package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.security.MessageDigest

class ForgotPasswordViewModel : ViewModel() {
    private val _passwordChangeSuccess = MutableLiveData<Boolean>()
    val passwordChangeSuccess: LiveData<Boolean> get() = _passwordChangeSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendResetPassEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordChangeSuccess.postValue(true)
                } else {
                    _errorMessage.postValue(task.exception?.message ?: "Gagal mengirimkan email")
                }
            }
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    fun updatePassword(user: FirebaseUser, newPassword: String) {
        // Firebase requires plaintext password for updating
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordChangeSuccess.value = true
                } else {
                    _errorMessage.value = task.exception?.message ?: "Gagal memperbarui password"
                }
            }
    }

    fun handlePasswordReset(user: FirebaseUser, newPassword: String) {
        // Optionally hash the password for any internal use, but Firebase needs plaintext
        val hashedPassword = hashString(newPassword, "SHA-256")

        // You can store hashedPassword if needed, but to update in Firebase, use the plaintext password
        updatePassword(user, newPassword)
    }
}
