package com.hallen.rfilemanager.ui.view.adapters.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.model.LayoutManagerType.GRID_LAYOUT_MANAGER
import com.hallen.rfilemanager.model.LayoutManagerType.LINEAR_LAYOUT_MANAGER
import com.hallen.rfilemanager.model.UpdateModel
import com.hallen.rfilemanager.ui.view.leftpanel.ThemeColor
import javax.inject.Inject

class MainAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<MainViewHolder>() {

    private var files: List<Archivo> = emptyList()
    var layoutMode = false
    private var itemsSize: Float? = null
    private var colorTheme: ThemeColor? = null


    /**
     * Set item size
     *
     * @param newSize nuevo tamanio
     */
    fun setItemSize(newSize: Float) {
        itemsSize = newSize
    }

    /**
     * Set linear mode
     *
     * @param isLinear if true set linear state to Linear before you have to set
     * layoutManager to LinearLayoutManager or GridLayoutManager otherwise
     */
    fun setLinearMode(isLinear: Boolean) {
        if (layoutMode != isLinear) {
            layoutMode = isLinear
            reloadUi()
        }
        layoutMode = isLinear
    }

    fun setColorTheme(theme: ThemeColor?) {
        if (this.colorTheme != theme) {
            if (this.colorTheme == null) {
                this.colorTheme = theme
                return
            }
            this.colorTheme = theme
            reloadUi()
        }
    }

    private var listeners: AdapterListener? = null

    fun setListeners(listeners: AdapterListener) {
        this.listeners = listeners
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutMode) LINEAR_LAYOUT_MANAGER.ordinal else GRID_LAYOUT_MANAGER.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val res = if (layoutMode) R.layout.list_item_linear else R.layout.list_item_grid
        val itemView = LayoutInflater.from(parent.context).inflate(res, parent, false)
        return MainViewHolder(itemView)
            .setListener(listeners)
            .setItemsSize(itemsSize)
            .setColorScheme(colorTheme)
            .setImageController(imageController)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) =
        holder.bind(files[position])

    @SuppressLint("NotifyDataSetChanged")
    private fun reloadUi() {
        val tempFiles = files
        files = tempFiles
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNew(updates: UpdateModel) {
        if (updates.reloadAll) {
            files = emptyList()
            notifyDataSetChanged()
            files = updates.files
            notifyDataSetChanged()
            return
        }
        update(updates.files)
    }

    private fun update(
        newFiles: List<Archivo>,
    ) {
        val diffCallback = MainDiff(files, newFiles)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        files = newFiles
        diffResult.dispatchUpdatesTo(this)
    }
}