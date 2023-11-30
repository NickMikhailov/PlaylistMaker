package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.SharingInteractor
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator,
                            private val context: Context
) : SharingInteractor {
    override fun shareApp() {
       externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }
    private fun getShareAppLink(): String {
        return context.getString(R.string.link)
        }

    private fun getSupportEmailData(): EmailData {
        return EmailData(context.getString(R.string.email),context.getString(R.string.msg_to_dev_subject),context.getString(R.string.msg_to_dev_text))
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.uri_practicum_offer)
    }
}