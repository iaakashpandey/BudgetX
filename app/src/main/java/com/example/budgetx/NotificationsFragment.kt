package com.example.budgetx

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetx.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle the switch for enabling/disabling notifications
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndRequestNotificationPermission()
            } else {
                Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }


    }

    // Step 1: Check and request notification permission
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                showCustomNotification()
            }
        } else {
            showCustomNotification()
        }
    }

    // Step 2: Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCustomNotification()
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Step 3: Show custom notification
    @SuppressLint("MissingPermission")
    private fun showCustomNotification() {
        //custom RemoteViews layout for the notification
        val remoteView = android.widget.RemoteViews(requireContext().packageName, R.layout.notification_custom)
        remoteView.setTextViewText(R.id.notification_title, "Expense Tracker")
        remoteView.setTextViewText(R.id.notification_text, "Don't forget to add today's expenses!")
        remoteView.setImageViewResource(R.id.notification_icon, R.drawable.food)

        // Initialize NotificationManager and NotificationCompat Builder
        val notificationManager = NotificationManagerCompat.from(requireContext())

        val notification = NotificationCompat.Builder(requireContext(), "default_notifications_channel")
            .setSmallIcon(R.drawable.food) // Small icon in the status bar
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) // Use custom view
            .setCustomContentView(remoteView) // Set the custom view
            .setCustomBigContentView(remoteView) // Optional for expanded view
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for immediate delivery
            .build()

        // Display the notification
        notificationManager.notify(1, notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

