package com.example.playlistmaker.presentation.ui.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackListAdapter(private val trackList: ArrayList<Track>) :
    RecyclerView.Adapter<TrackCardViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackCardViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount() = trackList.size
    fun getTrack(position: Int): Track {
        return trackList[position]
    }
}