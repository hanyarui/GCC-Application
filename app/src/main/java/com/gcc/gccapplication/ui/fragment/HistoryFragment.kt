package com.gcc.gccapplication.ui.fragment

import android.os.Bundle
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
import com.gcc.gccapplication.adapter.AngkutModelAdapter
import com.gcc.gccapplication.adapter.HistoryModelAdapter
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.databinding.FragmentHistoryBinding
import com.gcc.gccapplication.viewModel.HistoryViewModel

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var rvKeranjangSampah: RecyclerView
    private lateinit var userPreferences: UserPreferences
    private lateinit var historyAdapter: HistoryModelAdapter
    private val historyViewModel: HistoryViewModel by viewModels()
    private var email: String? = null

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String): HistoryFragment {
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
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        rvKeranjangSampah = binding.rvKeranjangSampah

        userPreferences = UserPreferences(requireContext())
        email = arguments?.getString(ARG_EMAIL) ?: userPreferences.getEmail()
        if (email != null) {
            setupRecyclerView()
            observeViewModel()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryModelAdapter(ArrayList())
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

        historyViewModel.historyData.observe(viewLifecycleOwner) { historyList ->
            try {
                isDataEmpty = historyList.isEmpty()
                if (isDataEmpty) {
                    Toast.makeText(activity, "Belum ada data sampah", Toast.LENGTH_SHORT).show()
                } else {
                    // Update adapter with HistoryModel data
                    historyAdapter = HistoryModelAdapter(historyList)
                    rvKeranjangSampah.adapter = historyAdapter
                }
            } catch (e: Exception) {
                Log.e("HistoryFragment", "Error updating UI", e)
            }
        }
    }
}
