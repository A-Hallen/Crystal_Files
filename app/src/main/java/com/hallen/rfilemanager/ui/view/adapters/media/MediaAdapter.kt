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
import java.io.File
import javax.inject.Inject

class MediaAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<MediaViewHolder>() {


    private var files: List<MediaManipulation.MediaFile> = emptyList()
    private var listeners: AdapterListener? = null
    fun setListeners(listeners: AdapterListener?) {
        this.listeners = listeners
    }

    override fun getItemCount(): Int = files.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)
        return MediaViewHolder(binding, listeners, imageController)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) =
        holder.bind(files[position])

    fun update(newFiles: List<MediaManipulation.MediaFile>) {
        val diffUtil = MainDiff(files, newFiles)
        val calculateDiff = DiffUtil.calculateDiff(diffUtil)
        calculateDiff.dispatchUpdatesTo(this)
        files = newFiles
    }
}

class MediaViewHolder(
    private val binding: ItemMediaBinding,
    listeners: AdapterListener?,
    private val imageController: ImageController,
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listeners?.onClick(adapterPosition)
        }
        binding.root.setOnLongClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION)
                return@setOnLongClickListener listeners?.onLongClick(adapterPosition) ?: false
            true
        }
    }

    fun bind(file: MediaManipulation.MediaFile) {
        binding.galeryText.text = file.displayName
        binding.galeryCheck.isChecked = file.isChecked == true
        binding.galeryCheck.isVisible = file.isChecked != null
        val thumbnailFile = File(file.thumbnail)
        imageController.setImage(binding.imageGalery, thumbnailFile)
    }
}