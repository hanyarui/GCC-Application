package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.replace
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.ActivityPageBinding
import com.gcc.gccapplication.databinding.DialogAddAddressBinding
import com.gcc.gccapplication.ui.fragment.HistoryFragment
import com.gcc.gccapplication.ui.fragment.HomeFragment
import com.gcc.gccapplication.ui.fragment.ProfileFragment
import com.gcc.gccapplication.viewModel.PageViewModel

class PageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPageBinding
    private lateinit var userPreferences: UserPreferences
    private val pageViewModel: PageViewModel by viewModels()
    private var currentNavItemId: Int = R.id.nav_home


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Initialize UserPreferences and Firestore
        userPreferences = UserPreferences(this)
        val fcmToken = userPreferences.getFCMtoken()
        Log.d("FCM__Token", "Token FCM didapatkan: $fcmToken")
        // Get user data from UserPreferences
        val fullName = userPreferences.getFullName() ?: "Nama Tidak Ada"
        val email = userPreferences.getEmail() ?: "Email Tidak Ada"

        checkAddress(email)

        val homepageFragment = HomeFragment.newInstance(fullName)
        val historyFragment = HistoryFragment.newInstance(email)
        val profileFragment = ProfileFragment.newInstance(fullName, email)



        setDefaultFragment(homepageFragment)
        binding.bnvMain.setOnItemSelectedListener { item ->
            val newNavItemId = item.itemId
            val fragment: Fragment

            when (newNavItemId) {
                R.id.nav_home -> fragment = homepageFragment
                R.id.nav_history -> fragment = historyFragment
                R.id.nav_person -> fragment = profileFragment
                else -> return@setOnItemSelectedListener false
            }

            val enterAnim: Int
            val exitAnim: Int
            val popEnterAnim: Int
            val popExitAnim: Int

            // Tentukan animasi berdasarkan kombinasi tombol navigasi
            when (currentNavItemId to newNavItemId) {
                R.id.nav_home to R.id.nav_history -> {
                    enterAnim = R.anim.enter_from_right
                    exitAnim = R.anim.exit_to_left
                    popEnterAnim = R.anim.enter_from_left
                    popExitAnim = R.anim.exit_to_right
                }
                R.id.nav_history to R.id.nav_home -> {
                    enterAnim = R.anim.enter_from_left
                    exitAnim = R.anim.exit_to_right
                    popEnterAnim = R.anim.enter_from_right
                    popExitAnim = R.anim.exit_to_left
                }
                R.id.nav_home to R.id.nav_person -> {
                    enterAnim = R.anim.enter_from_right
                    exitAnim = R.anim.exit_to_left
                    popEnterAnim = R.anim.enter_from_left
                    popExitAnim = R.anim.exit_to_right
                }
                R.id.nav_person to R.id.nav_home -> {
                    enterAnim = R.anim.enter_from_left
                    exitAnim = R.anim.exit_to_right
                    popEnterAnim = R.anim.enter_from_right
                    popExitAnim = R.anim.exit_to_left
                }
                R.id.nav_history to R.id.nav_person -> {
                    enterAnim = R.anim.enter_from_right
                    exitAnim = R.anim.exit_to_left
                    popEnterAnim = R.anim.enter_from_left
                    popExitAnim = R.anim.exit_to_right
                }
                R.id.nav_person to R.id.nav_history -> {
                    enterAnim = R.anim.enter_from_left
                    exitAnim = R.anim.exit_to_right
                    popEnterAnim = R.anim.enter_from_right
                    popExitAnim = R.anim.exit_to_left
                }
                // Tambahkan kasus lain jika ada
                else -> {
                    enterAnim = R.anim.enter_from_right
                    exitAnim = R.anim.exit_to_left
                    popEnterAnim = R.anim.enter_from_left
                    popExitAnim = R.anim.exit_to_right
                }
            }

            setCurrentFragment(fragment, enterAnim, exitAnim, popEnterAnim, popExitAnim)
            currentNavItemId = newNavItemId
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment, enterAnim: Int, exitAnim: Int, popEnterAnim: Int, popExitAnim: Int) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                enterAnim,      // Animasi untuk masuk
                exitAnim,       // Animasi untuk keluar
                popEnterAnim,   // Animasi untuk masuk saat kembali
                popExitAnim     // Animasi untuk keluar saat kembali
            )
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun setDefaultFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }


    private fun checkAddress(email: String) {
        pageViewModel.checkAddressData(email) { addressExists ->
            if (!addressExists) {
                showAddressDialog(email)
            }
        }
    }

    private fun showAddressDialog(email: String) {
        val dialogBinding = DialogAddAddressBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Tambah Alamat")
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton("Konfirmasi") { _, _ ->
                val address = dialogBinding.etAddress.text.toString()
                if (address.isNotEmpty()) {
                    pageViewModel.saveAddressData(this, email, address,
                        onSuccess = {
                            // Address saved successfully
                            userPreferences.saveAddress(address)  // Save address to UserPreferences
                            Toast.makeText(this, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                            // Restart the activity
                            recreate()
                        },
                        onFailure = { exception ->
                            // Handle error
                            Toast.makeText(this, "Alamat Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                        })
                } else {
                    Toast.makeText(this, "Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }
}
