package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.player.domain.models.Track


class TrackListAdapter(var trackList: MutableList<Track>) :
    RecyclerView.Adapter<TrackCardViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackCardViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackCardViewHolder(TrackViewBinding.inflate(layoutInspector, parent, false))
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
    fun update(newTrackList: List<Track>){
        trackList.clear()
        trackList.addAll(newTrackList)
    }
}