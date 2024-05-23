package com.example.c196gt

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DeleteAssignmentActivity  : AppCompatActivity() {

    private lateinit var spinnerAssignments: Spinner
    private lateinit var buttonDeleteAssignment: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_assignment)

        spinnerAssignments = findViewById(R.id.spinnerAssignments)
        buttonDeleteAssignment = findViewById(R.id.buttonDeleteAssignment)

        databaseHelper = DatabaseHelper(this)

        // Populate spinner with Assignment names
        val assignmentNames = databaseHelper.getAllAssignmentNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, assignmentNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAssignments.adapter = adapter

        buttonDeleteAssignment.setOnClickListener {
            val assignmentName = spinnerAssignments.selectedItem.toString()

            val assignmentId = databaseHelper.getAssignmentIdByName(assignmentName) ?: -1 // Providing -1 as a default value if AssignmentId is null

            val rowsDeleted: Int = if (assignmentId != -1L) {
                // Assignment has no associated Assignments, show confirmation dialog
                showConfirmationDialog(assignmentId)
                0 // Indicates deletion is pending confirmation
            } else {
                // Assignment not found, deletion failed
                Toast.makeText(this@DeleteAssignmentActivity, "Assignment cannot be deleted because it does not exist", Toast.LENGTH_SHORT).show()
                0 // Indicates deletion failed
            }

            // If deletion was not pending confirmation and not failed, show appropriate toast message
            if (rowsDeleted != 0 && rowsDeleted != -1) {
                if (rowsDeleted > 0) {
                    // Assignment successfully deleted
                    Toast.makeText(this@DeleteAssignmentActivity, "Assignment deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Deletion failed
                    Toast.makeText(this@DeleteAssignmentActivity, "Failed to delete Assignment", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showConfirmationDialog(assignmentId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete the Assignment?")
            .setPositiveButton("Yes") { dialog, which ->
                // User confirmed deletion
                val deletedRows = databaseHelper.deleteAssignment(assignmentId)
                if (deletedRows > 0) {
                    // Assignment deleted successfully, finish activity with result
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@DeleteAssignmentActivity, "Failed to delete Assignment", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}