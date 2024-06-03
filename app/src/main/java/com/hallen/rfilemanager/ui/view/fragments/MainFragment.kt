package com.hallen.rfilemanager.ui.view.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.databinding.FragmentMainBinding
import com.hallen.rfilemanager.infraestructure.DirectoryObserver
import com.hallen.rfilemanager.infraestructure.FilePlayer
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.model.LayoutManagerType
import com.hallen.rfilemanager.model.UpdateModel
import com.hallen.rfilemanager.ui.view.adapters.main.MainAdapter
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.State
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), MainAdapter.Listeners {
    private lateinit var binding: FragmentMainBinding
    private val baseViewModel: BaseViewModel by activityViewModels()

    @Inject
    lateinit var storages: Storages

    @Inject
    lateinit var mainAdapter: MainAdapter

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var filePlayer: FilePlayer


    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupStyle()
        setObserver()
    }

    private var directoryObserver: DirectoryObserver? = null
    private val directoryObserverListener =
        object : DirectoryObserver.AvailableSpaceChangeListener {
            override fun onAvailableSpaceChange(availableSpace: Long, path: String?) {
                Logger.i("STORAGE_UPDATE: path: $path, availableSpace: $availableSpace")
                baseViewModel.updateFiles(false)
            }
        }

    private fun setObserver() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        baseViewModel.actualPath.observe(viewLifecycleOwner) {
            val file = File(it)
            directoryObserver = DirectoryObserver(file)
            directoryObserver?.setListener(directoryObserverListener)
            directoryObserver?.startWatching()
        }
    }


    private fun setupStyle() {
        baseViewModel.backgroundBlurRatio.observe(viewLifecycleOwner) { ratio ->
            binding.realtimeBlurView.isVisible = ratio > 0
            binding.realtimeBlurView.setBlurRadius(ratio)
        }
    }

    private fun setupRecyclerView() {
        mainAdapter.setListeners(this)

        with(binding.reciclerViewLayout.recyclerView) {
            setOnScaleListener { scale: Int -> prefs.saveScala(scale) }
            setHasFixedSize(true)
            val state = prefs.getRecyclerState()
            mainAdapter.layoutMode = state
            setRecyclerLayoutMode(state)
            adapter = mainAdapter
        }

        baseViewModel.recyclerLayoutMode.observe(viewLifecycleOwner, this::setRecyclerLayoutMode)
        baseViewModel.update.observe(viewLifecycleOwner, ::updateFiles)
        baseViewModel.itemsSize.observe(viewLifecycleOwner, mainAdapter::setItemSize)
    }

    private fun RecyclerView.setLayoutManager(type: LayoutManagerType, scale: Int) {
        val firstCompletelyVisibleItemPosition =
            (layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
        layoutManager = if (type == LayoutManagerType.GRID_LAYOUT_MANAGER)
            GridLayoutManager(context, scale) else LinearLayoutManager(context)
        firstCompletelyVisibleItemPosition?.let { scrollToPosition(it) }
    }

    private fun setRecyclerLayoutMode(layoutMode: Boolean) {
        val layout =
            if (layoutMode) LayoutManagerType.LINEAR_LAYOUT_MANAGER else LayoutManagerType.GRID_LAYOUT_MANAGER
        val scale = baseViewModel.scale.value ?: 4
        binding.reciclerViewLayout.recyclerView.setLayoutManager(layout, scale)
        mainAdapter.setLinearMode(layoutMode)
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
        //if (baseViewModel.clipboard.value?.any() == true) return true
        setCheckableMode(true)
        onCheck(adapterPosition)
        return true
    }

    private fun updateFiles(updateModel: UpdateModel) = mainAdapter.addNew(updateModel)

    private fun setCheckableMode(isCheckable: Boolean) {
        val stateList = baseViewModel.state.value?.toMutableList() ?: return
        stateList.remove(State.NORMAL)
        stateList.remove(State.COPING)
        stateList.add(State.SELECTION)
        baseViewModel.state.value = stateList
        val updateModel = baseViewModel.update.value ?: return
        val check = if (isCheckable) false else null
        val files = updateModel.files.onEach { it.isChecked = check }
        baseViewModel.update.value = UpdateModel(files, true)
    }

    override fun onCheck(adapterPosition: Int) {
        val updates = baseViewModel.update.value ?: return
        val file = updates.files.getOrNull(adapterPosition) ?: return
        updates.files[adapterPosition].isChecked = !(file.isChecked ?: false)
        updates.reloadAll = false
        baseViewModel.update.value = updates
    }
}