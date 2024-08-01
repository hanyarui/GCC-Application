package com.gcc.gccapplication.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityRegisterBinding
import com.gcc.gccapplication.viewModel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etNama = findViewById<EditText>(R.id.etNama)
        val etPass = findViewById<EditText>(R.id.etPass)
        val etRepass = findViewById<EditText>(R.id.etRepass)
        val btnRegister = findViewById<Button>(R.id.register)
        val tvAkun = findViewById<TextView>(R.id.tvAkun)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val fullName = etNama.text.toString().trim()
            val password = etPass.text.toString().trim()
            val confirmPassword = etRepass.text.toString().trim()

            registerViewModel.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onSuccess = {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to another activity or close the current one
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

        tvAkun.setOnClickListener {
            // Navigate to Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}