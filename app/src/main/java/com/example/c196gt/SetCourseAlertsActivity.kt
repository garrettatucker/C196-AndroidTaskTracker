package com.example.c196gt

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SetCourseAlertsActivity   : AppCompatActivity() {

    // Define your variables here
    private lateinit var startDateCheck: CheckBox
    private lateinit var endDateCheck: CheckBox
    private lateinit var spinnerCourses: Spinner
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_course_alerts)

        // Initialize your views
        startDateCheck = findViewById(R.id.startDateCheck)
        endDateCheck = findViewById(R.id.endDateCheck)
        spinnerCourses = findViewById(R.id.spinnerCourses)

        databaseHelper = DatabaseHelper(this)

        // Populate spinner with Course names
        val courseNames = databaseHelper.getAllCourseNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapter

        // Create a notification channel for course alerts
        createNotificationChannel("course_alert_channel", "Course Alert Channel", "Channel for course alerts")

    }

    // Function to handle the button click
    fun onAddCourseAlertClick(view: View) {
    // Get the selected course name
    val selectedCourseName = spinnerCourses.selectedItem.toString()

    // Get the start and end dates from the database
    val (startDate, endDate) = databaseHelper.getCourseDatesByName(selectedCourseName)

    Log.d("CourseAlerts", "Start Date from DB: $startDate")
    Log.d("CourseAlerts", "End Date from DB: $endDate")

    if (startDate != null && startDate.isNotBlank()) {
        val parsedStartDate = parseDate(startDate)
        Log.d("CourseAlerts", "Setting alert for start date: $startDate, parsed date: $parsedStartDate")
        setAlert(parsedStartDate, "Course $selectedCourseName is starting today!", "course_alert_channel")
    } else {
        Log.e("CourseAlerts", "Start date is null or blank")
    }

    if (endDate != null && endDate.isNotBlank()) {
        val parsedEndDate = parseDate(endDate)
        Log.d("CourseAlerts", "Setting alert for end date: $endDate, parsed date: $parsedEndDate")
        setAlert(parsedEndDate, "Course $selectedCourseName is ending today!", "course_alert_channel")
    } else {
        Log.e("CourseAlerts", "End date is null or blank")
    }


    // Optionally, you can show a toast to indicate that alerts are set
    Toast.makeText(this, "Alerts set for $selectedCourseName", Toast.LENGTH_SHORT).show()
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

    private var requestCodeCounter = 0

    private fun getNextRequestCode(): Int {
        return requestCodeCounter++
    }

    private fun setAlert(date: Date, message: String, channel: String) {
        Log.d("SetAlert", "Setting alert with channel: $channel")
        Log.d("SetAlert", "Setting alert with message: $message")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent for AlertReceiver
        val alertReceiverIntent = Intent(this, AlertReceiver::class.java).apply {
            action = NOTIFICATION_ACTION
            putExtra("message", message)
            putExtra("channel", channel)
        }
        alertReceiverIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        val alertPendingIntent = PendingIntent.getBroadcast(this, getNextRequestCode(), alertReceiverIntent, PendingIntent.FLAG_MUTABLE)

        // Intent for NotificationService
        val notificationServiceIntent = Intent(this, NotificationService::class.java).apply {
            action = NOTIFICATION_ACTION
            putExtra("message", message)
            putExtra("channel", channel)
        }

        val notificationServicePendingIntent = PendingIntent.getService(
            this,
            getNextRequestCode(),
            notificationServiceIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date.time
            set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alertPendingIntent)

        // Start the NotificationService
        startService(notificationServiceIntent)
    }

    private fun parseDate(dateString: String): Date {
        val formats = arrayOf("MM/dd/yyyy", "MM-dd-yyyy")
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val parsedDate = sdf.parse(dateString)
                if (parsedDate != null) {
                    Log.d("DateParse", "Parsed date using format $format: $parsedDate")
                    return parsedDate
                }
            } catch (e: ParseException) {
                Log.e("DateParse", "Error parsing date with format $format: ${e.message}")
                // Try the next format
            }
        }
        // If none of the formats work, return the current date
        Log.e("DateParse", "None of the formats worked, returning current date")
        return Date()
    }

    companion object {
        // Inside your activity or a companion object
        private const val NOTIFICATION_ACTION = "com.example.c196gt.NOTIFICATION_ACTION"
    }

}