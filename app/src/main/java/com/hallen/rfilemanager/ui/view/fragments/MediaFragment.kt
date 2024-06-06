package com.hallen.rfilemanager.ui.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.FragmentMediaBinding
import com.hallen.rfilemanager.infraestructure.FilePlayer
import com.hallen.rfilemanager.model.LayoutManagerType
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.adapters.media.MediaAdapter
import com.hallen.rfilemanager.ui.view.custom.setLayoutManager
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.MediaViewModel
import com.hallen.rfilemanager.ui.viewmodels.Mode.FILES
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_APPS
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_BOOKS
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_IMAGE
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_MUSIC
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_VIDEO
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
        onBackPressedHandler()
    }

    private fun configureRecyclerView() {
        adapter.setListeners(this)
        binding.mediaRecyclerView.setHasFixedSize(true)
        binding.mediaRecyclerView.adapter = adapter

    }


    private fun configureObservers() {
        baseViewModel.mode.observe(viewLifecycleOwner) { mode ->
            val spanCount = if (mode != MEDIA_VIDEO && mode != MEDIA_IMAGE) {
                baseViewModel.scale.value ?: 3
            } else 3

            val layout = LayoutManagerType.GRID_LAYOUT_MANAGER
            binding.mediaRecyclerView.setLayoutManager(layout, spanCount)

            val resources = mapOf(
                MEDIA_IMAGE to Pair(R.string.imagenes, R.drawable.icon_image),
                MEDIA_MUSIC to Pair(R.string.musica, R.drawable.icon_music),
                MEDIA_VIDEO to Pair(R.string.videos, R.drawable.icon_video),
                MEDIA_BOOKS to Pair(R.string.libros, R.drawable.sidebar_books),
                MEDIA_APPS to Pair(R.string.apps, R.drawable.sidebar_apps),
                FILES to Pair(null, null)
            )
            val (stringRes, imageRes) = resources[mode] ?: return@observe
            val text = stringRes?.let { getString(it) } ?: ""
            val image = imageRes?.let { ContextCompat.getDrawable(requireContext(), it) }
            binding.mediaTopBar.isVisible = mode != FILES
            binding.title.text = text
            binding.titleIcon.setImageDrawable(image)
            mediaViewModel.loadFiles(mode)
        }
        mediaViewModel.files.observe(viewLifecycleOwner) { files ->
            val mode = baseViewModel.mode.value ?: return@observe
            adapter.update(files ?: emptyList(), mode)
        }
        baseViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state?.contains(State.SELECTION) == false) adapter.clearSelection()
            binding.mediaSelectionBar.isVisible = state?.contains(State.SELECTION) == true
            binding.mediaTopBar.isVisible =
                state?.contains(State.NORMAL) == true || state?.contains(State.COPING) == true
        }
        binding.closeSelectAll.setOnClickListener {
            baseViewModel.state.value = listOf(State.NORMAL)
        }
        binding.cbSelectAll.setOnClickListener {
            adapter.selectAll(binding.cbSelectAll.isChecked)
            onCheck(0)
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
        baseViewModel.clearMediaSelection()
        setCheckableMode()
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onCheck(adapterPosition: Int) {
        val selectionText = adapter.getSelectionText()
        binding.cbSelectAll.isChecked = selectionText.first == selectionText.second
        binding.numberItems.text = selectionText.first + "/" + selectionText.second
        val file = mediaViewModel.files.value?.getOrNull(adapterPosition) ?: return
        Logger.i("selectedFile: $file")
        baseViewModel.setSelectedMediaFile(file)
    }

    private fun setCheckableMode() {
        val stateList = baseViewModel.state.value?.toMutableList() ?: return
        stateList.remove(State.NORMAL)
        stateList.remove(State.COPING)
        stateList.add(State.SELECTION)
        baseViewModel.state.value = stateList
    }

    private fun onBackPressedHandler() {
        binding.back.setOnClickListener {
            if (!mediaViewModel.onBackPressed()) {
                baseViewModel.mode.value = FILES
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!mediaViewModel.onBackPressed()) {
                isEnabled = false
                baseViewModel.mode.value = FILES
                requireActivity().onBackPressed()
            }
        }
    }

}