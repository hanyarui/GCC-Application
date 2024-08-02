package com.gcc.gccapplication.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.TrashAdapter
import com.gcc.gccapplication.data.model.TrashModel

class HomeFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var rvSampah: RecyclerView
    private lateinit var trashAdapter: TrashAdapter
    private var fullName: String? = null

    companion object {
        private const val ARG_FULL_NAME = "full_name"

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvNama = view.findViewById(R.id.tvNama)
        rvSampah = view.findViewById(R.id.rvSampah)

        // Set the full name
        tvNama.text = fullName

        // Setup RecyclerView
        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        val listTrash = generateDummyTrashData() // Replace with your actual data source
        trashAdapter = TrashAdapter(listTrash)
        rvSampah.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = trashAdapter
        }

        // Handle item click
        trashAdapter.setOnItemClickCallback(object : TrashAdapter.OnItemClickCallback {
            override fun onItemClicked(data: TrashModel) {
                // Handle item click
            }
        })
    }

    private fun generateDummyTrashData(): ArrayList<TrashModel> {
        // Dummy data, replace with actual data
        val list = ArrayList<TrashModel>()
        list.add(TrashModel("Plastic", "Plastic waste", R.drawable.ic_trash))
        list.add(TrashModel("Glass", "Glass waste", R.drawable.ic_trash))
        return list
    }
}
