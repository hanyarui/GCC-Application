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
    private lateinit var ivTrashPhoto: Photo
    private lateinit var tvTrashName: TextView
    private lateinit var rvKeranjangSampah: RecyclerView
    private val trashViewModel: TrashbagViewModel by viewModels()
    private lateinit var trashAdapter: TrashbagAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashbagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar as the ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate and set the custom title view
        val customView = layoutInflater.inflate(R.layout.actionbar_title, null)
        customTitle = customView.findViewById(R.id.custom_title)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.customView = customView

        customTitle.text = "Keranjang Sampah"

        rvKeranjangSampah = findViewById(R.id.rvKeranjangSampah)
        setupRecyclerView()
        observeViewModel("Tamanan","Admin")
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.activity_trashbag, container, false)
//
//
//        rvKeranjangSampah = view.findViewById(R.id.rvKeranjangSampah    )
//
//        return view
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupRecyclerView() {
        trashAdapter = TrashbagAdapter(ArrayList())
        rvKeranjangSampah.apply {
            layoutManager = LinearLayoutManager(this@TrashbagActivity)
            adapter = trashAdapter
        }


    }

    private fun observeViewModel(userAddress: String, userRole: String) {
        trashViewModel.trashData.observe(this) { trashList ->
            if (trashList.isEmpty()) {
                Toast.makeText(this, "Belum ada data sampah", Toast.LENGTH_SHORT).show()
            }
            trashAdapter.listTrashbag.apply {
                clear()
                addAll(trashList)
            }
            trashAdapter.notifyDataSetChanged()
        }

        trashViewModel.fetchTrashData(userAddress, userRole)
    }
//            private fun setupRecyclerView() {
//                trashbagAdapter = TrashbagAdapter(ArrayList())
//                val recyclerView = findViewById<RecyclerView>(R.id.rvKeranjangSampah)
//                val emptyView = findViewById<TextView>(R.id.emptyView)
//
//                recyclerView.apply {
//                    layoutManager = LinearLayoutManager(this@TrashbagActivity)
//                    adapter = trashbagAdapter
//                }
//
//            }

}