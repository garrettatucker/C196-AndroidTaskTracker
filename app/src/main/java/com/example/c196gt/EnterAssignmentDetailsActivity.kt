package com.example.c196gt

import Assignment
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.ParseException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Locale

class EnterAssignmentDetailsActivity : AppCompatActivity() {

    private lateinit var editTextAssignmentName: EditText
    private lateinit var editTextEndDay: EditText
    private lateinit var editTextEndMonth: EditText
    private lateinit var editTextEndYear: EditText
    private lateinit var spinnerAssessment: Spinner
    private lateinit var spinnerCourses: Spinner
    private lateinit var buttonAddAssignment: Button
    private lateinit var buttonSubmitAndClear: Button
    private lateinit var buttonSbmtAssignmentEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_assignment_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize button
        buttonAddAssignment= findViewById(R.id.buttonAddAssignment)
        buttonSubmitAndClear = findViewById(R.id.buttonSubmitAndClear)
        buttonSbmtAssignmentEdit = findViewById(R.id.buttonSbmtAssignmentEdit)


        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Find the TextView by its ID
        val startDateTextView: TextView = findViewById(R.id.startDateTextView)

        // Get today's date
        val startDate: LocalDate = LocalDate.now()

        // Format the date (optional)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val formattedStartDate: String = startDate.format(formatter)

        // Set the text of the TextView to today's date
        startDateTextView.text = "Start Date: $formattedStartDate"

        // Retrieve the mode from intent extras
        val mode = intent.getStringExtra("mode")
        Log.d("EnterCourseDetails", "Mode: $mode")
        // Retrieve the assignment ID from intent extras


        if (mode == "edit") {
            // Hide add buttons, show edit button
            buttonAddAssignment.visibility = View.GONE
            buttonSubmitAndClear.visibility = View.GONE
            buttonSbmtAssignmentEdit.visibility = View.VISIBLE
        } else {
            // Hide edit button, show add buttons
            buttonSbmtAssignmentEdit.visibility = View.GONE
            buttonAddAssignment.visibility = View.VISIBLE
            buttonSubmitAndClear.visibility = View.VISIBLE
        }


        // Initialize EditText fields
        editTextAssignmentName = findViewById(R.id.editTextAssignmentName)
        editTextEndMonth = findViewById(R.id.editTextEndMonth)
        editTextEndDay = findViewById(R.id.editTextEndDay)
        editTextEndYear = findViewById(R.id.editTextEndYear)
        spinnerAssessment = findViewById(R.id.spinnerAssessment)
        spinnerCourses = findViewById(R.id.spinnerCourses)

        // Retrieve intent extras
        val assignmentName = intent.getStringExtra("ASSIGNMENT_NAME")
        val dueDateMonth = intent.getStringExtra("DUE_DATE_MONTH")
        val dueDateDay = intent.getStringExtra("DUE_DATE_DAY")
        val dueDateYear = intent.getStringExtra("DUE_DATE_YEAR")

        // Set values to EditText fields and Spinners
        editTextAssignmentName.setText(assignmentName)
        editTextEndMonth.setText(dueDateMonth)
        editTextEndDay.setText(dueDateDay)
        editTextEndYear.setText(dueDateYear)

        // Set up the spinner with courses
        populateCourseSpinner()



