package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.security.MessageDigest

class ChangePasswordViewModel : ViewModel() {

    private val _passwordChangeSuccess = MutableLiveData<Boolean>()
    val passwordChangeSuccess: LiveData<Boolean> get() = _passwordChangeSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun changePassword(currentPassword: String, newPassword: String) {
        if (newPassword.length < 8) {
            _errorMessage.value = "New password must be at least 8 characters long"
            return
        }

        val user = auth.currentUser
        user?.let {
            val currentPasswordHash = hashString(currentPassword, "SHA-256")
            reauthenticateUser(it, currentPasswordHash) { isReauthenticated ->
                if (isReauthenticated) {
                    val newPasswordHash = hashString(newPassword, "SHA-256")
                    updatePassword(it, newPasswordHash)
                } else {
                    _errorMessage.value = "Reauthentication failed"
                }
            }
        } ?: run {
            _errorMessage.value = "No user is currently logged in"
        }
    }

    private fun reauthenticateUser(user: FirebaseUser, currentPasswordHash: String, callback: (Boolean) -> Unit) {
        val email = user.email
        if (email != null) {
            val credential: AuthCredential = EmailAuthProvider.getCredential(email, currentPasswordHash)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful)
                }
        } else {
            callback(false)
        }
    }

    private fun updatePassword(user: FirebaseUser, newPasswordHash: String) {
        user.updatePassword(newPasswordHash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordChangeSuccess.value = true
                } else {
                    _errorMessage.value = task.exception?.message ?: "Password update failed"
                }
            }
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest.getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}
