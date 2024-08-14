package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Menunda eksekusi untuk menunjukkan splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAndNavigate()
        }, 3000) // 3000 ms (3 detik) adalah durasi splash screen
    }

    private fun checkUserAndNavigate() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Jika pengguna sudah login, arahkan ke PageActivity
            val intent = Intent(this, PageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Jika pengguna belum login, arahkan ke ValidationActivity
            val intent = Intent(this, ValidationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
