package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityCreateTrashBinding
import com.gcc.gccapplication.viewModel.CreateTrashViewModel
import com.yalantis.ucrop.UCrop
import java.io.File

class CreateTrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTrashBinding
    private var currentImageUri: Uri? = null
    private val viewModel: CreateTrashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivPhotoSampah.setOnClickListener { startGallery() }

        // Set spinner adapter
        val spinner: Spinner = binding.spinnerTipe
        ArrayAdapter.createFromResource(
            this,
            R.array.array_tipe_sampah,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Handle save button click
        binding.btnLogin.setOnClickListener { saveTrashData() }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                currentImageUri = uri
                startUCrop(uri)
            }
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val file = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, file))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                binding.ivPhotoSampah.setImageURI(it)
                currentImageUri = it
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(this, "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTrashData() {
        val trashName = binding.etTrashName.text.toString()
        val trashType = binding.spinnerTipe.selectedItem.toString()
        val trashDesc = binding.etDesc.text.toString()
        val trashAddress = binding.etAlamat.text.toString()

        if (trashName.isEmpty() || trashType.isEmpty() || trashDesc.isEmpty() || trashAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.saveTrashData(
            trashName,
            trashType,
            trashDesc,
            trashAddress,
            currentImageUri,
            onSuccess = {
                Toast.makeText(this, "Trash data saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = { e ->
                Toast.makeText(this, "Failed to save trash data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
