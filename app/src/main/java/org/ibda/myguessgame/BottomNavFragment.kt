package org.ibda.myguessgame

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import org.ibda.myguessgame.databinding.FragmentBottomNavBinding

class BottomNavFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var vm : BottomNavViewModel
    private lateinit var binding : FragmentBottomNavBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentBottomNavBinding.inflate(inflater, container, false)
        val rootView = binding.root

        vm = ViewModelProvider(this).get(BottomNavViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val destination = BottomNavFragmentArgs.fromBundle(requireArguments()).destination
        this.vm.destination.value = destination

        sharedViewModel.setDestination(destination)

        this.binding.botnav = vm
        this.binding.lifecycleOwner = viewLifecycleOwner

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navBottom.setupWithNavController(navController)

        return rootView
    }


}