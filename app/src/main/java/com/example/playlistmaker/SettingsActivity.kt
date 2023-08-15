package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import java.lang.Exception

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBackButton()
        initializeShareButton()
        initializeSupportButton()
        initializeAgreementButton()
        initializeThemeSwitcher()
    }

    private fun initializeBackButton() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
    }

    private fun initializeShareButton() {
        binding.shareIcon.setOnClickListener {
            val link = getString(R.string.link)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, link)
            try {
                startActivity(shareIntent)
            } catch (ex: Exception) {
                Toast.makeText(applicationContext, getString(R.string.err_msg_to_share), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initializeSupportButton() {
        binding.supportIcon.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                val subject = getString(R.string.msg_to_dev_subject)
                val message = getString(R.string.msg_to_dev_text)
                val email = getString(R.string.email)
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, message)
                try {
                    startActivity(this)
                } catch (ex: Exception) {
                    Toast.makeText(applicationContext, getString(R.string.err_msg_to_send_email), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun initializeAgreementButton() {
        binding.agreementIcon.setOnClickListener {
            val url = Uri.parse(getString(R.string.uri_practicum_offer))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)

            try {
                startActivity(agreementIntent)
            } catch (ex: Exception) {
                Toast.makeText(applicationContext, getString(R.string.err_msg_to_open_url), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initializeThemeSwitcher() {
        binding.themeSwitcher.isChecked = (applicationContext as App).getCurrentTheme()
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}