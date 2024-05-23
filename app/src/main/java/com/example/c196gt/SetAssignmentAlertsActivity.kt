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

class SetAssignmentAlertsActivity  : AppCompatActivity() {

    // Define your variables here
    private lateinit var startDateCheck: CheckBox
    private lateinit var endDateCheck: CheckBox
    private lateinit var spinnerAssignments: Spinner
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_assignment_alerts)

        // Initialize your views
        startDateCheck = findViewById(R.id.startDateCheck)
        endDateCheck = findViewById(R.id.endDateCheck)
        spinnerAssignments = findViewById(R.id.spinnerAssignments)

        // Initialize the database helper
        databaseHelper = DatabaseHelper(this)

        // Populate spinner with Assignment names
        val assignmentNames = databaseHelper.getAllAssignmentNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, assignmentNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAssignments.adapter = adapter

        // Create a notification channel for course alerts
        createNotificationChannel("assignment_alert_channel", "Assignment Alert Channel", "Channel for assignment alerts")
    }

    // Function to handle the button click
    fun onAddAssignmentAlertClick(view: View) {
        // Get the selected Assignment name
        val selectedAssignmentName = spinnerAssignments.selectedItem.toString()

        // Get the start and end dates from the database
        val (startDate, endDate) = databaseHelper.getAssignmentDatesByName(selectedAssignmentName)

        Log.d("AssignmentAlerts", "Start Date from DB: $startDate")
        Log.d("AssignmentAlerts", "End Date from DB: $endDate")

        if (startDate != null && startDate.isNotBlank()) {
            val parsedStartDate = parseDate(startDate)
            Log.d("AssignmentAlerts", "Setting alert for start date: $startDate, parsed date: $parsedStartDate")
            setAlert(parsedStartDate, "Assignment $selectedAssignmentName is starting today!", "assignment_alert_channel")
            // Pass the channel ID for assignment alerts
        } else {
            Log.e("AssignmentAlerts", "Start date is null or blank")
        }

        if (endDate != null && endDate.isNotBlank()) {
            val parsedEndDate = parseDate(endDate)
            Log.d("AssignmentAlerts", "Setting alert for end date: $endDate, parsed date: $parsedEndDate")
            setAlert(parsedEndDate, "Assignment $selectedAssignmentName is ending today!", "assignment_alert_channel")
        } else {
            Log.e("AssignmentAlerts", "End date is null or blank")
        }


        // Optionally, you can show a toast to indicate that alerts are set
        Toast.makeText(this, "Alerts set for $selectedAssignmentName", Toast.LENGTH_SHORT).show()
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
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        intent.action = NOTIFICATION_ACTION
        intent.putExtra("message", message)
        intent.putExtra("channel", channel)
        Log.d("SetAlert", "Channel value: $channel")
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        val pendingIntent = PendingIntent.getBroadcast(this, getNextRequestCode(), intent, PendingIntent.FLAG_MUTABLE)


        //sets time to midnight for alarm, does not affect date
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date.time
            set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        Log.d("AssignmentAlerts", "Parsed date for alert: $date")

        // Log the scheduled time before setting the alarm
        Log.d("AssignmentAlerts", "Scheduling alert for: ${calendar.timeInMillis}")

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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