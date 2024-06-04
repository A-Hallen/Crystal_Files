package com.hallen.rfilemanager.ui.view.adapters.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hallen.rfilemanager.databinding.ItemMediaBinding
import com.hallen.rfilemanager.infraestructure.ImageFolder
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import javax.inject.Inject


class MediaDiff(
    private val oldList: List<ImageFolder>,
    private val newList: List<ImageFolder>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val oldItem = oldList[oldPosition]
        val newItem = newList[newPosition]
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFile = oldList[oldItemPosition]
        val newFile = newList[newItemPosition]

        return oldFile.drawable == newFile.drawable && oldFile.folderName == newFile.folderName
    }
}

class MediaAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<MediaViewHolder>() {


    private var files: List<ImageFolder> = emptyList()
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

    fun update(newFiles: List<ImageFolder>) {
        val diffUtil = MediaDiff(files, newFiles)
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

    fun bind(file: ImageFolder) {
        binding.galeryText.text = file.folderName
        binding.galeryCheck.isChecked = file.isChecked == true
        binding.galeryCheck.isVisible = file.isChecked != null
        Glide.with(binding.root.context).load(file.drawable).into(binding.imageGalery)
    }
}