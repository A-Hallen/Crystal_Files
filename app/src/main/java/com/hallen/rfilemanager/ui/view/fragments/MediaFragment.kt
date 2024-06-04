package com.hallen.rfilemanager.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hallen.rfilemanager.databinding.FragmentMediaBinding
import com.hallen.rfilemanager.infraestructure.FilePlayer
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.adapters.media.MediaAdapter
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.MediaViewModel
import com.hallen.rfilemanager.ui.viewmodels.State
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaFragment : Fragment(), AdapterListener {
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val mediaViewModel: MediaViewModel by viewModels()
    private lateinit var binding: FragmentMediaBinding

    @Inject
    lateinit var adapter: MediaAdapter

    @Inject
    lateinit var filePlayer: FilePlayer
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
        onBackPressedHandler()
    }

    private fun configureRecyclerView() {
        adapter.setListeners(this)
        binding.mediaRecyclerView.setHasFixedSize(true)
        binding.mediaRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        binding.mediaRecyclerView.adapter = adapter
    }


    private fun configureObservers() {
        baseViewModel.mode.observe(viewLifecycleOwner) { model ->
            model ?: return@observe
            mediaViewModel.loadFiles(model)
        }
        mediaViewModel.files.observe(viewLifecycleOwner) {
            val files = mediaViewModel.files.value ?: return@observe
            val mode = baseViewModel.mode.value ?: return@observe
            adapter.update(files, mode)
        }
    }

    override fun onClick(adapterPosition: Int) {
        val mediaFile = mediaViewModel.files.value?.getOrNull(adapterPosition) ?: return
        if (mediaFile.isDirectory) {
            mediaViewModel.loadImages(mediaFile)
            return
        }
        filePlayer.play(mediaFile)
    }

    override fun onLongClick(adapterPosition: Int): Boolean {
        val newFiles = mediaViewModel.files.value ?: return false
        newFiles.onEachIndexed { index, mediaFile ->
            mediaFile.isChecked = index == adapterPosition
        }
        mediaViewModel.files.value = newFiles
        setCheckableMode()
        return true
    }

    override fun onCheck(adapterPosition: Int) {
        val files = mediaViewModel.files.value ?: return
        val arrayList = ArrayList(files)
        arrayList[adapterPosition].isChecked = arrayList[adapterPosition].isChecked != true
        mediaViewModel.files.value = arrayList
    }

    private fun setCheckableMode() {
        val stateList = baseViewModel.state.value?.toMutableList() ?: return
        stateList.remove(State.NORMAL)
        stateList.remove(State.COPING)
        stateList.add(State.SELECTION)
        baseViewModel.state.value = stateList
    }

    private fun onBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!mediaViewModel.onBackPressed()) {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

}