package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink(shareAppLink: String) {
        context.startActivity(
            Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
        }
        )
    }

    override fun openLink(termsLink: String) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
        )
    }

    override fun openEmail(supportEmailData: EmailData) {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
        })
    }
}