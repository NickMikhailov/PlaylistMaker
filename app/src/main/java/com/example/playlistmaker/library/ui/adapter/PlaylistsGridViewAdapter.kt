package com.example.playlistmaker.library.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistGridViewBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.view_holder.PlaylistGridViewHolder

class PlaylistsGridViewAdapter(var playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistGridViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistGridViewHolder(PlaylistGridViewBinding.inflate(layoutInspector, parent, false))    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }    }
}