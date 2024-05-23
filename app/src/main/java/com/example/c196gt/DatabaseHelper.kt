package com.example.c196gt

import Assignment
import Course
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TERMS_TABLE)
        db.execSQL(CREATE_COURSES_TABLE)
        db.execSQL(CREATE_ASSIGNMENTS_TABLE)
        db.execSQL(CREATE_NOTES_TABLE)

    }

    fun insertInitialData(db: SQLiteDatabase) {
        // Inserting initial data
        val term1Id = insertTerm(db, "Spring Term", "01-02-2024", "05-31-2024")
        val term2Id = insertTerm(db, "Fall Term", "09-01-2024", "12-31-2024")

// Insert courses for term 1
        val course1IdTerm1 = insertCourse(
            "Course 1 for Spring Term",
            "01-15-2024",
            "04-15-2024",
            "Instructor 1",
            "1234567890",
            "instructor1@example.com",
            "In progress",
            "Notes for Course 1 for Spring Term",
            term1Id
        )
        insertAssignment(db, "Assignment 1 for Course 1", "01-03-2024", "03-01-2024", course1IdTerm1, "Objective Assessment")
        insertNote(db, "Note 1 for Course 1", course1IdTerm1)

        val course2IdTerm1 = insertCourse(
            "Course 2 for Spring Term",
            "02-01-2024",
            "05-01-2024",
            "Instructor 2",
            "9876543210",
            "instructor2@example.com",
            "Completed",
            "Notes for Course 2 for Spring Term",
            term1Id
        )
        insertAssignment(db, "Assignment 1 for Course 2", "01-04-2024", "04-01-2024", course2IdTerm1, "Performance Assessment")
        insertNote(db, "Note 1 for Course 2", course2IdTerm1)

// Insert courses for term 2
        val course1IdTerm2 = insertCourse(
            "Course 1 for Fall Term",
            "09-15-2024",
            "12-15-2024",
            "Instructor 3",
            "5555555555",
            "instructor3@example.com",
            "Not started",
            "Notes for Course 1 for Fall Term",
            term2Id
        )
        insertAssignment(db, "Assignment 1 for Course 1", "01-01-2024", "10-01-2024", course1IdTerm2, "Objective Assessment")
        insertNote(db, "Note 1 for Course 1", course1IdTerm2)

        val course2IdTerm2 = insertCourse(
            "Course 2 for Fall Term",
            "10-01-2024",
            "12-31-2024",
            "Instructor 4",
            "6666666666",
            "instructor4@example.com",
            "In progress",
            "Notes for Course 2 for Fall Term",
            term2Id
        )
        insertAssignment(db, "Assignment 1 for Course 2", "01-01-2024", "11-01-2024", course2IdTerm2, "Performance Assessment")
        insertNote(db, "Note 1 for Course 2", course2IdTerm2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TERMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ASSIGNMENTS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "C196"
        private const val TABLE_TERMS = "Terms"
        private const val TABLE_COURSES = "Courses"
        private const val TABLE_ASSIGNMENTS = "Assignments"
        private const val COLUMN_TERM_ID = "term_id"
        private const val COLUMN_COURSE_ID = "course_id"
        private const val COLUMN_COURSE_NAME = "course_name"
        private const val COLUMN_ASSIGNMENT_ID = "assignment_id"
        private const val COLUMN_ASSIGNMENT_NAME = "assignment_name"

        private const val CREATE_TERMS_TABLE = "CREATE TABLE $TABLE_TERMS (" +
                "term_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "term_name TEXT," +
                "start_date TEXT," +
                "end_date TEXT" +
                ")"

        private const val CREATE_COURSES_TABLE = "CREATE TABLE $TABLE_COURSES (" +
                "course_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "course_name TEXT," +
                "start_date TEXT," +
                "end_date TEXT," +
                "instructor_name TEXT," +
                "phone_number TEXT," +
                "email_address TEXT," +
                "progress_status TEXT," +
                "notes TEXT," +
                "term_id INTEGER," +
                "term_name TEXT," +
                "FOREIGN KEY (term_id) REFERENCES $TABLE_TERMS(term_id)" +
                ")"

        private const val CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE $TABLE_ASSIGNMENTS (" +
                "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "assignment_name TEXT," +
                "start_date TEXT," +
                "due_date TEXT," +
                "course_id INTEGER," +
                "assessment_type TEXT," +
                "FOREIGN KEY (course_id) REFERENCES $TABLE_COURSES(course_id)" +
                ")"
        private const val CREATE_NOTES_TABLE = "CREATE TABLE Notes (" +
                "note_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "note_content TEXT," +
                "course_id INTEGER," +
                "FOREIGN KEY (course_id) REFERENCES Courses(course_id)" +
                ")"
    }

        private fun insertTerm(db: SQLiteDatabase, termName: String, startDate: String, endDate: String): Long {
        val values = ContentValues().apply {
            put("term_name", termName)
            put("start_date", startDate)
            put("end_date", endDate)
        }
        return db.insert("Terms", null, values)
    }



    fun insertCourse(
        courseName: String,
        startDate: String,
        endDate: String,
        instructorName: String,
        phoneNumber: String,
        emailAddress: String,
        progressStatus: String,
        notes: String,
        termId: Long
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("course_name", courseName)
            put("start_date", startDate)
            put("end_date", endDate)
            put("instructor_name", instructorName)
            put("phone_number", phoneNumber)
            put("email_address", emailAddress)
            put("progress_status", progressStatus)
            put("notes", notes)
            put("term_id", termId)
        }
        return db.insert("Courses", null, values)
    }


    private fun insertAssignment(db: SQLiteDatabase, assignmentName: String, startDate: String, dueDate: String?, courseId: Long, assessmentType: String) {
        val values = ContentValues().apply {
            put("assignment_name", assignmentName)
            put("start_date", startDate)
            put("due_date", dueDate)
            put("course_id", courseId)
            put("assessment_type", assessmentType) // Insert Assessment Type
        }
        db.insert("Assignments", null, values)
    }

    fun insertNote(db: SQLiteDatabase, noteContent: String, courseId: Long) {
        val values = ContentValues().apply {
            put("note_content", noteContent)
            put("course_id", courseId)
        }
        db.insert("Notes", null, values)
    }

    fun getAllTermNames(): List<String> {
        val termsList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT term_name FROM $TABLE_TERMS", null)
        cursor.use {
            while (it.moveToNext()) {
                val termName = it.getString(it.getColumnIndexOrThrow("term_name"))
                termsList.add(termName)
            }
        }
        db.close()
        return termsList
    }

    fun getAllCourseNames(): List<String> {
        val courseList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT course_name FROM $TABLE_COURSES", null)
        cursor.use {
            while (it.moveToNext()) {
                val courseName = it.getString(it.getColumnIndexOrThrow("course_name"))
                courseList.add(courseName)
            }
        }
        db.close()
        return courseList
    }


    fun getAllAssignments(): List<Assignment> {
        val assignmentsList = ArrayList<Assignment>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ASSIGNMENTS", null)
        cursor.use {
            while (it.moveToNext()) {
                val assignmentId = it.getLong(it.getColumnIndexOrThrow("assignment_id"))
                val assignmentName = it.getString(it.getColumnIndexOrThrow("assignment_name"))
                val startDate = it.getString((it.getColumnIndexOrThrow("start_date")))
                val dueDate = it.getString(it.getColumnIndexOrThrow("due_date"))
                val assessmentType = it.getString(it.getColumnIndexOrThrow("assessment_type"))
                val courseId = it.getLong(it.getColumnIndexOrThrow("course_id"))
                val assignment = Assignment(assignmentId, assignmentName, startDate, dueDate, assessmentType, courseId)
                assignmentsList.add(assignment)
            }
        }
        db.close()
        return assignmentsList
    }
    // Function to delete a term by its ID
    fun deleteTerm(termId: Long): Int {
        val db = this.writableDatabase
        // Check if the term has associated courses
        val hasCourses = db.rawQuery("SELECT COUNT(*) FROM $TABLE_COURSES WHERE term_id = ?", arrayOf(termId.toString()))
            .use { cursor ->
                cursor.moveToFirst()
                cursor.getInt(0) > 0
            }

        if (hasCourses) {
            // Term has associated courses, cannot be deleted
            db.close()
            return -1 // Indicate failure due to associated courses
        }

        // No associated courses, proceed with deletion
        val rowsDeleted = db.delete(TABLE_TERMS, "$COLUMN_TERM_ID = ?", arrayOf(termId.toString()))
        db.close()
        return rowsDeleted
    }

    fun getTermIdByName(termName: String): Long? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT term_id FROM $TABLE_TERMS WHERE term_name = ?", arrayOf(termName))
        var termId: Long? = null
        cursor.use {
            if (it.moveToFirst()) {
                termId = it.getLong(it.getColumnIndexOrThrow(COLUMN_TERM_ID))
            }
        }
        db.close()
        return termId
    }

    fun hasCourses(termId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_COURSES WHERE term_id = ?", arrayOf(termId.toString()))
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count > 0
    }

    // Function to delete a course by its ID
    fun deleteCourse(courseId: Long): Int {
        val db = this.writableDatabase

        // No associated courses, proceed with deletion
        val rowsDeleted = db.delete(TABLE_COURSES, "$COLUMN_COURSE_ID = ?", arrayOf(courseId.toString()))
        db.close()
        return rowsDeleted
    }

    fun getCourseIdByName(courseName: String): Long? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT course_id FROM $TABLE_COURSES WHERE course_name = ?", arrayOf(courseName))
        var courseId: Long? = null
        cursor.use {
            if (it.moveToFirst()) {
                courseId = it.getLong(it.getColumnIndexOrThrow(COLUMN_COURSE_ID))
            }
        }
        db.close()
        return courseId
    }

    fun getCourseNameById(courseId: Long): String? {
        val db = this.readableDatabase
        var courseName: String? = null

        val selection = "$COLUMN_COURSE_ID = ?"
        val selectionArgs = arrayOf(courseId.toString())
        val cursor = db.query(TABLE_COURSES, arrayOf(COLUMN_COURSE_NAME), selection, selectionArgs, null, null, null)

        val columnIndex = cursor.getColumnIndex(COLUMN_COURSE_NAME)
        if (columnIndex != -1) {
            courseName = cursor.getString(columnIndex)
        } else {
            Log.e("DatabaseHelper", "Column index not found for COLUMN_COURSE_NAME")
        }

        cursor?.close()
        db.close()
        return courseName
    }


    fun getAllCourses(): List<Course> {
        val coursesList = ArrayList<Course>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COURSES", null)

        cursor.use {
            while (it.moveToNext()) {
                // Retrieve values from the cursor
                val courseIdIndex = it.getColumnIndexOrThrow("course_id")
                val courseId = if (!it.isNull(courseIdIndex)) it.getLong(courseIdIndex) else null

                val courseNameIndex = it.getColumnIndexOrThrow("course_name")
                val courseName = it.getString(courseNameIndex)

                val startDateIndex = it.getColumnIndexOrThrow("start_date")
                val startDate = it.getString(startDateIndex)

                val endDateIndex = it.getColumnIndexOrThrow("end_date")
                val endDate = it.getString(endDateIndex)

                val instructorNameIndex = it.getColumnIndexOrThrow("instructor_name")
                val instructorName = if (!it.isNull(instructorNameIndex)) it.getString(instructorNameIndex) else null

                val phoneNumberIndex = it.getColumnIndexOrThrow("phone_number")
                val phoneNumber = if (!it.isNull(phoneNumberIndex)) it.getString(phoneNumberIndex) else null

                val emailAddressIndex = it.getColumnIndexOrThrow("email_address")
                val emailAddress = if (!it.isNull(emailAddressIndex)) it.getString(emailAddressIndex) else null

                val progressStatusIndex = it.getColumnIndexOrThrow("progress_status")
                val progressStatus = if (!it.isNull(progressStatusIndex)) it.getString(progressStatusIndex) else null

                val notesIndex = it.getColumnIndexOrThrow("notes")
                val notes = if (!it.isNull(notesIndex)) it.getString(notesIndex) else null

                val termIdIndex = it.getColumnIndexOrThrow("term_id")
                val termId = if (!it.isNull(termIdIndex)) it.getLong(termIdIndex) else null

                val termNameIndex = it.getColumnIndexOrThrow("term_name")
                val termName = if (!it.isNull(termNameIndex)) it.getString(termNameIndex) else null

                // Create Course object with retrieved data
                val course = Course(
                    courseId = courseId ?: -1,
                    courseName = courseName ?: "",
                    startDate = startDate ?: "",
                    endDate = endDate ?: "",
                    instructorName = instructorName ?: "",
                    phoneNumber = phoneNumber ?: "",
                    emailAddress = emailAddress ?: "",
                    progressStatus = progressStatus ?: "",
                    notes = notes ?: "",
                    termId = termId ?: -1,
                    termName = termName ?: ""
                )
                coursesList.add(course)
            }
        }
        db.close()
        return coursesList
    }

    fun updateCourse(
        courseId: Long,
        courseName: String,
        startDate: String,
        endDate: String,
        instructorName: String,
        phoneNumber: String,
        emailAddress: String,
        progressStatus: String,
        notes: String,
        termId: Long
    ): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("course_name", courseName)
            put("start_date", startDate)
            put("end_date", endDate)
            put("instructor_name", instructorName)
            put("phone_number", phoneNumber)
            put("email_address", emailAddress)
            put("progress_status", progressStatus)
            put("notes", notes)
            put("term_id", termId)
        }
        val rowsAffected = db.update(
            TABLE_COURSES,
            contentValues,
            "$COLUMN_COURSE_ID = ?",
            arrayOf(courseId.toString())
        )
        db.close()
        return rowsAffected
    }

    fun getCourseIdByDetails(courseName: String?, startDate: String?, endDate: String?, instructorName: String?, phoneNumber: String?, emailAddress: String?, progressStatus: String?, notes: String?, termName: String?): Long {
        Log.d("DatabaseHelper", "getCourseIdByDetails function called with parameters: courseName=$courseName, startDate=$startDate, endDate=$endDate, instructorName=$instructorName, phoneNumber=$phoneNumber, emailAddress=$emailAddress, progressStatus=$progressStatus, notes=$notes, termName=$termName")

        val db = this.readableDatabase
        var courseId: Long = -1

        try {
            val selection = "course_name = ? AND start_date = ? AND end_date = ? AND instructor_name = ? AND phone_number = ? AND email_address = ? AND progress_status = ? AND notes = ?"
            val selectionArgs = arrayOf(courseName, startDate, endDate, instructorName, phoneNumber, emailAddress, progressStatus, notes)
            val cursor = db.query(TABLE_COURSES, arrayOf(COLUMN_COURSE_ID), selection, selectionArgs, null, null, null)

            cursor.use { // Kotlin extension function to automatically close cursor
                if (it != null && it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(COLUMN_COURSE_ID)
                    if (columnIndex != -1) {
                        courseId = it.getLong(columnIndex)
                        Log.d("DatabaseHelper", "Successfully retrieved courseId from database: $courseId")
                    } else {
                        Log.e("DatabaseHelper", "Column index not found for COLUMN_COURSE_ID")
                    }
                } else {
                    // Cursor is empty, no data retrieved
                    Log.e("DatabaseHelper", "No data retrieved from database")
                }
            }
        } catch (e: SQLiteException) {
            Log.e("DatabaseHelper", "Error retrieving courseId from database: ${e.message}")
        } finally {
            db.close()
        }

        return courseId
    }

    fun updateAssignment(
        assignmentId: Long,
        assignmentName: String,
        endDate: String,
        assessmentType: String,
        courseId: Long
    ): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("assignment_name", assignmentName)
            put("due_date", endDate)
            put("assessment_type", assessmentType)
            put("course_id", courseId)
        }
        val rowsAffected = db.update(
            TABLE_ASSIGNMENTS,
            contentValues,
            "$COLUMN_ASSIGNMENT_ID = ?",
            arrayOf(assignmentId.toString())
        )
        db.close()
        return rowsAffected
    }

    fun getAllAssignmentNames(): MutableList<String> {
        val assignmentList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT assignment_name FROM $TABLE_ASSIGNMENTS", null)
        cursor.use {
            while (it.moveToNext()) {
                val assignmentName = it.getString(it.getColumnIndexOrThrow("assignment_name"))
                assignmentList.add(assignmentName)
            }
        }
        db.close()
        return assignmentList
    }


    fun getAssignmentIdByName(assignmentName: String): Long? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT assignment_id FROM $TABLE_ASSIGNMENTS WHERE assignment_name = ?", arrayOf(assignmentName))
        var assignmentId: Long? = null
        cursor.use {
            if (it.moveToFirst()) {
                assignmentId = it.getLong(it.getColumnIndexOrThrow(COLUMN_ASSIGNMENT_ID))
            }
        }
        db.close()
        return assignmentId
    }

    fun deleteAssignment(assignmentId: Long): Int {
        val db = this.writableDatabase

        // No associated courses, proceed with deletion
        val rowsDeleted = db.delete(TABLE_ASSIGNMENTS, "$COLUMN_ASSIGNMENT_ID = ?", arrayOf(assignmentId.toString()))
        db.close()
        return rowsDeleted
    }

    fun getAssignmentIdByDetails(
        assignmentName: String?,
        dueDate: String?,
        assessmentType: String?,
    ): Long {
        Log.d("DatabaseHelper", "getCourseIdByDetails function called with parameters: assignmentName=$assignmentName, dueDate=$dueDate, assessmentType=$assessmentType,")

        val db = this.readableDatabase
        var assignmentId: Long = -1

        try {
            val selection = "assignment_name = ? AND due_date = ? AND assessment_type = ?"
            val selectionArgs = arrayOf(assignmentName, dueDate, assessmentType)
            val cursor = db.query(TABLE_ASSIGNMENTS, arrayOf(COLUMN_ASSIGNMENT_ID), selection, selectionArgs, null, null, null)

            cursor.use { // Kotlin extension function to automatically close cursor
                if (it != null && it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(COLUMN_ASSIGNMENT_ID)
                    if (columnIndex != -1) {
                        assignmentId = it.getLong(columnIndex)
                        Log.d("DatabaseHelper", "Successfully retrieved courseId from database: $assignmentId")
                    } else {
                        Log.e("DatabaseHelper", "Column index not found for COLUMN_ASSESSMENT_ID")
                    }
                } else {
                    // Cursor is empty, no data retrieved
                    Log.e("DatabaseHelper", "No data retrieved from database")
                }
            }
        } catch (e: SQLiteException) {
            Log.e("DatabaseHelper", "Error retrieving courseId from database: ${e.message}")
        } finally {
            db.close()
        }
        return assignmentId
    }
    fun getCourseDatesByName(courseName: String): Pair<String?, String?> {
        val db = this.readableDatabase
        var startDate: String? = null
        var endDate: String? = null

        val cursor = db.rawQuery("SELECT start_date, end_date FROM courses WHERE course_name = ?", arrayOf(courseName))
        if (cursor != null && cursor.moveToFirst()) {
            val startDateIndex = cursor.getColumnIndex("start_date")
            val endDateIndex = cursor.getColumnIndex("end_date")

            if (startDateIndex != -1) {
                startDate = cursor.getString(startDateIndex)
            }

            if (endDateIndex != -1) {
                endDate = cursor.getString(endDateIndex)
            }
        }
        cursor?.close()
        return Pair(startDate, endDate)
    }
    fun getAssignmentDatesByName(assignmentName: String): Pair<String?, String?> {
        val db = this.readableDatabase
        var startDate: String? = null
        var endDate: String? = null

        val cursor = db.rawQuery("SELECT start_date, due_date FROM assignments WHERE assignment_name = ?", arrayOf(assignmentName))
        if (cursor != null && cursor.moveToFirst()) {
            val startDateIndex = cursor.getColumnIndex("start_date")
            val endDateIndex = cursor.getColumnIndex("due_date")

            if (startDateIndex != -1) {
                startDate = cursor.getString(startDateIndex)
            }

            if (endDateIndex != -1) {
                endDate = cursor.getString(endDateIndex)
            }
        }
        cursor?.close()
        return Pair(startDate, endDate)
    }



    fun getAssignmentByName(assignmentName: String): Assignment? {
        val db = readableDatabase
        var assignment: Assignment? = null

        val selection = "$COLUMN_ASSIGNMENT_NAME = ?"
        val selectionArgs = arrayOf(assignmentName)
        val cursor = db.query(TABLE_ASSIGNMENTS, null, selection, selectionArgs, null, null, null)

        cursor.use {
            if (it != null && it.moveToFirst()) {
                // Retrieve values from the cursor
                val assignmentId = it.getLong(it.getColumnIndexOrThrow("assignment_id"))
                val startDate = it.getString(it.getColumnIndexOrThrow("start_date"))
                val dueDate = it.getString(it.getColumnIndexOrThrow("due_date"))
                val courseId = it.getLong(it.getColumnIndexOrThrow("course_id"))
                val assessmentType = it.getString(it.getColumnIndexOrThrow("assessment_type"))

                // Create Assignment object
                assignment = Assignment(
                    assignmentId,
                    assignmentName,
                    startDate,
                    dueDate,
                    assessmentType,
                    courseId
                )
            }
        }

        db.close()
        return assignment
    }



}