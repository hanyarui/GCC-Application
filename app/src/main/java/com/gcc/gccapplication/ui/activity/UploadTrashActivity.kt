package com.gcc.gccapplication.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.API.ApiService
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.data.model.NotificationRequest
import com.gcc.gccapplication.databinding.ActivityTrashbagBinding
import com.gcc.gccapplication.databinding.ActivityUploadTrashBinding
import com.gcc.gccapplication.service.MyFirebaseMessagingService
import com.gcc.gccapplication.viewModel.UploadTrashViewModel
import com.yalantis.ucrop.UCrop
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadTrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadTrashBinding
    private var currentImageUri: Uri?=null
    private lateinit var customTitle: TextView
    private val viewModel: UploadTrashViewModel by viewModels()
    private val notification: MyFirebaseMessagingService = MyFirebaseMessagingService()
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userPreferences  = UserPreferences(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Inflate and set the custom title view
        val customView = layoutInflater.inflate(R.layout.actionbar_title, null)
        customTitle = customView.findViewById(R.id.custom_title)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.customView = customView
        customTitle.text = "Kirim Bukti Pengambilan Sampah"



        binding.ivPhotoSampah.setOnClickListener{startGallery()}

        binding.btnKonfirmasi.setOnClickListener{
            (viewModel.isInternetAvailable(this) && run { saveUploadData(); true }) || run { Toast.makeText(this, "No internet connection available.", Toast.LENGTH_SHORT).show(); false }
            //logic simpan data

        }


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

    private fun saveUploadData(){
        userPreferences = UserPreferences(this)
        val userFullName = binding.etUserName.text.toString()
        val userAddress = binding.etAlamat.text.toString()
        val phoneNumb = binding.etNomor.text.toString()
        val email = userPreferences.getEmail().toString()
        if(userFullName.isEmpty() || userAddress.isEmpty() || phoneNumb.isEmpty()){
            Toast.makeText(this, "Tolong masukkan data terlebih dulu", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.saveUploadData(
            userFullName,
            userAddress,
            phoneNumb,
            email,
            currentImageUri,
            onSuccess = {
                Toast.makeText(this, "Berhasil Untuk Menyimpan Data Upload", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                notification.sendNotification(userPreferences.getAddress()?: "","Halo Greeners!!", "ada sampah masuk nich dari $userFullName")
                finish()
            },
            onFailure = { e ->
                Toast.makeText(this, "Gagal Untuk Menyimpan data upload ${e.message}",Toast.LENGTH_SHORT).show()
            }
        )

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