package com.gcc.gccapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gcc.gccapplication.R
import com.gcc.gccapplication.adapter.TrashAdapter.ListViewHolder
import com.gcc.gccapplication.data.model.NotifikasiModel
import com.gcc.gccapplication.viewModel.NotifikasiViewModel

class ItemNotifAdapter(
    val notifList: ArrayList<NotifikasiModel>,
    private val viewModel: NotifikasiViewModel
) : RecyclerView.Adapter<ItemNotifAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_notif, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = notifList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val notifikasiItem = notifList[position]
        holder.tvNamaLengkap.text = notifikasiItem.namaLengkap ?: ""
        holder.tvAmount.text = notifikasiItem.totalAmount ?: ""
        holder.tvDusun.text = notifikasiItem.dusun ?: ""
        holder.tvAlamatLengkap.text = notifikasiItem.alamatLengkap ?: ""
        holder.tvTrashDescription.text = "Jumlah Sampah:"
        holder.tvKg.text = "KG"
        holder.tvTime.text = notifikasiItem.time ?: ""

        holder.imageView.setImageResource(
            if (notifikasiItem.isPicked) R.drawable.ready_pickup
            else R.drawable.not_ready_pickup
        )
        holder.cardView.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context,
                if (notifikasiItem.isPicked) R.color.card_selected
                else R.color.card_unselected
            )
        )

        holder.imageView.setOnClickListener {
            val newStatus = !notifikasiItem.isPicked
            notifikasiItem.isPicked = newStatus
            notifyItemChanged(position)

            viewModel.updatePickedStatus(notifikasiItem.buktiUploadId ?: "", newStatus) { success ->
                if (!success) {
                    Toast.makeText(holder.itemView.context, "Update failed", Toast.LENGTH_SHORT).show()
                }
                Log.d("ItemNotifAdapter", "Update status for ${notifikasiItem.buktiUploadId} to $newStatus")
            }
        }
    }



    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaLengkap: TextView = itemView.findViewById(R.id.tvNamaLengkap)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDusun: TextView = itemView.findViewById(R.id.tvDusun)
        val tvAlamatLengkap: TextView = itemView.findViewById(R.id.tvAlamatLengkap)
        val tvTrashDescription: TextView = itemView.findViewById(R.id.tvTrashDescription)
        val tvKg: TextView = itemView.findViewById(R.id.tvKg)
        val tvTime: TextView = itemView.findViewById(R.id.tvTanggalWaktu)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val cardView: CardView = itemView.findViewById(R.id.card_view_notif)
    }
}

