package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityForgotPasswordBinding
import com.gcc.gccapplication.viewModel.ForgotPasswordViewModel
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var customTitle: TextView
    private val forgotPassswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate and set the custom title view
        val customView = layoutInflater.inflate(R.layout.actionbar_title, null)
        customTitle = customView.findViewById(R.id.custom_title)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.customView = customView

        customTitle.text = ""

        // Kirim email reset password
        binding.btnResetPass.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                forgotPassswordViewModel.sendResetPassEmail(email)
            } else {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe success state
        forgotPassswordViewModel.passwordChangeSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Email untuk reset password telah dikirim", Toast.LENGTH_SHORT).show()

                // Arahkan ke LoginActivity setelah email terkirim
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        // Observe error messages
        forgotPassswordViewModel.errorMessage.observe(this, Observer { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updatePassword(user: FirebaseUser, newPassword: String) {
        // Mengupdate password pengguna di Firebase menggunakan plaintext password
        forgotPassswordViewModel.updatePassword(user, newPassword)
    }

    private fun handlePasswordReset(newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            forgotPassswordViewModel.handlePasswordReset(it, newPassword)
        }
    }
}
