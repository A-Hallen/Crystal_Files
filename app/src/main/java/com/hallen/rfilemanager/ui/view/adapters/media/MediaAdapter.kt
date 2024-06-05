package com.hallen.rfilemanager.ui.view.adapters.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.databinding.ItemMediaBinding
import com.hallen.rfilemanager.infraestructure.MediaManipulation
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.adapters.main.MainDiff
import com.hallen.rfilemanager.ui.viewmodels.Mode
import java.io.File
import javax.inject.Inject

class MediaAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {
    private lateinit var type: Mode
    private var files: List<MediaManipulation.MediaFile> = emptyList()
    private var listeners: AdapterListener? = null
    private val checkedItems = mutableSetOf<Int>()
    private var isEditMode = false // Estado para controlar el modo edición
    fun setListeners(listeners: AdapterListener?) {
        this.listeners = listeners
    }

    override fun getItemCount(): Int = files.size

    fun getSelectionText(): Pair<String, String> =
        checkedItems.size.toString() to files.size.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)
        return MediaViewHolder(binding, listeners, imageController)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) =
        holder.bind(files[position])

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun update(newFiles: List<MediaManipulation.MediaFile>, mode: Mode) {
        type = mode
        val diffUtil = MainDiff(files, newFiles)
        val calculateDiff = DiffUtil.calculateDiff(diffUtil)
        calculateDiff.dispatchUpdatesTo(this)
        files = newFiles
    }

    fun clearSelection() {
        checkedItems.clear()
        isEditMode = false
        notifyDataSetChanged()
    }

    fun selectAll(checked: Boolean) {
        files.forEachIndexed { index, _ ->
            if (checked) {
                checkedItems.add(index)
            } else {
                checkedItems.remove(index)
            }
        }
        notifyDataSetChanged()
    }

    inner class MediaViewHolder(
        private val binding: ItemMediaBinding,
        listeners: AdapterListener?,
        private val imageController: ImageController,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                if (!isEditMode) {
                    listeners?.onClick(adapterPosition)
                    return@setOnClickListener
                }
                binding.galeryCheck.toggle()
                if (binding.galeryCheck.isChecked) {
                    checkedItems.add(adapterPosition)
                } else {
                    checkedItems.remove(adapterPosition)
                }
                notifyItemChanged(adapterPosition)
                listeners?.onCheck(adapterPosition)
            }

            binding.root.setOnLongClickListener {
                if (isEditMode) return@setOnLongClickListener false
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnLongClickListener false
                isEditMode = true
                checkedItems.add(adapterPosition)
                listeners?.onLongClick(adapterPosition)
                listeners?.onCheck(adapterPosition)
                notifyDataSetChanged()
                true
            }
        }

        fun bind(file: MediaManipulation.MediaFile) {
            binding.galeryText.text = file.displayName
            binding.galeryCheck.isChecked = checkedItems.contains(adapterPosition)
            binding.galeryCheck.isVisible = isEditMode
            val thumbnailFile = File(file.thumbnail)
            imageController.setImage(binding.imageGalery, thumbnailFile)
        }
    }

}