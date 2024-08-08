package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityChangePasswordBinding
import com.gcc.gccapplication.viewModel.ChangePasswordViewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar as the ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.register.setOnClickListener {
            val currentPassword = binding.etOldPass.text.toString().trim()
            val newPassword = binding.etNewPass.text.toString().trim()
            val confirmPassword = binding.etNewRepass.text.toString().trim()

            if (newPassword == confirmPassword) {
                changePasswordViewModel.changePassword(currentPassword, newPassword)
            } else {
                Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        changePasswordViewModel.passwordChangeSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal mengubah password", Toast.LENGTH_SHORT).show()
            }
        }

        changePasswordViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
