package com.example.playlistmaker.presentation.view_model

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class PlayerViewModel {


    companion object{
        fun fuctory(productId: Int) = viewModelFactory {
            initializer { TODO() }
        }
    }
}