package com.hallen.rfilemanager.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hallen.rfilemanager.databinding.FragmentMediaBinding
import com.hallen.rfilemanager.infraestructure.FilePlayer
import com.hallen.rfilemanager.model.UpdateModel
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.adapters.media.MediaAdapter
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.MediaViewModel
import com.hallen.rfilemanager.ui.viewmodels.State
import com.orhanobut.logger.Logger
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
            Logger.i("Observing mode changes from fragmet")
            Toast.makeText(requireContext(), "changes: ${model.name}", Toast.LENGTH_SHORT).show()
            model ?: return@observe
            mediaViewModel.loadFiles(model)
        }
        mediaViewModel.files.observe(viewLifecycleOwner) {
            adapter.update(mediaViewModel.files.value ?: return@observe)
        }
    }

    override fun onClick(adapterPosition: Int) {
        val updates = baseViewModel.update.value ?: return
        if (updates.files.size < adapterPosition || adapterPosition < 0) return
        val file = updates.files.getOrNull(adapterPosition) ?: return
        if (file.isDirectory) {
            baseViewModel.listFiles(updates.files[adapterPosition])
            return
        }
        filePlayer.play(file)
    }

    override fun onLongClick(adapterPosition: Int): Boolean {
        if (baseViewModel.state.value?.contains(State.NORMAL) != true) return false
        setCheckableMode()
        onCheck(adapterPosition)
        return true
    }

    override fun onCheck(adapterPosition: Int) {
        val updates = baseViewModel.update.value ?: return
        val file = updates.files.getOrNull(adapterPosition) ?: return
        updates.files[adapterPosition].isChecked = !(file.isChecked ?: false)
        updates.reloadAll = false
        baseViewModel.update.value = updates
    }

    private fun setCheckableMode() {
        val stateList = baseViewModel.state.value?.toMutableList() ?: return
        stateList.remove(State.NORMAL)
        stateList.remove(State.COPING)
        stateList.add(State.SELECTION)
        baseViewModel.state.value = stateList
        val updateModel = baseViewModel.update.value ?: return
        val files = updateModel.files.onEach { it.isChecked = false }
        baseViewModel.update.value = UpdateModel(files, true)
    }


}