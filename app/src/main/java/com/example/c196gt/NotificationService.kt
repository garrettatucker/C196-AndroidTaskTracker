package com.example.c196gt

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class NotificationService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID_COURSE = "course_alert_channel"
        const val NOTIFICATION_CHANNEL_ID_ASSIGNMENT = "assignment_alert_channel"
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val FOREGROUND_NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "onStartCommand: Started")


        intent?.let {
            val message = intent.getStringExtra("message")
            val channel = intent.getStringExtra("channel")

            if (!message.isNullOrEmpty() && !channel.isNullOrEmpty()) {
                Log.d("NotificationService", "Received message: $message, channel: $channel")

                val notificationId = Random().nextInt() // Generate a unique notification ID
                if (channel == NOTIFICATION_CHANNEL_ID_ASSIGNMENT) {
                    Log.d("NotificationService", "Starting foreground service with assignment notification")
                    startForeground(notificationId, createNotification(NOTIFICATION_CHANNEL_ID_ASSIGNMENT, message))
                } else {
                    Log.d("NotificationService", "Starting foreground service with course notification")
                    startForeground(notificationId, createNotification(NOTIFICATION_CHANNEL_ID_COURSE, message))
                }
            } else {
                Log.e("NotificationService", "Message or channel is null or empty")
            }
        }

        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val courseChannelName = "Course Alert Channel"
            val courseChannelDescription = "Channel for course alerts"
            createNotificationChannel(NOTIFICATION_CHANNEL_ID_COURSE, courseChannelName, courseChannelDescription)

            val assignmentChannelName = "Assignment Alert Channel"
            val assignmentChannelDescription = "Channel for assignment alerts"
            createNotificationChannel(NOTIFICATION_CHANNEL_ID_ASSIGNMENT, assignmentChannelName, assignmentChannelDescription)
        }
    }

    private fun createNotificationChannel(channelId: String, channelName: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                this.description = description
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotification(channelId: String, message: String): Notification {
        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Course Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss notification when tapped

        // Display the notification
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())

        // Return the built notification
        return notificationBuilder.build()
    }
}
