package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Initialize UserPreferences and Firestore
        userPreferences = UserPreferences(this)

        // Get user data from UserPreferences
        val fullName = userPreferences.getFullName() ?: "Nama Tidak Ada"
        val email = userPreferences.getEmail() ?: "Email Tidak Ada"

        checkAddress(email)

        val homepageFragment = HomeFragment.newInstance(fullName)
        val historyFragment = HistoryFragment.newInstance()
        val profileFragment = ProfileFragment.newInstance(fullName, email)

        setCurrentFragment(homepageFragment)
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> setCurrentFragment(homepageFragment)
                R.id.nav_history -> setCurrentFragment(historyFragment)
                R.id.nav_person -> setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
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
