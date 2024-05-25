package org.ibda.myguessgame

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import org.ibda.myguessgame.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var vm : HomeViewModel
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        vm = ViewModelProvider(this).get(HomeViewModel::class.java)

        this.binding.home = vm
        this.binding.lifecycleOwner = viewLifecycleOwner

        vm.destination.observe(this.viewLifecycleOwner, { newValue ->
            when(newValue) {
                "AddNewTask" -> {
                    val action = HomeFragmentDirections
                        .actionHomeFragmentToAddNewTaskFragment()
                    rootView.findNavController().navigate(action)
                    vm.destination.value = "" // Reset destination value
                }
                "" -> {
                    // Do nothing if destination is empty
                }
                else -> {
                    val action = HomeFragmentDirections
                        .actionHomeFragmentToBottomNavFragment(newValue)
                    rootView.findNavController().navigate(action)
                    vm.destination.value = "" // Reset destination value
                }
            }
        })

        return rootView
    }
}