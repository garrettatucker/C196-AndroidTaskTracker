package com.example.c196gt

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class AlertReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val message = intent.getStringExtra("message")
            val channel = intent.getStringExtra("channel")
            Log.d("AlertReceiver", "Receiver activated with message: $message")
            Log.d("AlertReceiver", "Channel received: $channel")
            if (message != null) {
                // Log the current time to check if the alert is being received when the date arrives
                val currentTime = Calendar.getInstance().time
                Log.d("AlertReceiver", "Current time: $currentTime")

                val requestCode = intent.getIntExtra("requestCode", -1) // Getting the request code
                 // Getting the channel ID
                Log.d("AlertReceiver", "Grabbing Alert Channel: $channel")

                // Pass the channel value, handling null case with the null-safe operator
                if (channel != null) {
                    Log.d(
                        "AlertReceiver", "showNotification() function accessed with: " +
                                "context=$context, message=$message, requestCode=$requestCode, channel=$channel"
                    )
                    showNotification(context, message, requestCode, channel)
                } else {Log.e("AlertRecever","channel is null")}
            }
        }
    }


    private fun showNotification(context: Context, message: String, notificationId: Int, channelId: String) {
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
