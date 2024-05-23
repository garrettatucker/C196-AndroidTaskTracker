package com.example.c196gt

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.ParseException
import java.util.*
import java.util.Locale
import android.widget.EditText;


class EnterCourseDetailsActivity : AppCompatActivity() {

    private lateinit var editTextCourseName: EditText
    private lateinit var editTextStartMonth: EditText
    private lateinit var editTextStartDay: EditText
    private lateinit var editTextStartYear: EditText
    private lateinit var editTextEndMonth: EditText
    private lateinit var editTextEndDay: EditText
    private lateinit var editTextEndYear: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerTerms: Spinner
    private lateinit var editTextInstructorName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextEmailAddress: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var buttonAddCourse: Button
    private lateinit var buttonSubmitAndClear: Button
    private lateinit var buttonSbmtCourseEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_course_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize button
        buttonAddCourse= findViewById(R.id.buttonAddCourse)
        buttonSubmitAndClear = findViewById(R.id.buttonSubmitAndClear)
        buttonSbmtCourseEdit= findViewById(R.id.buttonSbmtCourseEdit)
        editTextNotes = findViewById(R.id.editTextNotes)



        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Retrieve the mode from intent extras
        val mode = intent.getStringExtra("mode")
        Log.d("EnterCourseDetails", "Mode: $mode")

        if (mode == "edit") {
            // Hide add buttons, show edit button
            buttonAddCourse.visibility = View.GONE
            buttonSubmitAndClear.visibility = View.GONE
            buttonSbmtCourseEdit.visibility = View.VISIBLE
        } else {
            // Hide edit button, show add buttons
            buttonSbmtCourseEdit.visibility = View.GONE
            buttonAddCourse.visibility = View.VISIBLE
            buttonSubmitAndClear.visibility = View.VISIBLE
        }


