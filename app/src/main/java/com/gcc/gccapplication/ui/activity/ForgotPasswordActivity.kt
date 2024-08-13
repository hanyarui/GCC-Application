package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gcc.gccapplication.R
import androidx.lifecycle.Observer
import com.gcc.gccapplication.databinding.ActivityForgotPasswordBinding
import com.gcc.gccapplication.viewModel.ForgotPasswordViewModel



class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var customTitle: TextView
    private val forgotPassswordViewModel : ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        supportActionBar?.hide()

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

        binding.btnResetPass.setOnClickListener{
            val email = binding.etEmail.text.toString().trim()
            if( email.isNotEmpty()) {
                forgotPassswordViewModel.sendResetPassEmail(email)

            }else{
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        // masih ada bug, pas udah ganti pass di link email,  pas mo  login  pake pass baru gabisa..
        forgotPassswordViewModel.passwordChangeSuccess.observe(this, Observer{ succes ->
            if (succes)  {
                Toast.makeText(this, "Email untuk reset password telah dikirim", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })


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


}