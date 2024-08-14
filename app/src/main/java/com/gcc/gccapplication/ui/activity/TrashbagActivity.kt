package com.gcc.gccapplication.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.TrashAdapter
import com.gcc.gccapplication.databinding.ActivityTrashbagBinding
import com.gcc.gccapplication.adapter.TrashbagAdapter
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.data.model.TrashbagModel
import com.gcc.gccapplication.viewModel.TrashbagViewModel
import com.gcc.gccapplication.ui.activity.DetailActivity
import com.gcc.gccapplication.ui.fragment.HomeFragment
import com.gcc.gccapplication.ui.fragment.HomeFragment.Companion
import com.gcc.gccapplication.viewModel.HomeViewModel


class TrashbagActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrashbagBinding
    private lateinit var customTitle: TextView
    private lateinit var rvKeranjangSampah: RecyclerView
    private val trashViewModel: TrashbagViewModel by viewModels()
    private lateinit var trashAdapter: TrashbagAdapter
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashbagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UserPreferences
        userPreferences = UserPreferences(this)

        // Set up the Toolbar as the ActionBar
        setupToolbar()

        // Set up RecyclerView
        setupRecyclerView()

        // Observe the ViewModel
        observeViewModel()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            customView = layoutInflater.inflate(R.layout.actionbar_title, null).apply {
                customTitle = findViewById(R.id.custom_title)
                customTitle.text = "Keranjang Sampah"
            }
        }
    }

    private fun setupRecyclerView() {
        trashAdapter = TrashbagAdapter(ArrayList())
        rvKeranjangSampah = findViewById(R.id.rvKeranjangSampah)
        rvKeranjangSampah.apply {
            layoutManager = LinearLayoutManager(this@TrashbagActivity)
            adapter = trashAdapter
        }
    }

    private fun observeViewModel() {
        val user = userPreferences.getUserData()
        user?.let {
            // Fetch trash data based on user email or other attribute
            trashViewModel.fetchTrashData(it.email)  // Misalnya menggunakan email
        }

        // Observe trash data from ViewModel
        trashViewModel.trashData.observe(this) { trashList ->
            if (trashList.isEmpty()) {
                Toast.makeText(this, "Belum ada data sampah", Toast.LENGTH_SHORT).show()
            } else {
                trashAdapter.listTrashbag.apply {
                    clear()
                    addAll(trashList)
                }
                trashAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}