package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

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
                    val exception = task.exception
                    if (exception != null) {
                        if (exception is FirebaseAuthInvalidUserException) {
                            _errorMessage.postValue("Email tidak terdaftar")
                        } else {
                            _errorMessage.postValue(exception.message ?: "Gagal mengirimkan email")
                        }
                    } else {
                        _errorMessage.postValue("Gagal mengirimkan email")
                    }
                }
            }
    }
}