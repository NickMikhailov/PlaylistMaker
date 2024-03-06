package com.example.playlistmaker.sharing.data.impl.domain.impl

import androidx.annotation.StringRes
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.SharingInteractor
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
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
    @StringRes
    private fun getShareAppLink(): Int {
        return R.string.share_app_link
        }

    private fun getSupportEmailData(): EmailData {
        return EmailData(R.string.email, R.string.msg_to_dev_subject, R.string.msg_to_dev_text)
    }
    @StringRes
    private fun getTermsLink(): Int {
        return R.string.terms_link
    }

}