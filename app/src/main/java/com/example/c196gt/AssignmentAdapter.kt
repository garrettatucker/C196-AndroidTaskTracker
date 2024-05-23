package com.example.c196gt

import Assignment
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AssignmentAdapter(private var assignments: List<Assignment>) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val assignmentButton: Button = itemView.findViewById(R.id.assignmentButton)

        init {
            // Set up click listener for the button
            assignmentButton.setOnClickListener {
                val assignment = assignments[adapterPosition] // Get the clicked assignment
                val context = itemView.context
                val intent = Intent(context, AssignmentsDetailsActivity::class.java).apply {
                    // Pass assignment data to the details activity
                    putExtra("ASSIGNMENT_NAME", assignment.assignmentName)
                    putExtra("START_DATE", assignment.startDate)
                    putExtra("DUE_DATE", assignment.dueDate)
                    putExtra("ASSESSMENT_TYPE", assignment.assessmentType)
                    putExtra("COURSE_ID", assignment.courseId)
                }
                context.startActivity(intent) // Start the details activity
            }
        }

        fun bind(assignment: Assignment) {
            assignmentButton.text = assignment.assignmentName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]
        holder.bind(assignment)
    }

    override fun getItemCount(): Int {
        return assignments.size
    }

    fun updateData(newAssignmentsList: List<Assignment>) {
        assignments = newAssignmentsList
        notifyDataSetChanged()
    }
}
