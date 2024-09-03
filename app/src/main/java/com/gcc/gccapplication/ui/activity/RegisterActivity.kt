package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.ActivityRegisterBinding
import com.gcc.gccapplication.viewModel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userPreferences: UserPreferences
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        userPreferences = UserPreferences(this)

//        enableEdgeToEdge() //

        binding.register.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val fullName = binding.etNama.text.toString().trim()
            val noHp = binding.etNoHp.text.toString().trim()
            val password = binding.etPass.text.toString().trim()
            val confirmPassword = binding.etRepass.text.toString().trim()
            val fcmToken = userPreferences.getFCMtoken().toString()

            userPreferences.saveNoHp(noHp)
            registerViewModel.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                noHp = noHp,
                confirmPassword = confirmPassword,
                onSuccess = {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )

        }

        binding.tvAkun.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
