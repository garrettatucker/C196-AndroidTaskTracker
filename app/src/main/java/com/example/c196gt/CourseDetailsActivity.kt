package com.example.c196gt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CourseDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Find TextViews by their IDs
        val text_view_course_name: TextView = findViewById(R.id.text_view_course_name)
        val text_view_start_date: TextView = findViewById(R.id.text_view_start_date)
        val text_view_end_date: TextView = findViewById(R.id.text_view_end_date)
        val text_view_instructor_name: TextView = findViewById(R.id.text_view_instructor_name)
        val text_view_phone_number: TextView = findViewById(R.id.text_view_phone_number)
        val text_view_email_address: TextView = findViewById(R.id.text_view_email_address)
        val text_view_progress_status: TextView = findViewById(R.id.text_view_progress_status)
        val text_view_notes: TextView = findViewById(R.id.text_view_notes)
        val termNameTextView: TextView = findViewById(R.id.text_view_term_name)
        val viewNotesButton = findViewById<Button>(R.id.buttonViewNotes)

        // Retrieve course details from intent extras
        val courseName = intent.getStringExtra("COURSE_NAME")
        val startDate = intent.getStringExtra("START_DATE")
        val endDate = intent.getStringExtra("END_DATE")
        val instructorName = intent.getStringExtra("INSTRUCTOR_NAME")
        val phoneNumber = intent.getStringExtra("PHONE_NUMBER")
        val emailAddress = intent.getStringExtra("EMAIL_ADDRESS")
        val progressStatus = intent.getStringExtra("PROGRESS_STATUS")
        val notes = intent.getStringExtra("NOTES")
        val termName = intent.getStringExtra("TERM_NAME")
        // Get courseId from SQLite database
        val dbHelper = DatabaseHelper(this)
        val courseId = dbHelper.getCourseIdByDetails(courseName, startDate, endDate, instructorName, phoneNumber, emailAddress, progressStatus, notes, termName)


        // Set retrieved details to TextViews
        text_view_course_name.text = courseName
        text_view_start_date.text = startDate
        text_view_end_date.text = endDate
        text_view_instructor_name.text = instructorName
        text_view_phone_number.text = phoneNumber
        text_view_email_address.text = emailAddress
        text_view_progress_status.text = progressStatus
        text_view_notes.text = notes
        termNameTextView.text = termName

        // Edit button click listener
        val editButton = findViewById<Button>(R.id.buttonEditCourse)
        editButton.setOnClickListener {
            Log.d("EditCourse", "Edit Button Clicked")

            // Split the start date and end date, while removing "/" or "-"
            val startDateComponents = startDate?.split(Regex("[/-]"))?.map { it.replace("[/-]", "") }
            val endDateComponents = endDate?.split(Regex("[/-]"))?.map { it.replace("[/-]", "") }

            // Ensure that the date has been split into components and that each component is valid
            if (startDateComponents?.size == 3 && endDateComponents?.size == 3) {
                val cleanedStartDate = startDateComponents[0] // Month
                val cleanedEndDate = endDateComponents[0] // Month

                val editIntent = Intent(this, EnterCourseDetailsActivity::class.java)
                editIntent.putExtra("COURSE_ID", courseId)
                editIntent.putExtra("mode", "edit")
                editIntent.putExtra("COURSE_NAME", courseName)
                editIntent.putExtra("START_MONTH", cleanedStartDate)
                editIntent.putExtra("START_DAY", startDateComponents[1]) // Day
                editIntent.putExtra("START_YEAR", startDateComponents[2]) // Year
                editIntent.putExtra("END_MONTH", cleanedEndDate)
                editIntent.putExtra("END_DAY", endDateComponents[1]) // Day
                editIntent.putExtra("END_YEAR", endDateComponents[2]) // Year
                editIntent.putExtra("INSTRUCTOR_NAME", instructorName)
                editIntent.putExtra("PHONE_NUMBER", phoneNumber)
                editIntent.putExtra("EMAIL_ADDRESS", emailAddress)
                editIntent.putExtra("PROGRESS_STATUS", progressStatus)
                editIntent.putExtra("NOTES", notes)
                editIntent.putExtra("TERM_NAME", termName)
                startActivity(editIntent)
            } else {
            // Handle invalid date format
            Log.e("DateParsing", "Invalid date format: $startDate or $endDate")
            // You may want to show a toast or dialog to inform the user about the invalid date format
            }
        }
        viewNotesButton.setOnClickListener {
            // Call a function to retrieve and display the notes
            if (notes != null) {
                showNotesDialog(courseId, notes)
            }
        }
    }
    private fun showNotesDialog(courseId: Long, notes: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Course Notes")
        dialogBuilder.setMessage(notes)
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Send Email") { _, _ ->
            // Launch email intent
            sendEmail(notes)
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
    private fun sendEmail(notes: String) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Course Notes")
            putExtra(Intent.EXTRA_TEXT, notes)
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send As Email"))
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

}

