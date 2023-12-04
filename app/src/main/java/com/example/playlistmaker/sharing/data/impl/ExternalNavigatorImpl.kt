package com.example.playlistmaker.sharing.data.impl

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val application: Application): ExternalNavigator {
    override fun shareLink(shareAppLink: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        (application as Context).startActivity(intent)
    }

    override fun openLink(termsLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        (application as Context).startActivity(intent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.message)
        }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        (application as Context).startActivity(intent)
        application.startActivity(intent)
    }
}