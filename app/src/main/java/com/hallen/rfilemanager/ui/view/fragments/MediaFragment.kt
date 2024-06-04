package com.hallen.rfilemanager.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hallen.rfilemanager.databinding.FragmentMediaBinding
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.adapters.media.MediaAdapter
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaFragment : Fragment(), AdapterListener {
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var binding: FragmentMediaBinding

    @Inject
    lateinit var adapter: MediaAdapter
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
        adapter.setListeners(this)
        binding.mediaRecyclerView.setHasFixedSize(true)
        binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        binding.mediaRecyclerView.adapter = adapter
    }

    private fun configureObservers() {
        baseViewModel.mode.observe(viewLifecycleOwner) {

        }
        baseViewModel.update.observe(viewLifecycleOwner){
            Logger.i("Observing changes from fragmet")
            adapter.update(it)
        }
    }

    override fun onClick(adapterPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(adapterPosition: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCheck(adapterPosition: Int) {
        TODO("Not yet implemented")
    }

}