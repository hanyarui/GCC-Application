package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.ActivityValidationBinding
import com.google.firebase.messaging.FirebaseMessaging

class ValidationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValidationBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userPreferences = UserPreferences(this)

        setupListeners()
        createFCMToken()
    }

    private fun setupListeners() {
        binding.tvSignUp.setOnClickListener {
            navigateToLoginActivity()
        }

        binding.btnMulai.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun createFCMToken(){
        val TAG ="FCM__TOKEN"
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
            if(!task.isSuccessful){
                Log.w(TAG,"Fetching FCM token failed",task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            userPreferences.setFCMtoken(token)
            Log.d(TAG,token)
        }
    }
}
