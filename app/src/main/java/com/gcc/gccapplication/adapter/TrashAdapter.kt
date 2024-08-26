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

class TrashAdapter(val listTrash: ArrayList<TrashModel>) : RecyclerView.Adapter<TrashAdapter.ListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_trash, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val trashItem = listTrash[position]
        holder.tvName.text = trashItem.type
        holder.tvDescription.text = trashItem.description
        holder.tvAddress.text = trashItem.address
        Glide.with(holder.itemView.context)
            .load(trashItem.photoUrl)
            .placeholder(R.drawable.img_dummy_image)
            .into(holder.ivPhoto)

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(trashItem.id)
        }
    }

    override fun getItemCount(): Int = listTrash.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivTrashPhoto)
        val tvName: TextView = itemView.findViewById(R.id.tvTrashName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvTrashDescription)
        val tvAddress: TextView = itemView.findViewById(R.id.tvTrashAddress)
    }

    interface OnItemClickCallback {
        fun onItemClicked(id: String?)
    }
}
