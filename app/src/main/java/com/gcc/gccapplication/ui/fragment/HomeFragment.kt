package com.gcc.gccapplication.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.TrashAdapter
import com.gcc.gccapplication.viewModel.HomeViewModel
import com.gcc.gccapplication.ui.activity.DetailActivity

class HomeFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var rvSampah: RecyclerView
    private lateinit var trashAdapter: TrashAdapter
    private val trashViewModel: HomeViewModel by viewModels()
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

        tvNama = view.findViewById(R.id.tvNama)
        rvSampah = view.findViewById(R.id.rvSampah)

        tvNama.text = fullName

        setupRecyclerView()
        observeViewModel()

        return view
    }

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
    }

    private fun observeViewModel() {
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

        trashViewModel.fetchTrashData()
    }
}
