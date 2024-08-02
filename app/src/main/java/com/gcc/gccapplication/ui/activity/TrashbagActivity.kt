package com.gcc.gccapplication.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gcc.gccapplication.R
import com.gcc.gccapplication.databinding.ActivityTrashbagBinding

class TrashbagActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrashbagBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashbagBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}