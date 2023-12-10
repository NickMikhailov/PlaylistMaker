package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink: Int)
    fun openLink(termsLink: Int)
    fun openEmail(supportEmailData: EmailData)
}