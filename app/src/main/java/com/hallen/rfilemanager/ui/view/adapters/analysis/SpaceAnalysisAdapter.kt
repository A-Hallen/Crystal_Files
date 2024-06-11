package com.hallen.rfilemanager.ui.view.adapters.analysis

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.databinding.ListItemAnalisisBinding
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.ui.view.adapters.ThemeColor
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.viewmodels.AnalysisFile
import javax.inject.Inject

class SpaceAnalysisAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<AnalysisViewHolder>() {

    private var files: List<AnalysisFile> = emptyList()
    private var itemsSize: Float? = null
    private var colorTheme: ThemeColor? = null

    fun setItemSize(newSize: Float) {
        itemsSize = newSize
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val binding =
            ListItemAnalisisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnalysisViewHolder(binding)
            .setListener(listeners)
            .setItemsSize(itemsSize)
            .setColorScheme(colorTheme)
            .setImageController(imageController)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) =
        holder.bind(files[position])

    @SuppressLint("NotifyDataSetChanged")
    private fun reloadUi() {
        val tempFiles = files
        files = tempFiles
        notifyDataSetChanged()
    }

    fun update(
        newFiles: List<AnalysisFile>,
    ) {
        files = newFiles
        notifyDataSetChanged()
        /*       return
               if (files.isEmpty() && newFiles.isNotEmpty()) {
                   files = newFiles
                   notifyDataSetChanged()
                   return
               }
               val diffCallback = AnalysisDiff(files, newFiles)
               val diffResult = DiffUtil.calculateDiff(diffCallback)
               Logger.i("FILES: ${newFiles.size}, ${files.size}")
               files = newFiles
               diffResult.dispatchUpdatesTo(this)*/
    }

    inner class AnalysisDiff(
        private val oldList: List<AnalysisFile>,
        private val newList: List<AnalysisFile>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            val oldItem = oldList[oldPosition]
            val newItem = newList[newPosition]
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldFile = oldList[oldItemPosition]
            val newFile = newList[newItemPosition]

            return oldFile.name == newFile.name && oldFile.isChecked == newFile.isChecked && oldFile.percent == newFile.percent
        }
    }
}