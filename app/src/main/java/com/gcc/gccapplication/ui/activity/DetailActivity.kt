package com.gcc.gccapplication.ui.activity

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.ActivityDetailBinding
import com.gcc.gccapplication.viewModel.DetailViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()
//    private val trashbagViewModel: TrashbagViewModel by viewModels()

    private lateinit var customTitle: TextView
    private lateinit var btnPlus: ImageButton
    private lateinit var btnMinus: ImageButton
    private lateinit var etJumlahSampah: TextInputEditText
    private lateinit var lytBtnAngkut : ConstraintLayout
    private lateinit var lytBtnKumpul : ConstraintLayout
    private lateinit var userPreferences: UserPreferences

    companion object {
        const val EXTRA_TRASH_ID = "extra_trash_id"
        const val ARG_EMAIL = "email"
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

        plusMinusBtn()
        val trashId = intent.getStringExtra(EXTRA_TRASH_ID)
        if (trashId != null) {
            detailViewModel.getTrashDetail(trashId)
        }

        btnKumpulAngkut()
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

    private fun plusMinusBtn(){
        //set button plus minus
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        etJumlahSampah = findViewById(R.id.etJumlahSampah)


        btnPlus.setOnClickListener{
            val currentVal = etJumlahSampah.text.toString().toDoubleOrNull()  ?: 0.0
            val newVal = currentVal + 0.5
            etJumlahSampah.setText(newVal.toString())

        }

        btnMinus.setOnClickListener{
            val currentVal = etJumlahSampah.text.toString().toDoubleOrNull()  ?: 0.0
            val newVal = if (currentVal > 0) currentVal - 0.5 else 0.0
            etJumlahSampah.setText(newVal.toString())
        }
    }

    private fun btnKumpulAngkut(){
        lytBtnAngkut = findViewById((R.id.lytBtnAngkut))
        lytBtnKumpul = findViewById((R.id.lytBtnAturUlang))

        lytBtnAngkut.setOnClickListener{
            angkutSampah()
            finish()
        }

        lytBtnKumpul.setOnClickListener{
            kumpulSampah()
//            finish()
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    private fun kumpulSampah(){
        val calendar = Calendar.getInstance()
//        val id = intent.getStringExtra(EXTRA_TRASH_ID)
        val trashId = intent.getStringExtra(EXTRA_TRASH_ID) ?: return
        val trashAmount =  binding.etJumlahSampah.text.toString()
        val dateFormat = java.text.SimpleDateFormat("EEEE, yyyy-MM-dd HH:mm", Locale("id","ID"))
        val trashTime = dateFormat.format(Calendar.getInstance().time)
        userPreferences = UserPreferences(this)
        val email = userPreferences.getEmail()

        if(trashAmount.isEmpty() || trashAmount == "0.0"){
            Toast.makeText(this, "Masukkan jumlah sampah terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        if (email!= null) {
            detailViewModel.kumpulSampah(
            trashId,
            trashAmount,
            trashTime,
            email,
            onSuccess = {
                Toast.makeText(this, "Berhasil memasukkan ke keranjang", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = { e ->
                Toast.makeText(this, "Gagal memasukkan ke keranjang: ${e.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun angkutSampah(){
        val trashId = intent.getStringExtra(EXTRA_TRASH_ID) ?: return
        val trashAmount = binding.etJumlahSampah.text.toString()
        val dateFormat = java.text.SimpleDateFormat("EEEE, yyyy-MM-dd HH:mm", Locale("id","ID"))
        val trashTime = dateFormat.format(Calendar.getInstance().time)
        userPreferences = UserPreferences(this)
        val email = userPreferences.getEmail()


        if (email != null) {
            detailViewModel.angkutSampah(
                trashId,
                trashAmount,
                trashTime,
                email,
                onSuccess = {
                    detailViewModel.hapusDokumenTrash(
                        trashId,
                        onSuccess = {
                            Toast.makeText(this, "Sampah berhasil diangkut dan dokumen dihapus", Toast.LENGTH_SHORT).show()
                            finish()
                        },
                        onFailure = { e ->
                            Toast.makeText(this, "Gagal menghapus dokumen trash: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onFailure = { e ->
                    Toast.makeText(this, "Gagal memasukkan ke koleksi angkut: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
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
