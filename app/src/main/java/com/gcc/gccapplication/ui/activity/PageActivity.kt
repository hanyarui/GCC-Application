package com.gcc.gccapplication.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityPageBinding
import com.gcc.gccapplication.ui.fargment.HomeFragment
import com.gcc.gccapplication.ui.fargment.ProfileFragment

class PageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("name", "User")
        val email = sharedPref.getString("email", "")

        val homepageFragment = HomeFragment.newInstance(fullName ?: "Nama Tidak Ada")
        val profileFragment = ProfileFragment.newInstance(fullName ?: "Nama Tidak Ada", email ?: "Email Tidak Ada")

        setCurrentFragment(homepageFragment)
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> setCurrentFragment(homepageFragment)
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
}