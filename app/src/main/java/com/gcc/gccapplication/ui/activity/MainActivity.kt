package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityMainBinding
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menunda eksekusi untuk menunjukkan splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkTokenAndNavigate()
        }, 3000) // 2000 ms (2 detik) adalah durasi splash screen

    }

    private fun checkTokenAndNavigate() {
        // Mengambil SharedPreferences
        val sharedPref = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token != null) {
            // Jika token ditemukan, arahkan ke PageActivity
            val intent = Intent(this, PageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Jika token tidak ditemukan, arahkan ke ValidationPage
            val intent = Intent(this, ValidationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}