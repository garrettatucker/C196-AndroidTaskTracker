package com.example.c196gt

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.ParseException
import java.util.*
import java.util.Locale


class EnterTermDetailsActivity : AppCompatActivity() {

    private lateinit var editTextTermName: EditText
    private lateinit var editTextStartMonth: EditText
    private lateinit var editTextStartDay: EditText
    private lateinit var editTextStartYear: EditText
    private lateinit var editTextEndMonth: EditText
    private lateinit var editTextEndDay: EditText
    private lateinit var editTextEndYear: EditText
    private lateinit var buttonAddTerm: Button
    private lateinit var buttonSubmitAndClear: Button

    companion object {
        private const val TABLE_TERMS = "Terms"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_term_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        // Initialize EditText fields
        editTextTermName = findViewById(R.id.editTextTermName)
        editTextStartMonth = findViewById(R.id.editTextStartMonth)
        editTextStartDay = findViewById(R.id.editTextStartDay)
        editTextStartYear = findViewById(R.id.editTextStartYear)
        editTextEndMonth = findViewById(R.id.editTextEndMonth)
        editTextEndDay = findViewById(R.id.editTextEndDay)
        editTextEndYear = findViewById(R.id.editTextEndYear)

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

        // Initialize button
        buttonAddTerm = findViewById(R.id.buttonAddTerm)

        // Set click listener for Add Term button
        buttonAddTerm.setOnClickListener {
            if(validateForm()) {
                addTerm()
            }
        }
        // Initialize the button for submitting and clearing the form
        buttonSubmitAndClear = findViewById(R.id.buttonSubmitAndClear)

        // Set click listener for the submit and clear button
        buttonSubmitAndClear.setOnClickListener {
            if (validateForm()) {
                addTermMulti()
                clearForm()
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
        val termName = editTextTermName.text.toString()
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

        if (termName.isEmpty()) {
            Toast.makeText(this, "Term name cannot be empty", Toast.LENGTH_SHORT).show()
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
        editTextTermName.text.clear()
        editTextStartMonth.text.clear()
        editTextStartDay.text.clear()
        editTextStartYear.text.clear()
        editTextEndMonth.text.clear()
        editTextEndDay.text.clear()
        editTextEndYear.text.clear()
    }


    private fun addTerm() {
        // Get input values
        val termName = editTextTermName.text.toString()
        val startMonth = editTextStartMonth.text.toString()
        val startDay = editTextStartDay.text.toString()
        val startYear = editTextStartYear.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()

        // Combine input values to form start and end dates
        val startDate = "$startMonth/$startDay/$startYear"
        val endDate = "$endMonth/$endDay/$endYear"

        // Create a ContentValues object to store the term details
        val values = ContentValues().apply {
            put("term_name", termName)
            put("start_date", startDate)
            put("end_date", endDate)
        }

        // Get an instance of the SQLite database
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        // Insert the term details into the database
        val newRowId = db.insert(TABLE_TERMS, null, values)

        // Close the database connection
        db.close()

        if (newRowId != -1L) {
            // If insertion was successful, display a success message
            Toast.makeText(this, "Term added successfully", Toast.LENGTH_SHORT).show()
            // Navigate back to the list of terms page and refresh the list
            val intent = Intent(this, ViewTermDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish() // Finish the current activity to prevent it from stacking on top of the previous one
        } else {
            // If insertion failed, display an error message
            Toast.makeText(this, "Failed to add term", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTermMulti() {
        // Get input values
        val termName = editTextTermName.text.toString()
        val startMonth = editTextStartMonth.text.toString()
        val startDay = editTextStartDay.text.toString()
        val startYear = editTextStartYear.text.toString()
        val endMonth = editTextEndMonth.text.toString()
        val endDay = editTextEndDay.text.toString()
        val endYear = editTextEndYear.text.toString()

        // Combine input values to form start and end dates
        val startDate = "$startMonth/$startDay/$startYear"
        val endDate = "$endMonth/$endDay/$endYear"

        // Create a ContentValues object to store the term details
        val values = ContentValues().apply {
            put("term_name", termName)
            put("start_date", startDate)
            put("end_date", endDate)
        }

        // Get an instance of the SQLite database
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        // Insert the term details into the database
        val newRowId = db.insert(TABLE_TERMS, null, values)

        // Close the database connection
        db.close()

        if (newRowId != -1L) {
            // If insertion was successful, display a success message
            Toast.makeText(this, "Term added successfully", Toast.LENGTH_SHORT).show()
            // Navigate back to the list of terms page and refresh the list
        } else {
            // If insertion failed, display an error message
            Toast.makeText(this, "Failed to add term", Toast.LENGTH_SHORT).show()
        }
    }
}
