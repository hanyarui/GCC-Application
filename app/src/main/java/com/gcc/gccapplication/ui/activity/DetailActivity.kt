package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityDetailBinding
import com.gcc.gccapplication.viewModel.DetailViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()
    private lateinit var customTitle: TextView

    companion object {
        const val EXTRA_TRASH_ID = "extra_trash_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar as the ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate and set the custom title view
        val customView = layoutInflater.inflate(R.layout.actionbar_title, null)
        customTitle = customView.findViewById(R.id.custom_title)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.customView = customView

        val trashId = intent.getStringExtra(EXTRA_TRASH_ID)
        if (trashId != null) {
            detailViewModel.getTrashDetail(trashId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        detailViewModel.trashDetail.observe(this) { trash ->
            if (trash != null) {
                // Set the custom title text to the trash name
                customTitle.text = trash.name

                binding.tvTrashDesc.text = trash.description
                binding.tvAlamat.text = trash.address
                Glide.with(this)
                    .load(trash.photoUrl)
                    .placeholder(R.drawable.img_dummy_image)
                    .into(binding.ivTrash)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
