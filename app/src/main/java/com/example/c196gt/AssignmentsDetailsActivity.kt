package com.example.c196gt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AssignmentsDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Find TextViews by their IDs
        val text_view_assignment_name: TextView = findViewById(R.id.textViewAssignmentName)
        val text_view_start_date: TextView = findViewById(R.id.textViewAssignmentStartDate)
        val text_view_due_date: TextView = findViewById(R.id.textViewAssignmentDueDate)
        val text_view_assignment_type: TextView = findViewById(R.id.textViewAssignmentType)

        // Retrieve course details from intent extras
        val assignmentName = intent.getStringExtra("ASSIGNMENT_NAME")
        val startDate = intent.getStringExtra("START_DATE")
        val dueDate = intent.getStringExtra("DUE_DATE")
        val assessmentType = intent.getStringExtra("ASSESSMENT_TYPE")
        // Get courseId from SQLite database
        val dbHelper = DatabaseHelper(this)
        val assignmentId = dbHelper.getAssignmentIdByDetails(assignmentName, dueDate, assessmentType)


        // Set retrieved details to TextViews
        text_view_assignment_name.text = assignmentName
        text_view_start_date.text = startDate
        text_view_due_date.text = dueDate
        text_view_assignment_type.text = assessmentType

        // Find the button by its ID
        val buttonEditAssignment: Button = findViewById(R.id.buttonEditAssignment)

        // Set a click listener for the button
        buttonEditAssignment.setOnClickListener {

            Log.d("EditCourse", "Edit Button Clicked")

            // Split the start date and end date, while removing "/" or "-"
            val dueDateComponents = dueDate?.split(Regex("[/-]"))?.map { it.replace("[/-]", "") }
            if (dueDateComponents?.size == 3) {
                val cleanedDueDate = dueDateComponents[0] // Month
                val day = dueDateComponents[1] // Day
                val year = dueDateComponents[2] // Year
                // Create intent to start EnterAssignmentDetailsActivity
                val editIntent = Intent(this, EnterAssignmentDetailsActivity::class.java)
                // Pass the assignment details as extras
                editIntent.putExtra("ASSIGNMENT_ID", assignmentId)
                editIntent.putExtra("ASSIGNMENT_NAME", assignmentName)
                editIntent.putExtra("DUE_DATE_MONTH", cleanedDueDate)
                editIntent.putExtra("DUE_DATE_DAY", day[0].toString()) // day
                editIntent.putExtra("DUE_DATE_YEAR", year) // Year
                editIntent.putExtra("ASSESSMENT_TYPE", assessmentType)
                editIntent.putExtra("mode", "edit")
                Log.d("ID Details", "assignmentId: $assignmentId")
                // Start EnterAssignmentDetailsActivity
                startActivity(editIntent)
            } else {
                // Handle invalid date format
                Log.e("DateParsing", "Invalid date format: $dueDate")
                // You may want to show a toast or dialog to inform the user about the invalid date format
            }

        }
    }
}