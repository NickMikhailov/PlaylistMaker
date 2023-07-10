package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter(private val trackList: ArrayList<Track>) :
    RecyclerView.Adapter<TrackCardViewHolder>() {
    private lateinit var itemView: View
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackCardViewHolder {
        itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackCardViewHolder, position: Int) {
        holder.bind(trackList[position], itemView)
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount() = trackList.size
    fun getTrack(position: Int):Track{
        return trackList[position]
    }
}