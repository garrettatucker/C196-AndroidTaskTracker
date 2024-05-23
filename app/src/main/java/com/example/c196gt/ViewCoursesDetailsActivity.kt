package com.example.c196gt

import Course
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewCoursesDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerViewCourses: RecyclerView
    private lateinit var courseAdapter: CourseAdapter

    private lateinit var deleteCourseActivityResultLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_courses_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize recyclerViewCourses
        recyclerViewCourses = findViewById(R.id.recycler_view_courses)

        // Set the layout manager
        recyclerViewCourses.layoutManager = LinearLayoutManager(this)

        // Fetch courses from the database
        val coursesList = fetchCoursesFromDatabase()

        // Log the contents of the coursesList
        for (course in coursesList) {
            Log.d("CoursesList", "Course Name: ${course.courseName}")
        }

        // Initialize and set up the adapter
        courseAdapter = CourseAdapter(coursesList)
        recyclerViewCourses.adapter = courseAdapter

        // Initialize the ActivityResultLauncher
        deleteCourseActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Term has been deleted, refresh term list
                refreshCourseList()
            }
        }

        val buttonEnterDeleteCourse = findViewById<Button>(R.id.buttonEnterDeleteCourse)

        buttonEnterDeleteCourse.setOnClickListener {
            // Start DeleteCourseActivity with request code
            val intent = Intent(this, DeleteCourseActivity::class.java)
            deleteCourseActivityResultLauncher.launch(intent)
        }
    }

    // Method to handle button click to navigate to entering course details activity
    fun onEnterCourseDetailsClick(view: View?) {
        val mode = "add"
        Log.d("EnterCourseDetails", "Mode: $mode") // Log statement
        val intent = Intent(this, EnterCourseDetailsActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }
    // Method to handle button click to navigate to entering course details activity
    fun onEnterCourseAlertClick(view: View?) {
        val intent = Intent(this, SetCourseAlertsActivity::class.java)
        startActivity(intent)
    }

    private fun fetchCoursesFromDatabase(): List<Course> {
        val coursesList = mutableListOf<Course>()
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        // Query to fetch all courses
        val projection = arrayOf(
            "course_id", "course_name", "start_date", "end_date",
            "instructor_name", "phone_number", "email_address",
            "progress_status", "notes", "term_id"
        )
        val cursor = db.query("Courses", projection, null, null, null, null, null)

        // Loop through the cursor and add courses to the list
        with(cursor) {
            while (moveToNext()) {
                val courseId = getLong(getColumnIndexOrThrow("course_id"))
                val courseName = getString(getColumnIndexOrThrow("course_name"))
                val startDate = getString(getColumnIndexOrThrow("start_date"))
                val endDate = getString(getColumnIndexOrThrow("end_date"))
                val instructorName = getString(getColumnIndexOrThrow("instructor_name"))
                val phoneNumber = getString(getColumnIndexOrThrow("phone_number"))
                val emailAddress = getString(getColumnIndexOrThrow("email_address"))
                val progressStatus = getString(getColumnIndexOrThrow("progress_status"))
                val notes = getString(getColumnIndexOrThrow("notes"))
                val termId = getLong(getColumnIndexOrThrow("term_id"))
                val termName = fetchTermNameFromDatabase(db, termId)

                // Create Course object and add it to the list
                val course = Course(
                    courseId, courseName, startDate, endDate,
                    instructorName, phoneNumber, emailAddress,
                    progressStatus, notes, termId, termName
                )
                coursesList.add(course)
            }
        }

        // Close cursor (no need to close database here)
        cursor.close()

        return coursesList
    }

    private fun fetchTermNameFromDatabase(db: SQLiteDatabase, termId: Long): String {
        var termName = ""

        // Define the columns to be retrieved from the Terms table
        val projection = arrayOf("term_name")

        // Define the selection criteria
        val selection = "term_id = ?"
        val selectionArgs = arrayOf(termId.toString())

        // Execute the query
        val cursor = db.query("Terms", projection, selection, selectionArgs, null, null, null)

        // Extract the term name from the cursor if it exists
        if (cursor.moveToFirst()) {
            termName = cursor.getString(cursor.getColumnIndexOrThrow("term_name"))
        }

        // Close the cursor (no need to close database here)
        cursor.close()

        return termName
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

    override fun onResume() {
        super.onResume()
        // Refresh data when activity is resumed
        courseAdapter.notifyDataSetChanged()
        // Refresh data when activity is resumed
        fetchAndDisplayCourses()
    }

    private fun fetchAndDisplayCourses() {
        val courseList = fetchCoursesFromDatabase()
        courseAdapter.updateCourses(courseList)
    }
    // Add this method to refresh term list
    private fun refreshCourseList() {
        // Assuming termListRecyclerView is your RecyclerView variable
        recyclerViewCourses.adapter?.notifyDataSetChanged()
    }
}
