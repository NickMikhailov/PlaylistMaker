package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter (private val trackList: ArrayList<Track>) : RecyclerView.Adapter<TrackCardViewHolder> () {
        private lateinit var itemView: View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackCardViewHolder {
        itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackCardViewHolder, position: Int) {
        holder.bind(trackList[position], itemView)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

}