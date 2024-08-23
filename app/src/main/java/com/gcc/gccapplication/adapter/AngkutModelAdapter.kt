package com.gcc.gccapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.model.AngkutModel


class AngkutModelAdapter(val listAngkut: List<AngkutModel>) : RecyclerView.Adapter<AngkutModelAdapter.ListViewHolder>(){

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.ivTrashPhoto)
        val tvKG: TextView = itemView.findViewById(R.id.tvKG)
        val tvName: TextView = itemView.findViewById(R.id.tvTrashName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
//        val tvJumlahSampah : TextView = itemView.findViewById(R.id.tvJumlahSampah)
//        val tvAmount: TextView = itemView.findViewById(R.id.tvTrashAmount) // Corrected ID
    }

    private var onItemClickCallback: OnItemClickCallback ?= null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_history_trash, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, waktu, amount,photoUrl) = listAngkut[position]
        val angkutItem = listAngkut[position]
//            holder.imgPhoto.setImageResource(photoUrl)
        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .placeholder(R.drawable.img_dummy_image).into(holder.imgPhoto)
        holder.tvName.text = name
        holder.tvTime.text = waktu
        holder.tvKG.text = amount.toString()
//            holder.tvJumlahSampah.text = amount.toString()
//            holder.tvAmount.text = amount.toString()
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(angkutItem)
        }
    }

    override fun getItemCount(): Int = listAngkut.size

    interface OnItemClickCallback {
        fun onItemClicked(data: AngkutModel)
    }

}