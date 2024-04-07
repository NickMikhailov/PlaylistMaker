package com.example.playlistmaker.sharing

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(message: String)
}