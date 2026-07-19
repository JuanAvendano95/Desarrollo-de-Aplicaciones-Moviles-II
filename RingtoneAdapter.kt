package com.example.ringtones

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RingtoneAdapter(
    private val ringtones: List<RingtoneItem>,
    private val onPlayClick: (RingtoneItem) -> Unit,
    private val onDownloadClick: (RingtoneItem) -> Unit
) : RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>() {

    var selectedPosition = -1 // Controla qué elemento está resaltado

    class RingtoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val btnPlay: ImageButton = view.findViewById(R.id.btnPlay)
        val btnDownload: ImageButton = view.findViewById(R.id.btnDownload)
        val container: View = view.findViewById(R.id.itemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ringtone, parent, false)
        return RingtoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        val ringtone = ringtones[position]
        holder.title.text = ringtone.name

        // Resaltado visual
        holder.container.setBackgroundColor(if (selectedPosition == position) Color.LTGRAY else Color.WHITE)

        holder.btnPlay.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged() // Refresca la lista para que se pinte el color
            onPlayClick(ringtone)
        }

        holder.btnDownload.setOnClickListener { onDownloadClick(ringtone) }
    }

    override fun getItemCount() = ringtones.size
}
