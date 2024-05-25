package org.ibda.myguessgame

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return rootView
    }
}