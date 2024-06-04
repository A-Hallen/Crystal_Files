package com.hallen.rfilemanager.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hallen.rfilemanager.databinding.FragmentMediaBinding
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaFragment : Fragment() {
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var binding: FragmentMediaBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureObservers()
        configureRecyclerView()
    }

    private fun configureRecyclerView() {

    }

    private fun configureObservers() {
        baseViewModel.mode.observe(viewLifecycleOwner) {

        }
    }

}