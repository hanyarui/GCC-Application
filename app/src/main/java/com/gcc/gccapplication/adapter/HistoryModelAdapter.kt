package com.gcc.gccapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.model.HistoryModel

class HistoryModelAdapter(val historyList: List<HistoryModel>) : RecyclerView.Adapter<HistoryModelAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerViewTrash: RecyclerView = itemView.findViewById(R.id.RvChild)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvTelp: TextView = itemView.findViewById(R.id.tvTelp)
        val ivTrashPhoto: ImageView = itemView.findViewById(R.id.ivTrashPhoto)
//        val tvTelp: TextView = itemView.findViewById(R.id.tvTelp)
        // Inisialisasi elemen lainnya jika ada
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.tvName.text = currentItem.name
        holder.tvAlamat.text = currentItem.alamat
        holder.tvTelp.text = currentItem.telp

        holder.recyclerViewTrash.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerViewTrash.adapter = currentItem.trashItems?.let { AngkutModelAdapter(it) }
//        holder.tvKG.text = currentItem.trashKg.toString()
//        holder.tvKGDesc.text = "kg"
        Glide.with(holder.itemView.context)
            .load(currentItem.photoUrl)
            .placeholder(R.drawable.img_dummy_image).into(holder.ivTrashPhoto)
    }

    override fun getItemCount(): Int = historyList.size
}
