package com.hallen.rfilemanager.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hallen.rfilemanager.databinding.FragmentSpaceAnalysisBinding
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpaceAnalysisFragment : Fragment() {
    private val viewModel: BaseViewModel by activityViewModels()
    private lateinit var binding: FragmentSpaceAnalysisBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpaceAnalysisBinding.inflate(inflater)
        return binding.root
    }

}