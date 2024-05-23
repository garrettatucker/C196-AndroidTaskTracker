package com.example.c196gt

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DeleteCourseActivity  : AppCompatActivity() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var buttonDeleteCourse: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_course)

        spinnerCourses = findViewById(R.id.spinnerCourses)
        buttonDeleteCourse = findViewById(R.id.buttonDeleteCourse)

        databaseHelper = DatabaseHelper(this)

        // Populate spinner with Course names
        val courseNames = databaseHelper.getAllCourseNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapter

        buttonDeleteCourse.setOnClickListener {
            val courseName = spinnerCourses.selectedItem.toString()

            val courseId = databaseHelper.getCourseIdByName(courseName) ?: -1 // Providing -1 as a default value if CourseId is null

            val rowsDeleted: Int = if (courseId != -1L) {
                    // Course has no associated courses, show confirmation dialog
                    showConfirmationDialog(courseId)
                    0 // Indicates deletion is pending confirmation
            } else {
                // course not found, deletion failed
                Toast.makeText(this@DeleteCourseActivity, "Course cannot be deleted because it does not exist", Toast.LENGTH_SHORT).show()
                0 // Indicates deletion failed
            }

            // If deletion was not pending confirmation and not failed, show appropriate toast message
            if (rowsDeleted != 0 && rowsDeleted != -1) {
                if (rowsDeleted > 0) {
                    // course successfully deleted
                    Toast.makeText(this@DeleteCourseActivity, "Course deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Deletion failed
                    Toast.makeText(this@DeleteCourseActivity, "Failed to delete course", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showConfirmationDialog(courseId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete the course?")
            .setPositiveButton("Yes") { dialog, which ->
                // User confirmed deletion
                val deletedRows = databaseHelper.deleteCourse(courseId)
                if (deletedRows > 0) {
                    // course deleted successfully, finish activity with result
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@DeleteCourseActivity, "Failed to delete course", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}