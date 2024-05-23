package com.example.c196gt


import android.app.NotificationManager
import android.content.Context
import android.content.Intent;
import android.icu.text.SimpleDateFormat
import android.os.Bundle;
import android.util.Log
import android.view.View;
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity;
import java.util.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


// Check if initial data has already been inserted
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val initialDataInserted = sharedPreferences.getBoolean("INITIAL_DATA_INSERTED", false)


        if (!initialDataInserted) {
            // Instantiate DatabaseHelper
            val dbHelper = DatabaseHelper(this)

            // Call insertInitialData
            dbHelper.writableDatabase.use { db ->
                dbHelper.insertInitialData(db)
            }

            // Update the flag in SharedPreferences to indicate that initial data has been inserted
            sharedPreferences.edit().putBoolean("INITIAL_DATA_INSERTED", true).apply()
        }

        // Get the TextView for Last Updated
        val textViewLastUpdated: TextView = findViewById(R.id.textViewLastUpdated)

        // Format the build timestamp and set it to the TextView
        val buildTimeMillis = BuildConfig.BUILD_TIME.toLong()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lastUpdated = dateFormat.format(Date(buildTimeMillis.toDouble().toLong()))
        textViewLastUpdated.text = "Last Updated: $lastUpdated"
    }

    // Method to handle button click to navigate to entering Course details activity
    fun onViewTermsDetailsClick(view: View?) {
        val intent: Intent = Intent(this, ViewTermDetailsActivity::class.java)
        startActivity(intent)
    }
    // Method to handle button click to navigate to entering Course details activity
    fun onViewCoursesDetailsClick(view: View?) {
        val intent: Intent = Intent(this, ViewCoursesDetailsActivity::class.java)
        startActivity(intent)
    }
    // Method to handle button click to navigate to entering Course details activity
    fun onViewAssignmentDetailsClick(view: View?) {
        val intent: Intent = Intent(this, ViewAssignmentsDetailsActivity::class.java)
        startActivity(intent)
    }

}