        // Set listener for the month field of end date
        editTextEndMonth.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Move focus to the day field of the end date
                editTextEndDay.requestFocus()
                true
            } else {
                false
            }
        }

        // Set listener for the day field of end date
        editTextEndDay.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Move focus to the year field of the end date
                editTextEndYear.requestFocus()
                true
            } else {
                false
            }
        }

        // Set listener for the year field of end date
        editTextEndYear.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide the keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextEndYear.windowToken, 0)
                true
            } else {
                false
            }
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.assignment_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerAssessment.adapter = adapter
        }



        // Set click listener for Add Term button
        buttonAddAssignment.setOnClickListener {
            if(validateForm()) {
                addAssignment()
            }
        }

        // Set click listener for the submit and clear button
        buttonSubmitAndClear.setOnClickListener {
            if (validateForm()) {
                addAssignmentMulti()
                clearForm()
            }
        }
        buttonSbmtAssignmentEdit.setOnClickListener {
            if(validateForm()) {
                updateAssignment()
            }
        }
    }

    private fun updateAssignment() {
        // Get input values
        val assignmentId = intent.getLongExtra("ASSIGNMENT_ID", -1)
        val assignmentName = editTextAssignmentName.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()
        val assessmentType = spinnerAssessment.selectedItem.toString()
        val courseName = spinnerCourses.selectedItem.toString()

        // Combine input values to form start and end dates
        val endDate = "$endMonth-$endDay-$endYear"

        // Get courseId from courseName
        val dbHelper = DatabaseHelper(this)
        val courseId = dbHelper.getCourseIdByName(courseName)

        Log.d("UpdateAssignment", "Assignment ID: $assignmentId")
        Log.d("UpdateAssignment", "Assignment Name: $assignmentName")
        Log.d("UpdateAssignment", "End Date: $endDate")
        Log.d("UpdateAssignment", "Assessment Type: $assessmentType")
        Log.d("UpdateAssignment", "Course Name: $courseName")
        Log.d("UpdateAssignment", "Course ID: $courseId")

        if (courseId != null) {
            // Update the course in the database
            val rowsAffected = dbHelper.updateAssignment(
                assignmentId,
                assignmentName,
                endDate,
                assessmentType,
                courseId
            )

            if (rowsAffected > 0) {
                // Course updated successfully
                Toast.makeText(this, "assignment updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ViewAssignmentsDetailsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Failed to update course
                Toast.makeText(this, "Failed to update assignment", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Failed to retrieve termId
            Log.e("Database", "Failed to retrieve courseId")
            Toast.makeText(this, "Failed to retrieve courseId", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle toolbar item clicks
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle back button click
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun validateForm(): Boolean {
        val AssignmentName = editTextAssignmentName.text.toString()
        val endDate = parseDate(
            editTextEndMonth.text.toString(),
            editTextEndDay.text.toString(),
            editTextEndYear.text.toString()
        )
        if (AssignmentName.isEmpty()) {
            Toast.makeText(this, "Assignment name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (endDate == null) {
            Toast.makeText(this, "Invalid end date", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun parseDate(month: String, day: String, year: String): Date? {
        if (month.isBlank() || day.isBlank() || year.isBlank()) {
            // Show Toast for empty input
            Toast.makeText(this, "Date components cannot be empty", Toast.LENGTH_SHORT).show()
            return null
        }

        // Construct date string
        val dateString = "$month/$day/$year"

        try {
            // Parse date string
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            return dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // Show Toast for parse error
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return null
        }
    }
    // Function to clear the form fields
    private fun clearForm() {
        editTextAssignmentName.text.clear()
        editTextEndMonth.text.clear()
        editTextEndDay.text.clear()
        editTextEndYear.text.clear()
    }


    private fun addAssignmentToDatabase(assignment: Assignment) {
        val db = DatabaseHelper(this).writableDatabase

        val values = ContentValues().apply {
            put("assignment_name", assignment.assignmentName)
            put("start_date", assignment.startDate)
            put("due_date", assignment.dueDate)
            put("assessment_type", assignment.assessmentType)
            put("course_id", assignment.courseId)
        }

        val newRowId = db.insert("Assignments", null, values)

        if (newRowId != -1L) {
            Toast.makeText(this, "Assignment added successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error adding assignment", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }

    private fun addAssignment() {
        val assignmentName = editTextAssignmentName.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()
        val assessmentType = spinnerAssessment.selectedItem.toString()
        val courseId = spinnerCourses.selectedItemId // Assuming this returns courseId

        val startDate: LocalDate = LocalDate.now()

        val endDate = "$endMonth/$endDay/$endYear"

        // Create Assignment object
        val assignment = Assignment(
            assignmentId = -1, // Assuming you manage assignmentId in the database
            assignmentName = assignmentName,
            startDate = startDate.toString(),
            dueDate = endDate,
            assessmentType = assessmentType,
            courseId = courseId
        )

        addAssignmentToDatabase(assignment)
        val intent = Intent(this, ViewAssignmentsDetailsActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun addAssignmentMulti() {
        val assignmentName = editTextAssignmentName.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()
        val assessmentType = spinnerAssessment.selectedItem.toString()
        val courseId = spinnerCourses.selectedItemId // Assuming this returns courseId

        val startDate: LocalDate = LocalDate.now()

        val endDate = "$endMonth/$endDay/$endYear"

        // Create Assignment object
        val assignment = Assignment(
            assignmentId = -1, // Assuming you manage assignmentId in the database
            assignmentName = assignmentName,

            startDate = startDate.toString(),
            dueDate = endDate,
            assessmentType = assessmentType,
            courseId = courseId
        )

        addAssignmentToDatabase(assignment)
    }
    private fun fetchCoursesFromDatabase(): List<String> {
        val coursesList = mutableListOf<String>()

        // Open the database
        val db = DatabaseHelper(this).readableDatabase

        // Query to fetch all courses
        val projection = arrayOf("course_name")
        val cursor = db.query("Courses", projection, null, null, null, null, null)

        // Loop through the cursor and add course names to the list
        with(cursor) {
            while (moveToNext()) {
                val courseName = getString(getColumnIndexOrThrow("course_name"))
                coursesList.add(courseName)
            }
        }

        // Close cursor and database
        cursor.close()
        db.close()

        return coursesList
    }
    private fun populateCourseSpinner() {
        val coursesList = fetchCoursesFromDatabase()

        // Create an ArrayAdapter using the list of courses and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, coursesList)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerCourses.adapter = adapter
    }




}