package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_arrow)
        backButton.setOnClickListener {
            finish()
        }

        val shareButton: ImageView = findViewById<ImageView>(R.id.share_icon)
        shareButton.setOnClickListener {
            val link = getString(R.string.link)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(shareIntent)
        }
        val supportButton: ImageView = findViewById<ImageView>(R.id.support_icon)
        supportButton.setOnClickListener{
            val subject = getString(R.string.msg_to_dev_subject)
            val message = getString(R.string.msg_to_dev_text)
            val email = getString(R.string.email)

            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            }
            if (supportIntent.resolveActivity(packageManager) != null) {
                startActivity(supportIntent)
            }
        }
        val agreementButton: ImageView = findViewById<ImageView>(R.id.agreement_icon)
        agreementButton.setOnClickListener{
            val url = Uri.parse(getString(R.string.uri_practicum_offer))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(agreementIntent)
        }
    }
}