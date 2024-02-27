package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.domain.model.EmailData

class ExternalNavigatorImpl(private val applicationContext: Context): ExternalNavigator {
    override fun shareLink(shareAppLink: Int) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_TEXT, applicationContext.getString(shareAppLink))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    override fun openLink(termsLink: Int) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(applicationContext.getString(termsLink)))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(applicationContext.getString(supportEmailData.email)))
            putExtra(Intent.EXTRA_SUBJECT, applicationContext.getString(supportEmailData.subject))
            putExtra(Intent.EXTRA_TEXT, applicationContext.getString(supportEmailData.message))
        }
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }
}