package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
            reauthenticateUser(it, currentPassword) { isReauthenticated ->
                if (isReauthenticated) {
                    updatePassword(it, newPassword)
                } else {
                    _errorMessage.value = "Reauthentication failed"
                }
            }
        } ?: run {
            _errorMessage.value = "No user is currently logged in"
        }
    }

    private fun reauthenticateUser(user: FirebaseUser, currentPassword: String, callback: (Boolean) -> Unit) {
        val email = user.email
        if (email != null) {
            // Reauthenticate user with the raw current password
            val credential: AuthCredential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful)
                }
        } else {
            callback(false)
        }
    }

    private fun updatePassword(user: FirebaseUser, newPassword: String) {
        // Update user's password with the raw new password
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordChangeSuccess.value = true
                } else {
                    _errorMessage.value = task.exception?.message ?: "Password update failed"
                }
            }
    }
}
