package com.hallen.rfilemanager.ui.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.rfilemanager.databinding.StorageAnalyserFragmentBinding
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.model.LayoutManagerType
import com.hallen.rfilemanager.ui.view.adapters.analysis.SpaceAnalysisAdapter
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.custom.setLayoutManager
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.State
import com.hallen.rfilemanager.ui.viewmodels.StorageAnalyzerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StorageAnalyzerFragment : Fragment(), AdapterListener {
    private lateinit var binding: StorageAnalyserFragmentBinding
    private val baseViewModel: BaseViewModel by activityViewModels()

    private val viewModel: StorageAnalyzerViewModel by viewModels()

    @Inject
    lateinit var storage: Storages

    @Inject
    lateinit var spaceAnalysisAdapter: SpaceAnalysisAdapter

    @Inject
    lateinit var prefs: Prefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = StorageAnalyserFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclerView()
        viewModel.listFiles(storage.drives.value!!.first())
        onBackPressedHandler()
        binding.backBtn.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupObservers() {
        viewModel.back1.observe(viewLifecycleOwner) { binding.back1.text = it }
        viewModel.back2.observe(viewLifecycleOwner) { binding.back2.text = it }
        baseViewModel.colorScheme.observe(viewLifecycleOwner) {
            spaceAnalysisAdapter.setColorTheme(it)
            binding.back2.setTextColor(Color.parseColor(it.lightColor))
        }
        baseViewModel.itemsSize.observe(viewLifecycleOwner, spaceAnalysisAdapter::setItemSize)
    }

    private fun setupRecyclerView() {
        spaceAnalysisAdapter.setListeners(this)
        with(binding.reciclerViewLayout.recyclerView) {
            setOnScaleListener { scale: Int -> prefs.saveScala(scale) }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            setRecyclerLayoutMode()
            adapter = spaceAnalysisAdapter
            setOnScaleListener(baseViewModel::setScale)
        }

        lifecycleScope.launch {
            viewModel.files.collect { files ->
                spaceAnalysisAdapter.update(files)
            }
        }
    }

    private fun setRecyclerLayoutMode() {
        val layout = LayoutManagerType.LINEAR_LAYOUT_MANAGER
        val scale = baseViewModel.scale.value ?: 4
        binding.reciclerViewLayout.recyclerView.setLayoutManager(layout, scale)
    }

    override fun onClick(adapterPosition: Int) {
        val file = viewModel.files.value.getOrNull(adapterPosition) ?: return
        if (file.isDirectory) viewModel.listFiles(file)
    }

    override fun onLongClick(adapterPosition: Int): Boolean {
        baseViewModel.clearMediaSelection()
        setCheckableMode()
        return true
    }

    private fun setCheckableMode() {
        val stateList = baseViewModel.state.value?.toMutableList() ?: return
        stateList.remove(State.NORMAL)
        stateList.remove(State.COPING)
        stateList.add(State.SELECTION)
        baseViewModel.state.value = stateList
    }

    override fun onCheck(adapterPosition: Int) {
        val files = viewModel.files.value
        val file = files[adapterPosition]
        baseViewModel.setSelectedMediaFile(file)
    }

    private fun onBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.back()
        }
    }

}