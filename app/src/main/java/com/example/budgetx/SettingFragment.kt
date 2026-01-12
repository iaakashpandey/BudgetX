package com.example.budgetx

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.budgetx.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

         val userId = arguments?.getString("userId")
        Log.d("SettingFragment", "User ID from arguments: $userId")
        binding.textUserIdNumber.text = userId ?: "No ID Available"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutName.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left_partially,
                    R.anim.slide_in_right_partially,
                    R.anim.slide_out_right
                )
            fragmentTransaction.replace(R.id.fragmentContainer, NameFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.layoutNotification.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left_partially,
                    R.anim.slide_in_right_partially,
                    R.anim.slide_out_right
                )
            fragmentTransaction.replace(R.id.fragmentContainer, NotificationsFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        binding.layoutGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/IAakashPandey"))
            startActivity(intent)
        }

        binding.layoutLinkedIn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/iaakashpandey"))
            startActivity(intent)
        }

        binding.layoutPlayStore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/iaakashpandey")) //change it with playStore link
            startActivity(intent)
        }


        binding.layoutPrivacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse
                ("https://techiestudioweb.blogspot.com/2021/09/privacy-policy-status-server.html"))
            startActivity(intent)
        }

        binding.layoutShare.setOnClickListener {
            val ShareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://github.com/IAakashPandey") //change it with playStore link
                type = "text/plain"
            }

        }

        binding.layoutReportBug.setOnClickListener {
            val email = "mailshinobicode@gmail.com"
            val subject = "Bug Report - BudgetX"
            val body = getDevicesInfo()

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                `package` = "com.google.android.gm" // Set Gmail as the target app
            }

            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Gmail app is not installed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.layoutSuggestion.setOnClickListener {
            val email = "mailshinobicode@gmail.com"
            val subject = "Regarding App Suggestion - BudgetX"
            val body = getDevicesInfo()

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                `package` = "com.google.android.gm" // Set Gmail as the target app
            }

            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Gmail app is not installed", Toast.LENGTH_SHORT).show()
            }
        }
        }


    private fun getDevicesInfo(): String {
        val deviceInfo = StringBuilder()
        deviceInfo.append("Device Info:\n")
        deviceInfo.append("Model: ${Build.MODEL}\n")
        deviceInfo.append("Brand: ${Build.BRAND}\n")
        deviceInfo.append("Device Version: ${Build.VERSION.RELEASE}\n")
        deviceInfo.append("SDK: ${Build.VERSION.SDK_INT}\n")
        deviceInfo.append("App Version: 1 \n") // App version change everytime
        deviceInfo.append("Package Name: com.example.budgetx \n") // App package name change every time


        return deviceInfo.toString()


    }
}

