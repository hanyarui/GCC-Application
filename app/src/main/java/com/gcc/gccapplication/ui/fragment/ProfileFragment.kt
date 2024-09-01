package com.gcc.gccapplication.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.ui.activity.ChangePasswordActivity
import com.gcc.gccapplication.ui.activity.CreateTrashActivity
import com.gcc.gccapplication.ui.activity.TrashbagActivity
import com.gcc.gccapplication.ui.activity.ValidationActivity
import com.gcc.gccapplication.viewModel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import androidx.core.content.FileProvider
import com.google.android.datatransport.BuildConfig

class ProfileFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnKeranjang: ConstraintLayout
    private lateinit var btnUbahPass: ConstraintLayout
    private lateinit var btnLogout: ConstraintLayout
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var btnDataSampah: ConstraintLayout
    private lateinit var btnRekapSampah: ConstraintLayout
    private lateinit var userPreferences: UserPreferences

    companion object {
        private const val ARG_FULL_NAME = "full_name"
        private const val ARG_EMAIL = "email"
        private const val REQUEST_CODE = 1001

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize UserPreferences
        userPreferences = UserPreferences(requireContext())

        // Bind views
        tvNama = view.findViewById(R.id.tvNama)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnKeranjang = view.findViewById(R.id.btnKeranjang)
        btnUbahPass = view.findViewById(R.id.btnUbahPass)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnDataSampah = view.findViewById(R.id.btnDataSampah)
        btnRekapSampah = view.findViewById(R.id.btnRekapSampah)

        val userRole = userPreferences.getRole() ?: "user"
        if (userRole == "user") {
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
            startActivity(Intent(activity, TrashbagActivity::class.java))
        }

        btnUbahPass.setOnClickListener {
            startActivity(Intent(activity, ChangePasswordActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val uid = userPreferences.getUid()
            userPreferences.clearFCMToken(uid, onSuccess = {
                userPreferences.clear()
                userPreferences.firebaseSignOut()
                val intent = Intent(activity, ValidationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }, onFailure = {
                Log.d("FCM", "Gagal menghapus token FCM dari Firestore ${uid.toString()}", it)
            })
        }

        btnDataSampah.setOnClickListener {
            startActivity(Intent(activity, CreateTrashActivity::class.java))
        }

        btnRekapSampah.setOnClickListener {
            // Check if the WRITE_EXTERNAL_STORAGE permission is granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            } else {
                // Permission is already granted, proceed with exporting to PDF
                viewModel.fetchAndExportDataToPdf(requireContext())
                viewModel.fileSavedLocation.observe(viewLifecycleOwner) { filePath ->
                    filePath?.let {
                        val file = File(filePath)
                        val fileUri =  FileProvider.getUriForFile(requireContext(), "com.gcc.gccapplication.fileprovider", file)

                        Snackbar.make(
                            view,
                            "PDF berhasil disimpan di $filePath",
                            Snackbar.LENGTH_LONG
                        ).setAction("Buka") {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(fileUri, "application/pdf")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                            }
                            startActivity(intent)
                        }.show()
                    } ?: run {
                        Snackbar.make(
                            view,
                            "Gagal menyimpan PDF.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        /* Observe fileSavedLocation LiveData */





        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.fetchAndExportDataToPdf(requireContext())
            } else {
                Log.d("ProfileFragment", "Permission denied")
            }
        }
    }
}
