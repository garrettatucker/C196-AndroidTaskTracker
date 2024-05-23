package com.example.c196gt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ViewAssignmentsDetailsActivity : AppCompatActivity()  {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewAssignments: RecyclerView
    private lateinit var deleteAssignmentActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_assignments_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dbHelper = DatabaseHelper(this)


        // Initialize recyclerViewAssignments
        recyclerViewAssignments = findViewById(R.id.recycler_view_assignments)

        // Set the layout manager
        recyclerViewAssignments.layoutManager = LinearLayoutManager(this)

        // Fetch assignments from the database using your DatabaseHelper method
        val dbHelper = DatabaseHelper(this)
        val assignmentsList = dbHelper.getAllAssignments()

        // Log the contents of the assignmentsList
        for (assignment in assignmentsList) {
            Log.d("AssignmentsList", "Assignment Name: ${assignment.assignmentName}")
        }

        // Create and set up the adapter
        val adapter = AssignmentAdapter(assignmentsList)
        recyclerViewAssignments.adapter = adapter

        // Log adapter setup
        Log.d("AdapterSetup", "Adapter setup completed")

        // Initialize the ActivityResultLauncher
        deleteAssignmentActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Term has been deleted, refresh term list
                refreshAssignmentList()
            }
        }

        val buttonEnterDeleteAssignment = findViewById<Button>(R.id.buttonEnterDeleteAssignment)

        buttonEnterDeleteAssignment.setOnClickListener {
            // Start DeleteCourseActivity with request code
            val intent = Intent(this, DeleteAssignmentActivity::class.java)
            deleteAssignmentActivityResultLauncher.launch(intent)
        }
    }
    // Method to handle button click to navigate to entering Course details activity
    fun onEnterAssignmentDetailsClick(view: View?) {
        val intent: Intent = Intent(this, EnterAssignmentDetailsActivity::class.java)
        startActivity(intent)
    }
    // Method to handle button click to navigate to entering course details activity
    fun onEnterAssignmentAlertClick(view: View?) {
        val intent = Intent(this, SetAssignmentAlertsActivity::class.java)
        startActivity(intent)
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
    private fun refreshAssignmentList() {
        // Fetch assignments from the database using your DatabaseHelper method
        val dbHelper = DatabaseHelper(this)
        val assignmentsList = dbHelper.getAllAssignments()

        // Log the contents of the assignmentsList
        for (assignment in assignmentsList) {
            Log.d("AssignmentsList", "Assignment Name: ${assignment.assignmentName}")
        }

        // Update the adapter with the new data
        val adapter = recyclerViewAssignments.adapter as AssignmentAdapter
        adapter.updateData(assignmentsList)
    }
}