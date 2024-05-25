package org.ibda.myguessgame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TaskAdapter(private val tasks: List<TaskInfo>, private val action: String, private val fragment: Fragment) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.task_title)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val actionButton: Button = itemView.findViewById(R.id.action_button)

        init {
            // Set click listener for the button
            actionButton.setOnClickListener {
                val task = tasks[adapterPosition]
                val direction = when (fragment) {
                    is NormalFragment -> NormalFragmentDirections.actionNormalFragmentToTaskDetailFragment(task.task_id)
                    is UrgentFragment -> UrgentFragmentDirections.actionUrgentFragmentToTaskDetailFragment(task.task_id)
                    is ImportantFragment -> ImportantFragmentDirections.actionImportantFragmentToTaskDetailFragment(task.task_id)
                    else -> throw IllegalArgumentException("Unsupported fragment type")
                }
                // Use findNavController from the Fragment
                fragment.findNavController().navigate(direction)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitle.text = task.title
        holder.taskDescription.text = task.description
        holder.actionButton.text = action
    }

    override fun getItemCount() = tasks.size
}