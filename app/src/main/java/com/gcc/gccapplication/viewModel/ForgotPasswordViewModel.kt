package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ForgotPasswordViewModel : ViewModel() {
    private val _passwordForgetSuccess = MutableLiveData<Boolean>()
    val passwordChangeSuccess: LiveData<Boolean> get() = _passwordForgetSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendResetPassEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordForgetSuccess.postValue(true)

                } else {
                    _errorMessage.postValue(task.exception?.message ?: "Gagal mengirimkan email")
                }
            }
    }

    fun reauthenticateUser(user: FirebaseUser, password: String, callback: (Boolean) -> Unit) {
        val credential: AuthCredential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }
}
