package com.example.c196gt

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class NotificationHelper(private val context: Context) {

    companion object {
        const val COURSE_CHANNEL_ID = "Course_Notif"
        const val ASSIGNMENT_CHANNEL_ID = "Assessment_Notif"
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the course notification channel
            val courseChannel = NotificationChannel(
                COURSE_CHANNEL_ID,
                "Course Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for course notifications"
            }

            // Define the assignment notification channel
            val assignmentChannel = NotificationChannel(
                ASSIGNMENT_CHANNEL_ID,
                "Assignment Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for assignment notifications"
            }

            // Register the channels with the system
            notificationManager.createNotificationChannel(courseChannel)
            notificationManager.createNotificationChannel(assignmentChannel)
        }
    }

    fun sendNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, COURSE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
    }
}

