package com.example.c196gt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TermDetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_details)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Find TextViews by their IDs
        val text_view_term_name: TextView = findViewById(R.id.text_view_term_name)
        val text_view_start_date: TextView = findViewById(R.id.text_view_start_date)
        val text_view_end_date: TextView = findViewById(R.id.text_view_end_date)
        val text_view_course_names: TextView = findViewById(R.id.text_view_course_names)

        // Retrieve term details from intent extras
        val termName = intent.getStringExtra("TERM_NAME")
        val startDate = intent.getStringExtra("START_DATE")
        val endDate = intent.getStringExtra("END_DATE")
        val courseNames = intent.getStringArrayListExtra("COURSE_NAMES")
        val termId = intent.getLongExtra("TERM_ID", -1)

        // Set retrieved details to TextViews
        text_view_term_name.text = termName
        text_view_start_date.text = startDate
        text_view_end_date.text = endDate
        text_view_course_names.text = courseNames?.joinToString(separator = "\n")
    }
}
