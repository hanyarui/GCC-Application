package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityDetailBinding
import com.gcc.gccapplication.databinding.DialogTrashAmountBinding
import com.gcc.gccapplication.viewModel.DetailViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var dialogBinding: DialogTrashAmountBinding
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val EXTRA_TRASH_ID = "extra_trash_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogBinding = DialogTrashAmountBinding.inflate(layoutInflater)

        val trashId = intent.getStringExtra(EXTRA_TRASH_ID)
        if (trashId != null) {
            detailViewModel.getTrashDetail(trashId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        detailViewModel.trashDetail.observe(this) { trash ->
            if (trash != null) {
                binding.tvTrashName.text = trash.name
                binding.tvTrashDesc.text = trash.description
                binding.tvAlamat.text = trash.address
                Glide.with(this)
                    .load(trash.photoUrl)
                    .placeholder(R.drawable.img_dummy_image)
                    .into(binding.ivTrash)
            }
        }
    }
}
