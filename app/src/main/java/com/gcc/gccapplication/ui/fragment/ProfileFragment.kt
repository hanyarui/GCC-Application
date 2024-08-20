package com.gcc.gccapplication.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.ui.activity.ChangePasswordActivity
import com.gcc.gccapplication.ui.activity.CreateTrashActivity
import com.gcc.gccapplication.ui.activity.LoginActivity
import com.gcc.gccapplication.ui.activity.MainActivity
import com.gcc.gccapplication.ui.activity.TrashbagActivity
import com.gcc.gccapplication.ui.activity.ValidationActivity

class ProfileFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnKeranjang: ConstraintLayout
    private lateinit var btnUbahPass: ConstraintLayout
//    private lateinit var btnHistori: ConstraintLayout
    private lateinit var btnLogout: ConstraintLayout
    private lateinit var btnDataSampah: ConstraintLayout
    private lateinit var btnRekapSampah: ConstraintLayout
    private lateinit var userPreferences: UserPreferences

    companion object {
        private const val ARG_FULL_NAME = "full_name"
        private const val ARG_EMAIL = "email"

        fun newInstance(fullName: String, email: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_FULL_NAME, fullName)
            args.putString(ARG_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val fullName = it.getString(ARG_FULL_NAME)
            val email = it.getString(ARG_EMAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize UserPreferences
        userPreferences = UserPreferences(requireContext())

        // Bind views
        tvNama = view.findViewById(R.id.tvNama)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnKeranjang = view.findViewById(R.id.btnKeranjang)
        btnUbahPass = view.findViewById(R.id.btnUbahPass)
//        btnHistori = view.findViewById(R.id.btnHistori)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnDataSampah = view.findViewById(R.id.btnDataSampah)
        btnRekapSampah = view.findViewById(R.id.btnRekapSampah)
        userPreferences = UserPreferences(requireContext())
        val userRole = userPreferences.getRole() ?: "user"

        if (userRole == "user"){
            btnDataSampah.visibility = View.GONE
            btnRekapSampah.visibility = View.GONE
        }

        // Set user data
        val fullName = arguments?.getString(ARG_FULL_NAME) ?: "Nama Tidak Ada"
        val email = arguments?.getString(ARG_EMAIL) ?: "Email Tidak Ada"
        tvNama.text = fullName
        tvEmail.text = email

        // Set click listeners
        btnKeranjang.setOnClickListener {
            // Handle Keranjang Sampah button click
            startActivity(Intent(activity, TrashbagActivity::class.java))
        }

        btnUbahPass.setOnClickListener {
            // Handle Ubah Password button click
            startActivity(Intent(activity, ChangePasswordActivity::class.java))
        }

//        btnHistori.setOnClickListener {
//            // Handle Histori Sampah button click
//            // Navigate to a new activity or fragment for trash history
//        }

        btnLogout.setOnClickListener {
            // Handle Logout button click
            userPreferences.firebaseSignOut()
            userPreferences.clear()
            val intent = Intent(activity, ValidationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        btnDataSampah.setOnClickListener {
            // Handle Data Sampah button click
            startActivity(Intent(activity, CreateTrashActivity::class.java))
        }

        btnRekapSampah.setOnClickListener {
            // Handle Rekap Sampah button click
            // Navigate to a new activity or fragment for trash recap
        }

        return view
    }
}
