package com.hallen.rfilemanager.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.rfilemanager.databinding.FragmentMainBinding
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.model.LayoutManagerType
import com.hallen.rfilemanager.ui.view.adapters.analysis.SpaceAnalysisAdapter
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.custom.setLayoutManager
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.StorageAnalyzerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StorageAnalyzerFragment : Fragment(), AdapterListener {
    private lateinit var binding: FragmentMainBinding
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
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclerView()
        viewModel.listFiles(storage.drives.value!!.first())
        onBackPressedHandler()
    }

    private fun setupObservers() {
        baseViewModel.colorScheme.observe(viewLifecycleOwner, spaceAnalysisAdapter::setColorTheme)
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
        return false
    }

    override fun onCheck(adapterPosition: Int) {
    }

    private fun onBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.back()
        }
    }

}