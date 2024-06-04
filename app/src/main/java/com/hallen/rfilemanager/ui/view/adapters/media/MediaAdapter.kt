package com.hallen.rfilemanager.ui.view.adapters.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.databinding.ItemMediaBinding
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.model.UpdateModel
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.view.adapters.main.MainDiff
import javax.inject.Inject

class MediaAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<MediaViewHolder>() {


    private var files: List<Archivo> = emptyList()
    private var listeners: AdapterListener? = null
    fun setListeners(listeners: AdapterListener?){
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

    fun update(updateModel: UpdateModel) {
        if (updateModel.reloadAll){
            files = updateModel.files
            notifyDataSetChanged()
            return
        }
        val diffUtil = MainDiff(files, updateModel.files)
        val calculateDiff = DiffUtil.calculateDiff(diffUtil)
        calculateDiff.dispatchUpdatesTo(this)
        files = updateModel.files
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

    fun bind(file: Archivo) {
        binding.galeryText.text = file.nameWithoutExtension
        binding.galeryCheck.isChecked = file.isChecked == true
        binding.galeryCheck.isVisible = file.isChecked != null
        imageController.setImage(binding.imageGalery, file)
    }
}