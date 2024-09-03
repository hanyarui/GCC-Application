package com.gcc.gccapplication.ui.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gcc.gccapplication.databinding.ActivityMainBinding
import com.gcc.gccapplication.data.local.UserPreferences
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    companion object {
        const val CHANNEL_ID = "notification_channel"
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                createNotificationChannel() // Buat saluran notifikasi setelah izin diberikan
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

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

        reqPermission()

    }

    private fun reqPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
            // Izin sudah diberikan
            createNotificationChannel()
        } else {
            // Meminta izin
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Important Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "This Notification is Important Announcement"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkUserAndNavigate() {
        val currentUser = userPreferences.firebaseCurrrentUser()
        val currentEmail = userPreferences.getEmail()

        if (currentUser != null) {
            // Jika pengguna sudah login, arahkan ke PageActivity
            val intent = Intent(this, PageActivity::class.java)
            startActivity(intent)
        } else {

            // Jika pengguna belum login, arahkan ke ValidationActivity
            val intent = Intent(this, ValidationActivity::class.java)
            startActivity(intent)
        }
        finish() // Tutup aktivitas utama setelah berpindah
    }
}
