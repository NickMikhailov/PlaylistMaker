package com.example.playlistmaker.presentation.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.presentation.view_model.TrackViewModel

class TrackActivity: AppCompatActivity() {
//    private val viewModel by lazy {
//        TrackViewModel.getViewModelFactory("123")
//    }
//    private lateinit var binding: ActivityTrackBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityTrackBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        viewModel.getScreenStateLiveData().observe(this) { screenState ->
//            // 1
//            when (screenState) {
//                is TrackScreenState.Content -> {
//                    changeContentVisibility(loading = false)
//                    binding.picture.setImage(screenState.trackModel.pictureUrl)
//                    binding.author.text = screenState.trackModel.author
//                    binding.trackName.text = screenState.trackModel.name
//                }
//                is TrackScreenState.Loading -> {
//                    changeContentVisibility(loading = true)
//                }
//            }
//        }
//        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
//            changeButtonStyle(playStatus)
//            // 2
//            binding.seekBar.value = playStatus.progress
//        }
//    }
//
//    private fun changeContentVisibility(loading: Boolean) {
//        binding.progressBar.isVisible = loading
//
//        binding.picture.isVisible = !loading
//        binding.author.isVisible = !loading
//        binding.trackName.isVisible = !loading
//    }
//    private fun changeButtonStyle(playStatus: PlayStatus) {
//        // Меняем вид кнопки проигрывания в зависимости от того, играет сейчас трек или нет
//    }
}
