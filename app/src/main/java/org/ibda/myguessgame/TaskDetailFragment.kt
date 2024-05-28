package org.ibda.myguessgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import org.ibda.myguessgame.databinding.FragmentTaskDetailBinding

class TaskDetailFragment : Fragment() {

    private lateinit var vm : TaskDetailViewModel
    private lateinit var binding : FragmentTaskDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        vm = ViewModelProvider(this).get(TaskDetailViewModel::class.java)

        this.binding.taskDetail = vm
        this.binding.lifecycleOwner = viewLifecycleOwner

        val taskId = arguments?.getInt("taskId") ?: -1 // Default value if taskId is not found
        vm.taskDetail(taskId)

        // Observe the actionText LiveData to update the button's text
        vm.actionText.observe(viewLifecycleOwner, Observer { text ->
            if (text == "Details") {
                binding.btnStart.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE

                binding.btnStart.isEnabled = false
                binding.btnCancel.isEnabled = false
            }
            else {
                binding.btnStart.text = text
            }
        })

        vm.destination.observe(this.viewLifecycleOwner, { newValue ->
            when (newValue) {
                "Back" -> {

                    // Call ViewModel function to add the new task
                    vm.editStatus(taskId)

                    val actionTextValue = vm.actionText.value // Accessing the value of actionText
                    val titleDetailValue = vm.titleDetail.value // Accessing the value of titleDetail

                    val toastMessage = "$actionTextValue Task $titleDetailValue"
                    Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()

                    requireView().findNavController().popBackStack()

                }
            }
        })

        // Set click listener for the Cancel button
        binding.btnCancel.setOnClickListener {
            // Navigate back when cancel button is clicked
            requireView().findNavController().navigateUp()
        }

        return rootView
    }
}