        // Initialize EditText fields
        editTextCourseName = findViewById(R.id.editTextCourseName)
        editTextStartMonth = findViewById(R.id.editTextStartMonth)
        editTextStartDay = findViewById(R.id.editTextStartDay)
        editTextStartYear = findViewById(R.id.editTextStartYear)
        editTextEndMonth = findViewById(R.id.editTextEndMonth)
        editTextEndDay = findViewById(R.id.editTextEndDay)
        editTextEndYear = findViewById(R.id.editTextEndYear)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        spinnerTerms = findViewById(R.id.spinnerTerms)
        editTextInstructorName = findViewById(R.id.editTextInstructorName)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress)
        editTextNotes = findViewById(R.id.editTextNotes)

        // Retrieve course details from intent extras
        val extras = intent.extras
        if (extras != null) {
            val courseName = extras.getString("COURSE_NAME")
            val startMonth = extras.getString("START_MONTH")
            val startDay = extras.getString("START_DAY")
            val startYear = extras.getString("START_YEAR")
            val endMonth = extras.getString("END_MONTH")
            val endDay = extras.getString("END_DAY")
            val endYear = extras.getString("END_YEAR")
            val instructorName = extras.getString("INSTRUCTOR_NAME")
            val phoneNumber = extras.getString("PHONE_NUMBER")
            val emailAddress = extras.getString("EMAIL_ADDRESS")
            val progressStatus = extras.getString("PROGRESS_STATUS")
            val notes = extras.getString("NOTES")
            val termName = extras.getString("TERM_NAME")

            // Set retrieved details to EditText fields
            editTextCourseName.setText(courseName)
            editTextStartMonth.setText(startMonth)
            editTextStartDay.setText(startDay)
            editTextStartYear.setText(startYear)
            editTextEndMonth.setText(endMonth)
            editTextEndDay.setText(endDay)
            editTextEndYear.setText(endYear)
            editTextInstructorName.setText(instructorName)
            editTextPhoneNumber.setText(phoneNumber)
            editTextEmailAddress.setText(emailAddress)
            // Set spinner selection
            val statusIndex = resources.getStringArray(R.array.status_options).indexOf(progressStatus)
            spinnerStatus.setSelection(statusIndex)
            val termsIndex = (spinnerTerms.adapter as ArrayAdapter<String>).getPosition(termName)
            spinnerTerms.setSelection(termsIndex)
            editTextNotes.setText(notes)
        }

        // Fetch terms from the database
        val termsList = DatabaseHelper(this).getAllTermNames()

        // Populate spinner with terms
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, termsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTerms.adapter = adapter


        // Set listener for the month field
        editTextStartMonth.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Move focus to the day field
                editTextStartDay.requestFocus()
                true
            } else {
                false
            }
        }

        // Set listener for the day field
        editTextStartDay.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Move focus to the year field
                editTextStartYear.requestFocus()
                true
            } else {
                false
            }
        }

        // Set listener for the year field
        editTextStartYear.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Move focus to the month field of the end date
                editTextEndMonth.requestFocus()
                true
            } else {
                false
            }
        }

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
            R.array.status_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerStatus.adapter = adapter
        }
        editTextPhoneNumber.addTextChangedListener(object : TextWatcher {
            var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) {
                    return
                }

                isFormatting = true

                val text = s.toString()
                if (text.isNotEmpty() && !text.endsWith("-")) {
                    val sanitizedString = text.replace("-", "")
                    val formattedString = buildString {
                        for (i in sanitizedString.indices) {
                            if (i == 3 || i == 6) {
                                append('-')
                            }
                            append(sanitizedString[i])
                        }
                    }
                    editTextPhoneNumber.setText(formattedString)
                    editTextPhoneNumber.setSelection(formattedString.length)
                }

                isFormatting = false
            }
        })




        // Set click listener for Add Term button
        buttonAddCourse.setOnClickListener {
            if(validateForm()) {
                addCourse()
            }
        }

        // Set click listener for the submit and clear button
        buttonSubmitAndClear.setOnClickListener {
            if (validateForm()) {
                addCourseMulti()
                clearForm()
            }
        }
        buttonSbmtCourseEdit.setOnClickListener {
            if(validateForm()) {
                updateCourse()
            }
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
        val courseName = editTextCourseName.text.toString()
        val instructorName = editTextInstructorName.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val emailAddress = editTextEmailAddress.text.toString()
        val startDate = parseDate(
            editTextStartMonth.text.toString(),
            editTextStartDay.text.toString(),
            editTextStartYear.text.toString()
        )
        val endDate = parseDate(
            editTextEndMonth.text.toString(),
            editTextEndDay.text.toString(),
            editTextEndYear.text.toString()
        )

        if (courseName.isEmpty()) {
            Toast.makeText(this, "Course name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (startDate == null) {
            Toast.makeText(this, "Invalid start date", Toast.LENGTH_SHORT).show()
            return false
        }

        if (endDate == null) {
            Toast.makeText(this, "Invalid end date", Toast.LENGTH_SHORT).show()
            return false
        }

        if (startDate >= endDate) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
            return false
        }
        if (instructorName.isEmpty()) {
            Toast.makeText(this, "Instructor name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT).show()
            return false
        }

        if (emailAddress.isEmpty()) {
            Toast.makeText(this, "Email address cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (!isValidEmail(emailAddress)) {
            Toast.makeText(this, "Invalid email address format", Toast.LENGTH_SHORT).show()
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

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Regular expression for a valid phone number format
        val numericPhoneNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        val regex = """^\d{10}$""".toRegex()
        return regex.matches(numericPhoneNumber)
    }

    private fun isValidEmail(email: String): Boolean {
        // Regular expression for a valid email address format
        val regex = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$""".toRegex()
        return regex.matches(email)
    }
    // Function to clear the form fields
    private fun clearForm() {
        editTextCourseName.text.clear()
        editTextStartMonth.text.clear()
        editTextStartDay.text.clear()
        editTextStartYear.text.clear()
        editTextEndMonth.text.clear()
        editTextEndDay.text.clear()
        editTextEndYear.text.clear()
        editTextInstructorName.text.clear()
        editTextPhoneNumber.text.clear()
        editTextEmailAddress.text.clear()
        editTextNotes.text.clear()
    }


    private fun addCourse() {
        // Get input values
        val courseName = editTextCourseName.text.toString()
        val startMonth = editTextStartMonth.text.toString()
        val startDay = editTextStartDay.text.toString()
        val startYear = editTextStartYear.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()
        val instructorName = editTextInstructorName.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val emailAddress = editTextEmailAddress.text.toString()
        val progressStatus = spinnerStatus.selectedItem.toString()
        val notes = editTextNotes.text.toString()
        val termName = spinnerTerms.selectedItem.toString()

        // Combine input values to form start and end dates
        val startDate = "$startMonth/$startDay/$startYear"
        val endDate = "$endMonth/$endDay/$endYear"

        // Insert the course into the database
        val dbHelper = DatabaseHelper(this)
        val termId = dbHelper.getTermIdByName(termName)

        if (termId != null) {
            // Insert the course into the database
            val courseId = dbHelper.insertCourse(
                courseName,
                startDate,
                endDate,
                instructorName,
                phoneNumber,
                emailAddress,
                progressStatus,
                notes,
                termId
            )

            if (courseId != -1L) {
                // Course added successfully
                Log.d("Database", "Course added successfully: $courseName, $startDate, $endDate, $instructorName, $phoneNumber, $emailAddress, $progressStatus, $notes, $termName")
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ViewCoursesDetailsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Failed to add course
                Log.e("Database", "Failed to add course")
                Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Failed to retrieve term ID
            Log.e("Database", "Failed to retrieve term ID")
            Toast.makeText(this, "Failed to retrieve term ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCourseMulti() {
        // Get input values
        val courseName = editTextCourseName.text.toString()
        val startMonth = editTextStartMonth.text.toString()
        val startDay = editTextStartDay.text.toString()
        val startYear = editTextStartYear.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()
        val instructorName = editTextInstructorName.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val emailAddress = editTextEmailAddress.text.toString()
        val progressStatus = spinnerStatus.selectedItem.toString()
        val notes = editTextNotes.text.toString()
        val termName = spinnerTerms.selectedItem.toString()

        // Combine input values to form start and end dates
        val startDate = "$startMonth/$startDay/$startYear"
        val endDate = "$endMonth/$endDay/$endYear"

        // Insert the course into the database
        val dbHelper = DatabaseHelper(this)
        val termId = dbHelper.getTermIdByName(termName)

        if (termId != null) {
            // Insert the course into the database
            val courseId = dbHelper.insertCourse(
                courseName,
                startDate,
                endDate,
                instructorName,
                phoneNumber,
                emailAddress,
                progressStatus,
                notes,
                termId
            )

            if (courseId != -1L) {
                // Course added successfully
                Log.d("Database", "Course added successfully: $courseName, $startDate, $endDate, $instructorName, $phoneNumber, $emailAddress, $progressStatus, $notes, $termName")
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Failed to add course
                Log.e("Database", "Failed to add course")
                Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Failed to retrieve term ID
            Log.e("Database", "Failed to retrieve term ID")
            Toast.makeText(this, "Failed to retrieve term ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCourse() {
        // Get input values
        val courseId = intent.getLongExtra("COURSE_ID", -1) // Assuming you're passing the courseId from the previous activity
        val courseName = editTextCourseName.text.toString()
        val startMonth = editTextStartMonth.text.toString()
        val startDay = editTextStartDay.text.toString()
        val startYear = editTextStartYear.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()
        val instructorName = editTextInstructorName.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val emailAddress = editTextEmailAddress.text.toString()
        val progressStatus = spinnerStatus.selectedItem.toString()
        val notes = editTextNotes.text.toString() // Assuming you have editTextNotes in your layout
        val termName = spinnerTerms.selectedItem.toString()

        // Combine input values to form start and end dates
        val startDate = "$startMonth-$startDay-$startYear"
        val endDate = "$endMonth-$endDay-$endYear"

        // Get termId from termName
        val dbHelper = DatabaseHelper(this)
        val termId = dbHelper.getTermIdByName(termName)

        if (termId != null) {
            // Update the course in the database
            val rowsAffected = dbHelper.updateCourse(
                courseId,
                courseName,
                startDate,
                endDate,
                instructorName,
                phoneNumber,
                emailAddress,
                progressStatus,
                notes,
                termId
            )

            if (rowsAffected > 0) {
                // Course updated successfully
                Log.d("Database", "Course updated successfully: $courseId, $courseName, $startDate, $endDate, $instructorName, $phoneNumber, $emailAddress, $progressStatus, $notes, $termName")
                Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ViewCoursesDetailsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Failed to update course
                Log.e("Database", "Failed to update course: $courseId, $courseName, $startDate, $endDate, $instructorName, $phoneNumber, $emailAddress, $progressStatus, $notes, $termName")
                Toast.makeText(this, "Failed to update course", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Failed to retrieve termId
            Log.e("Database", "Failed to retrieve termId")
            Toast.makeText(this, "Failed to retrieve termId", Toast.LENGTH_SHORT).show()
        }
    }



}