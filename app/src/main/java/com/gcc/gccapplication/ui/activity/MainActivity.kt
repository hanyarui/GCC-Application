package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.gcc.gccapplication.data.local.UserPreferences
//import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userPreferences = UserPreferences(this)

        // Menunda eksekusi untuk menunjukkan splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAndNavigate()
        }, 3000) // 3000 ms (3 detik) adalah durasi splash screen
    }

    private fun checkUserAndNavigate() {
        val currentUser = userPreferences.firebaseCurrrentUser()

        if (currentUser != null) {
//            currentUser != null
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
