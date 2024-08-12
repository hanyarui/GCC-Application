package com.gcc.gccapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gcc.gccapplication.R
import com.gcc.gccapplication.data.model.TrashbagModel

class TrashbagAdapter(val listTrashbag: ArrayList<TrashbagModel>) : RecyclerView.Adapter<TrashbagAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_trashbag, parent, false)
        return ListViewHolder(view)
    }

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val (id,name, trashId, amount, photoUrl) = listTrashbag[position]
    //        val trashItem = listTrashbag[position]
//            holder.imgPhoto.setImageResource(photoUrl)
            Glide.with(holder.itemView.context)
                .load(photoUrl)
                .placeholder(R.drawable.img_dummy_image).into(holder.imgPhoto)
            holder.tvName.text = name
            holder.tvKG.text = amount.toString()
//            holder.tvJumlahSampah.text = amount.toString()
//            holder.tvAmount.text = amount.toString()
            println(amount)
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(listTrashbag[holder.adapterPosition])
            }
        }


    override fun getItemCount(): Int = listTrashbag.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.ivTrashPhoto)
        val tvKG: TextView = itemView.findViewById(R.id.tvKG)
        val tvName: TextView = itemView.findViewById(R.id.tvTrashName)
//        val tvJumlahSampah : TextView = itemView.findViewById(R.id.tvJumlahSampah)
//        val tvAmount: TextView = itemView.findViewById(R.id.tvTrashAmount) // Corrected ID
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: TrashbagModel)
    }
}
