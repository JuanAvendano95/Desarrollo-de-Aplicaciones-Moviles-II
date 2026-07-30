package com.example.ringtones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RingtoneAdapter(
    private val ringtones: List<RingtoneItem>,
    private val onPlayClick: (RingtoneItem) -> Unit,
    private val onDownloadClick: (RingtoneItem) -> Unit,
    private val onShareClick: (RingtoneItem) -> Unit
) : RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>() {

    class RingtoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvRingtoneName)
        val btnPlay: ImageButton = view.findViewById(R.id.btnPlay)
        val btnDownload: ImageButton = view.findViewById(R.id.btnDownload)
        val btnShare: ImageButton = view.findViewById(R.id.btnShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ringtone, parent, false)
        return RingtoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        val ringtone = ringtones[position]
        holder.tvName.text = ringtone.name

        holder.btnPlay.setOnClickListener { onPlayClick(ringtone) }
        holder.btnDownload.setOnClickListener { onDownloadClick(ringtone) }
        holder.btnShare.setOnClickListener { onShareClick(ringtone) }
    }

    override fun getItemCount(): Int = ringtones.size
}
