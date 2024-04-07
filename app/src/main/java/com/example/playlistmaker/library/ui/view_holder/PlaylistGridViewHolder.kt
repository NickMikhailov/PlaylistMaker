package com.example.playlistmaker.library.ui.view_holder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistGridViewBinding
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistGridViewHolder(private val binding: PlaylistGridViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        binding.playlistName.text = model.name
        binding.playlistTrackCount.text = model.trackCount.toString() + getTrackEnding(model.trackCount)
        Glide.with(binding.root)
            .load(model.coverName)
            .placeholder(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_medium_8)))
            .into(binding.playlistCover)
        Log.d("filesave",model.coverName)

    }

    private fun getTrackEnding(count: Int): String {
        return when {
            (count % 20) in 10..20 || (count % 10) in 5..9 || (count % 10) == 0 -> " треков"
            (count % 10) == 1 -> " трек"
            else -> " трека"
        }
    }
}
