package com.gcc.gccapplication.ui.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.databinding.ActivityMainBinding
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.gcc.gccapplication.data.local.UserPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.core.View
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

//import com.google.firebase.auth.FirebaseAuth
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    companion object{
        const val CHANNEL_ID = "notification_channel"
    }
//    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val notificationManager: NotificationManager by lazy{
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                // Izin diberikan
                Snackbar.make(
                    findViewById<android.view.View>(android.R.id.content).rootView,
                    "Berhasil Mendapatkan Izin Notifikasi", Snackbar.LENGTH_LONG
                ).show()
            }else{
                // Izin ditolak
                Snackbar.make(
                    findViewById<android.view.View>(android.R.id.content).rootView,
                    "Gagal Mendapatkan Izin Notifikasi", Snackbar.LENGTH_LONG
                ).show()
            }

        }

        userPreferences = UserPreferences(this)

        // Menunda eksekusi untuk menunjukkan splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAndNavigate()
        }, 3000) // 3000 ms (3 detik) adalah durasi splash screen

        reqPermission()
        createNotificationChannel()


    }

    private fun reqPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
            // Izin sudah diberikan
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }



    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Important Notification",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description="This Notification is Important annouchment" }
        notificationManager.createNotificationChannel(channel)
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

//    private suspend fun generateTokenFCM(): String? {
//        val token = try {
//            FirebaseMessaging.getInstance().token.await()
//        } catch (e: Exception) {
//            Log.w("FCM__TOKEN", "Fetching FCM token failed", e)
//            null
//        }
//        Log.d("FCM__TOKEN",token.toString())
//        return token
//    }


}
