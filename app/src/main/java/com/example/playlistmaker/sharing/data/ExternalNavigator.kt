package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.data.impl.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink: Int)
    fun openLink(termsLink: Int)
    fun openEmail(supportEmailData: EmailData)
    fun sharePlaylist(message: String)
}