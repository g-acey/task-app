package org.ibda.myguessgame

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import org.ibda.myguessgame.databinding.FragmentAddNewTaskBinding

class AddNewTaskFragment : Fragment() {
    private lateinit var vm : AddNewTaskViewModel
    private lateinit var binding : FragmentAddNewTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentAddNewTaskBinding.inflate(inflater, container, false)
        val rootView = binding.root

        vm = ViewModelProvider(this).get(AddNewTaskViewModel::class.java)

        this.binding.addNewTask = vm
        this.binding.lifecycleOwner = viewLifecycleOwner

        // Set up the AutoCompleteTextView for task category
        val autoCompleteTextView = binding.taskCategory

        // Define the list of categories
        val categories = listOf("Normal", "Urgent", "Important")

        // Create an ArrayAdapter with the category list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)

        // Set the adapter to the AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter)

        // Set an item click listener to handle selection
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = parent.getItemAtPosition(position) as String
            // You can perform any action here with the selected category if needed
        }

        // Set click listener for the Cancel button
        binding.cancelButton.setOnClickListener {
            // Navigate back when cancel button is clicked
            requireView().findNavController().navigateUp()
        }

        vm.destination.observe(this.viewLifecycleOwner, { newValue ->
            when (newValue) {
                "Home" -> {

                    val title = binding.editTextTitle.text.toString()
                    val description = binding.editTextDescription.text.toString()
                    val category = binding.taskCategory.text.toString()

                    // Call ViewModel function to add the new task
                    vm.addNewTask(title, description, category)

                    val action = AddNewTaskFragmentDirections
                        .actionAddNewTaskFragmentToHomeFragment()
                    rootView.findNavController().navigate(action)
                    vm.destination.value = "" // Reset destination value
                }
            }
        })

        return rootView
    }
}