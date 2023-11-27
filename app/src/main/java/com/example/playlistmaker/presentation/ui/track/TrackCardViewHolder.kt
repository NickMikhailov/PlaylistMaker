package com.example.playlistmaker.presentation.ui.track


import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.models.Track



class TrackCardViewHolder(private val binding: TrackViewBinding): RecyclerView.ViewHolder(binding.root) {
//    private val trackName: TextView = itemView.findViewById(R.id.trackName)
//    private val artistName: TextView = itemView.findViewById(R.id.artistName)
//    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
//    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)

    fun bind(model: Track) {
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = model.trackTime
        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_small)))
            .into(binding.trackCover)
    }
}