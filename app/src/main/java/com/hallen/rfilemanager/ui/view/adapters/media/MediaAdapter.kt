package com.hallen.rfilemanager.ui.view.adapters.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.databinding.ItemMediaBinding
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import javax.inject.Inject

class MediaAdapter @Inject constructor(private var imageController: ImageController) :
    RecyclerView.Adapter<MediaViewHolder>() {


    private var files: List<Archivo> = emptyList()
    private var listeners: AdapterListener? = null
    override fun getItemCount(): Int = files.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)
        return MediaViewHolder(binding, listeners, imageController)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) =
        holder.bind(files[position])
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