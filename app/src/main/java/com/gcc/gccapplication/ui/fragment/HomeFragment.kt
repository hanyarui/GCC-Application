package com.gcc.gccapplication.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.TrashAdapter
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.data.model.NotifikasiModel
import com.gcc.gccapplication.ui.activity.DetailActivity
import com.gcc.gccapplication.ui.activity.NotificationActivity
import com.gcc.gccapplication.ui.activity.TrashbagActivity
import com.gcc.gccapplication.viewModel.HomeViewModel
import com.gcc.gccapplication.viewModel.NotifikasiViewModel

class HomeFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var rvSampah: RecyclerView
    private lateinit var ivNotifDot: ImageView
    private lateinit var clNotification: ConstraintLayout
    private lateinit var trashAdapter: TrashAdapter
    private val trashViewModel: HomeViewModel by viewModels()
    private val notifikasiViewModel: NotifikasiViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences
    private var fullName: String? = null

    companion object {
        private const val ARG_FULL_NAME = "full_name"
        private const val EXTRA_TRASH_ID = "extra_trash_id"

        fun newInstance(fullName: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_FULL_NAME, fullName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        checkForNewData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fullName = it.getString(ARG_FULL_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi view
        clNotification = view.findViewById(R.id.clNotification)
        tvNama = view.findViewById(R.id.tvNama)
        rvSampah = view.findViewById(R.id.rvSampah)
        ivNotifDot = view.findViewById(R.id.ivNotifDot)



        // Set nama user


        // Initialize UserPreferences
        userPreferences = UserPreferences(requireContext())
        val userAddress = userPreferences.getAddress()
        val userRole = userPreferences.getRole() ?: "user"
        val userFullName = userPreferences.getFullName() ?: ""
        tvNama.text = userFullName


        // Atur visibilitas clNotification berdasarkan peran pengguna
        if (userRole == "user") {
            clNotification.visibility = View.GONE
        }
//        clNotification.visibility = View.VISIBLE


        clNotification.setOnClickListener {
            ivNotifDot.visibility = View.GONE
//            userPreferences.setHasNewNotif(false)
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
        // Cek apakah alamat sudah diisi
        if (userAddress.isNullOrEmpty()) {
            Toast.makeText(context, "Silahkan masukkan alamat anda terlebih dahulu", Toast.LENGTH_SHORT).show()
        } else {
            setupRecyclerView()
            observeViewModel(userAddress, userRole)
        }

        return view
    }


    //    kode buat negload adapter
    private fun setupRecyclerView() {
        trashAdapter = TrashAdapter(ArrayList())
        rvSampah.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = trashAdapter
        }

        trashAdapter.setOnItemClickCallback(object : TrashAdapter.OnItemClickCallback {
            override fun onItemClicked(id: String?) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra(EXTRA_TRASH_ID, id)
                startActivity(intent)

            }
        })
        checkForNewData()
    }

    private fun observeViewModel(userAddress: String, userRole: String) {
        trashViewModel.trashData.observe(viewLifecycleOwner) { trashList ->
            if (trashList.isEmpty()) {
                Toast.makeText(context, "Belum ada data sampah", Toast.LENGTH_SHORT).show()
            }
            trashAdapter.listTrash.apply {
                clear()
                addAll(trashList)
            }
            trashAdapter.notifyDataSetChanged()
        }

        trashViewModel.fetchTrashData(userAddress, userRole)
    }

    private fun checkForNewData() {
        val hasNewNotification = userPreferences.getHasNewNotif()
        if (hasNewNotification) {
            ivNotifDot.visibility = View.VISIBLE
        } else {
            ivNotifDot.visibility = View.GONE
        }
    }

}
