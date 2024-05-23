package com.example.c196gt

import Course
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(private var courses: List<Course>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buttonCourse: Button? = itemView.findViewById(R.id.button_course)
        val textTermName: TextView? = itemView.findViewById(R.id.text_view_term_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = courses[position]

        // Check if buttonCourse and textTermName are not null
        holder.buttonCourse?.apply {
            text = currentCourse.courseName
            setOnClickListener {
                val intent = Intent(context, CourseDetailsActivity::class.java).apply {
                    putExtra("COURSE_ID", currentCourse.courseId)
                    putExtra("COURSE_NAME", currentCourse.courseName)
                    putExtra("START_DATE", currentCourse.startDate)
                    putExtra("END_DATE", currentCourse.endDate)
                    putExtra("INSTRUCTOR_NAME", currentCourse.instructorName)
                    putExtra("PHONE_NUMBER", currentCourse.phoneNumber)
                    putExtra("EMAIL_ADDRESS", currentCourse.emailAddress)
                    putExtra("PROGRESS_STATUS", currentCourse.progressStatus)
                    putExtra("NOTES", currentCourse.notes)
                    putExtra("TERM_NAME", currentCourse.termName)
                }
                context.startActivity(intent)
            }
        }

        holder.textTermName?.text = "Term: ${currentCourse.termName}"

        // Log the bound Course
        Log.d("AdapterBinding", "Bound Course: ${currentCourse.courseName}")
    }

    override fun getItemCount(): Int {
        Log.d("AdapterItemCount", "Item count: ${courses.size}")
        return courses.size
    }

    // Method to update the terms list
    fun updateCourses(courseList: List<Course>) {
        this.courses = courseList
        notifyDataSetChanged()
    }
}

