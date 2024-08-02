package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.databinding.ActivityRegisterBinding
import com.gcc.gccapplication.viewModel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val fullName = binding.etNama.text.toString().trim()
            val password = binding.etPass.text.toString().trim()
            val confirmPassword = binding.etRepass.text.toString().trim()

            registerViewModel.registerUser(
                fullName = fullName,
                email = email,
                password = password,
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