package com.example.c196gt

import Course
import Term
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.database.sqlite.SQLiteDatabase
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class ViewTermDetailsActivity : AppCompatActivity(), TermAdapter.OnItemClickListener  {

    private lateinit var recyclerViewTerms: RecyclerView
    private lateinit var termAdapter: TermAdapter

    private lateinit var deleteTermActivityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_term_details)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize recyclerViewTerms
        recyclerViewTerms = findViewById(R.id.recycler_view_terms)

        // Set the layout manager
        recyclerViewTerms.layoutManager = LinearLayoutManager(this)


        // Fetch terms from the database (Assuming you have a method to do this)
        val termsList = fetchTermsFromDatabase()

        // Log the contents of the termsList
        for (term in termsList) {
            Log.d("TermsList", "Term Name: ${term.termName}")
        }

        termAdapter = TermAdapter(termsList)
        recyclerViewTerms.adapter = termAdapter


        // Set click listener for the RecyclerView items
        termAdapter.setOnItemClickListener(this)

        // Initialize the ActivityResultLauncher
        deleteTermActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Term has been deleted, refresh term list
                refreshTermList()
            }
        }

        val buttonEnterDeleteTerm = findViewById<Button>(R.id.buttonEnterDeleteTerm)

        buttonEnterDeleteTerm.setOnClickListener {
            // Start DeleteTermActivity with request code
            val intent = Intent(this, DeleteTermActivity::class.java)
            deleteTermActivityResultLauncher.launch(intent)
        }
    }

    override fun onItemClick(term: Term) {
        // Handle item click here
        val intent = Intent(this, TermDetailsActivity::class.java).apply {
            putExtra("TERM_ID", term.termId)
        }
        startActivity(intent)
    }

    // Method to handle button click to navigate to entering term details activity
    fun onEnterTermDetailsClick(view: View?) {
        val intent: Intent = Intent(this, EnterTermDetailsActivity::class.java)
        startActivity(intent)
    }

    private fun fetchTermsFromDatabase(): List<Term> {
        val termsList = mutableListOf<Term>()

        val db = DatabaseHelper(this).readableDatabase

        val projection = arrayOf("term_id", "term_name", "start_date", "end_date")

        val cursor = db.query("Terms", projection, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val termId = getLong(getColumnIndexOrThrow("term_id"))
                val termNameFromCursor = getString(getColumnIndexOrThrow("term_name"))
                val startDate = getString(getColumnIndexOrThrow("start_date"))
                val endDate = getString(getColumnIndexOrThrow("end_date"))

                val (courses, termNameFromFunction) = fetchCoursesForTerm(db, termId)

                val term = Term(termId, termNameFromCursor ?: "", startDate, endDate, courses)
                termsList.add(term)
            }
        }


        cursor.close()
        db.close()

        return termsList
    }

    private fun fetchCoursesForTerm(db: SQLiteDatabase, termId: Long): Pair<List<Course>, String?> {
        val coursesList = mutableListOf<Course>()
        var termName: String? = null

        val projection = arrayOf(
            "course_id", "course_name", "start_date", "end_date",
            "instructor_name", "phone_number", "email_address",
            "progress_status", "notes"
        )
        val selection = "term_id = ?"
        val selectionArgs = arrayOf(termId.toString())

        val cursor = db.query("Courses", projection, selection, selectionArgs, null, null, null)

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

                val termName = fetchTermName(db, termId)

                val course = termName?.let {
                    Course(
                        courseId, courseName, startDate, endDate,
                        instructorName, phoneNumber, emailAddress,
                        progressStatus, notes, termId, it
                    )
                }

                course?.let {
                    coursesList.add(it)
                }
            }
        }
        return Pair(coursesList, termName)}


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

    private fun fetchTermName(db: SQLiteDatabase, termId: Long): String? {
        var termName: String? = null

        val projection = arrayOf("term_name")
        val selection = "term_id = ?"
        val selectionArgs = arrayOf(termId.toString())

        val cursor = db.query("Terms", projection, selection, selectionArgs, null, null, null)

        if (cursor.moveToFirst()) {
            termName = cursor.getString(cursor.getColumnIndexOrThrow("term_name"))
        }

        cursor.close()
        return termName
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when activity is resumed
        termAdapter.notifyDataSetChanged()
        // Refresh data when activity is resumed
        fetchAndDisplayTerms()
    }

    private fun fetchAndDisplayTerms() {
        val termsList = fetchTermsFromDatabase()
        termAdapter.updateTerms(termsList)
    }


    // Add this method to refresh term list
    private fun refreshTermList() {
        // Assuming termListRecyclerView is your RecyclerView variable
        recyclerViewTerms.adapter?.notifyDataSetChanged()
    }


}