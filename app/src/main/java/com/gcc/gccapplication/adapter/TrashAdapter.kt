package com.gcc.gccapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.model.TrashModel

class TrashAdapter(var listTrash: ArrayList<TrashModel>) : RecyclerView.Adapter<TrashAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_trash, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photoUrl) = listTrash[position]
        holder.tvName.text = name
        holder.tvDescription.text = description
        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .placeholder(R.drawable.img_dummy_image)
            .into(holder.imgPhoto)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listTrash[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listTrash.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.ivTrashPhoto)
        val tvName: TextView = itemView.findViewById(R.id.tvTrashName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvTrashDescription)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: TrashModel)
    }
}
