package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.AngkutModelAdapter
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.ActivityHistoryBinding
import com.gcc.gccapplication.viewModel.HistoryViewModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var rvKeranjangSampah: RecyclerView
    private lateinit var userPreferences: UserPreferences
    private lateinit var angkutModelAdapter: AngkutModelAdapter
    private val historyViewModel   : HistoryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        angkutModelAdapter = AngkutModelAdapter(ArrayList())
        rvKeranjangSampah = findViewById(R.id.rvKeranjangSampah)
        rvKeranjangSampah.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = angkutModelAdapter
        }

    }

    private var isDataEmpty = true
    private fun observeViewModel() {
        val user = userPreferences.getUserData()
        user?.let {
            // Fetch trash data based on user email or other attribute
            historyViewModel.fetchTrashData(it.email)  // Misalnya menggunakan email
        }

        // Observe trash data from ViewModel
//        historyViewModel.angkutData.observe(this) { trashList ->
//            isDataEmpty = trashList.isEmpty()
//            if (isDataEmpty) {
//                Toast.makeText(this, "Belum ada data sampah", Toast.LENGTH_SHORT).show()
//            } else {
//                angkutModelAdapter.listAngkut.apply {
//                    clear()
//                    addAll(trashList)
//                }
//                angkutModelAdapter.notifyDataSetChanged()
//            }
//        }
    }



}