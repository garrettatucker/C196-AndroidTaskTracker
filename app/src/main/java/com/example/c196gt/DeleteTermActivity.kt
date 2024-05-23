package com.example.c196gt

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DeleteTermActivity : AppCompatActivity() {

    private lateinit var spinnerTerms: Spinner
    private lateinit var buttonDeleteTerm: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_term)

        spinnerTerms = findViewById(R.id.spinnerTerms)
        buttonDeleteTerm = findViewById(R.id.buttonDeleteTerm)

        databaseHelper = DatabaseHelper(this)

        // Populate spinner with term names
        val termNames = databaseHelper.getAllTermNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, termNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTerms.adapter = adapter

        buttonDeleteTerm.setOnClickListener {
            val termName = spinnerTerms.selectedItem.toString()

            val termId = databaseHelper.getTermIdByName(termName) ?: -1 // Providing -1 as a default value if termId is null

            val rowsDeleted: Int = if (termId != -1L) {
                val hasCourses = databaseHelper.hasCourses(termId)
                if (hasCourses) {
                    // Term has associated courses, cannot be deleted directly
                    Toast.makeText(this@DeleteTermActivity, "Term cannot be deleted because it has associated courses", Toast.LENGTH_SHORT).show()
                    -1 // Indicate failure due to associated courses
                } else {
                    // Term has no associated courses, show confirmation dialog
                    showConfirmationDialog(termId)
                    0 // Indicates deletion is pending confirmation
                }
            } else {
                // Term not found, deletion failed
                Toast.makeText(this@DeleteTermActivity, "Term cannot be deleted because it does not exist", Toast.LENGTH_SHORT).show()
                0 // Indicates deletion failed
            }

            // If deletion was not pending confirmation and not failed, show appropriate toast message
            if (rowsDeleted != 0 && rowsDeleted != -1) {
                if (rowsDeleted > 0) {
                    // Term successfully deleted
                    Toast.makeText(this@DeleteTermActivity, "Term deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Deletion failed
                    Toast.makeText(this@DeleteTermActivity, "Failed to delete term", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showConfirmationDialog(termId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete the term?")
            .setPositiveButton("Yes") { dialog, which ->
                // User confirmed deletion
                val deletedRows = databaseHelper.deleteTerm(termId)
                if (deletedRows > 0) {
                    // Term deleted successfully, finish activity with result
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@DeleteTermActivity, "Failed to delete term", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
