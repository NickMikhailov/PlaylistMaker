package com.example.playlistmaker.sharing.data.impl

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val application: Application): ExternalNavigator {
    private val context = application as Context
    override fun shareLink(shareAppLink: Int) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_TEXT, context.getString(shareAppLink))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openLink(termsLink: Int) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(termsLink)))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(supportEmailData.email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(supportEmailData.subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(supportEmailData.message))
        }
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}