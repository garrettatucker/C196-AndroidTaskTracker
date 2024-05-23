package com.example.c196gt

import Term
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class TermAdapter(private var terms: List<Term>) :
    RecyclerView.Adapter<TermAdapter.TermViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(term: Term)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    inner class TermViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buttonTerm: Button = itemView.findViewById(R.id.button_term)

        init {
            buttonTerm.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(terms[position])
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_term, parent, false)
        return TermViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        val currentTerm = terms[position]
        holder.buttonTerm.text = currentTerm.termName

        // Set click listener for the button
        holder.buttonTerm.setOnClickListener {

            // Launch TermDetailsActivity with term details
            val intent = Intent(holder.itemView.context, TermDetailsActivity::class.java).apply {
                putExtra("TERM_NAME", currentTerm.termName)
                putExtra("START_DATE", currentTerm.startDate)
                putExtra("END_DATE", currentTerm.endDate)
                val courseNamesList = currentTerm.courseNames.map { it.courseName }
                putStringArrayListExtra("COURSE_NAMES", ArrayList(courseNamesList))
            }
            holder.itemView.context.startActivity(intent)
        }


        // Log the bound term
        Log.d("AdapterBinding", "Bound Term: ${currentTerm.termName}")
    }

    override fun getItemCount(): Int {
        Log.d("AdapterItemCount", "Item count: ${terms.size}")
        return terms.size
    }

    // Method to update the terms list
    fun updateTerms(termsList: List<Term>) {
        this.terms = termsList
        notifyDataSetChanged()
    }

}