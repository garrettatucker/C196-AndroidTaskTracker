package com.example.c196gt

import Assignment
import Course
import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner

class C196GT : Application() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()

        // Initialize the NotificationHelper
        notificationHelper = NotificationHelper(this)

        // Register app lifecycle observer
        registerAppLifecycleObserver()

        // Request notification permissions if needed
        requestNotificationPermissionsIfNeeded()
    }

    private fun registerAppLifecycleObserver() {
        val appLifecycleObserver = AppLifecycleObserver {
            // This block will be executed when the app goes to the background
            captureAndScheduleNotification()
            captureAndScheduleAssignmentNotification()
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }

    private fun captureAndScheduleNotification() {
        val dbHelper = DatabaseHelper(this)
        val allCourses = dbHelper.getAllCourses() // Retrieve all courses from the database

        if (allCourses.isNotEmpty()) {
            // Construct notification content
            val notificationContent = buildCourseNotificationContent(allCourses)

            // Schedule notification with the constructed content
            scheduleNotification("Upcoming Courses Summary", notificationContent)
        }
    }

    private fun buildCourseNotificationContent(courses: List<Course>): String {
        val summaryBuilder = StringBuilder()
        summaryBuilder.append("Upcoming Courses Summary:\n\n")
        for (course in courses) {
            summaryBuilder.append("Course: ${course.courseName}\n")
            summaryBuilder.append("Start Date: ${course.startDate}\n")
            summaryBuilder.append("End Date: ${course.endDate}\n\n")
        }
        return summaryBuilder.toString()
    }

    private fun captureAndScheduleAssignmentNotification() {
        val dbHelper = DatabaseHelper(this)
        val allAssignments = dbHelper.getAllAssignments()

        if (allAssignments.isNotEmpty()) {
            val notificationContent = buildAssignmentNotificationContent(allAssignments)
            scheduleNotification("Upcoming Assignments Summary", notificationContent)
        }
    }

    private fun buildAssignmentNotificationContent(assignments: List<Assignment>): String {
        val summaryBuilder = StringBuilder()
        summaryBuilder.append("Upcoming Assignments Summary:\n\n")
        for (assignment in assignments) {
            summaryBuilder.append("Assignment: ${assignment.assignmentName}\n")
            summaryBuilder.append("Due Date: ${assignment.dueDate}\n\n")
        }
        return summaryBuilder.toString()
    }

    private fun scheduleNotification(title: String, content: String) {
        val notificationBuilder = NotificationCompat.Builder(this, NotificationHelper.COURSE_CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content)) // Set BigTextStyle with the content

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notificationBuilder.build())
    }


    private fun requestNotificationPermissionsIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_FULL_SCREEN_INTENT
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, launch PermissionRequestActivity
            launchPermissionRequestActivity()
        }
    }

    private fun launchPermissionRequestActivity() {
        Log.d("C196GT", "Launching PermissionRequestActivity")
        val intent = Intent(this, PermissionRequestActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Log.d("C196GT", "PermissionRequestActivity launched")
    }

    fun showNotification(title: String, message: String) {
        notificationHelper.sendNotification(title, message)
    }
}
