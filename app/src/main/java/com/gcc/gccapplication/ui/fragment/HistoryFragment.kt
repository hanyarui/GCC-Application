package com.gcc.gccapplication.ui.fragment

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.HistoryAdapter
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.ActivityHistoryBinding
import com.gcc.gccapplication.databinding.FragmentHistoryBinding
import com.gcc.gccapplication.viewModel.HistoryViewModel

class HistoryFragment : Fragment() {


    private lateinit var binding: FragmentHistoryBinding
    private lateinit var rvKeranjangSampah: RecyclerView
    private lateinit var userPreferences: UserPreferences
    private lateinit var historyAdapter: HistoryAdapter
    private val historyViewModel: HistoryViewModel by viewModels()    // TODO: Rename and change types of parameters
    private var email: String? = null

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String) : HistoryFragment{
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(ARG_EMAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        rvKeranjangSampah = view.findViewById(R.id.rvKeranjangSampah)

        userPreferences = UserPreferences(requireContext())
        email = arguments?.getString(ARG_EMAIL) ?: userPreferences.getEmail()
        if(email != null){
            setupRecyclerView()
            observeViewModel()
        }

        return view
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(ArrayList())
        rvKeranjangSampah.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = historyAdapter
        }

    }

    private var isDataEmpty = true
    private fun observeViewModel() {
        email?.let { email ->
            historyViewModel.fetchTrashData(email)
        }

        historyViewModel.angkutData.observe(viewLifecycleOwner) { trashList ->
            try {
                isDataEmpty = trashList.isEmpty()
                if (isDataEmpty) {
                    Toast.makeText(activity, "Belum ada data sampah", Toast.LENGTH_SHORT).show()
                } else {
                    historyAdapter.listAngkut.apply {
                        clear()
                        addAll(trashList)
                    }
                    historyAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("HistoryFragment", "Error updating UI", e)
            }
        }
    }
}