package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.player.domain.models.Track


class TrackListAdapter(var trackList: MutableList<Track>) :
    RecyclerView.Adapter<TrackCardViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null
    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackCardViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackCardViewHolder(TrackViewBinding.inflate(layoutInspector, parent, false))
    }
    override fun onBindViewHolder(holder: TrackCardViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(position)
            true
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