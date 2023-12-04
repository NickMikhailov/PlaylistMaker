package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.SharingInteractor
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

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
    private fun getShareAppLink(): String {
        return SHARE_LINK
        }

    private fun getSupportEmailData(): EmailData {
        return EmailData(EMAIL, SUBJECT, MESSAGE)
    }

    private fun getTermsLink(): String {
        return TERMS_LINK
    }
    companion object {
        private const val SHARE_LINK = "https://practicum.yandex.ru/profile/android-developer/?from=new_landing_android-developer"
        private const val EMAIL = "mikhailov.nick@ya.ru"
        private const val SUBJECT = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        private const val MESSAGE = "Спасибо разработчикам и разработчицам за крутое приложение!"
        private const val TERMS_LINK = "https://yandex.ru/legal/practicum_offer/"
    }

}