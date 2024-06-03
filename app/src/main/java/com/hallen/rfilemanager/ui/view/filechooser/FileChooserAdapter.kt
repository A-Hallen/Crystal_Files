package com.hallen.rfilemanager.ui.view.filechooser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.FileChooserListItemBinding
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.ui.view.adapters.main.MainDiff
import javax.inject.Inject


typealias fileHolder = FileChooserViewHolder

class FileChooserAdapter @Inject constructor() : RecyclerView.Adapter<fileHolder>() {

    @Inject
    lateinit var imageController: ImageController

    private var files: List<Archivo> = emptyList()

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    private var onClickListener: OnClickListener? = null
    fun setOnItemClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    override fun getItemCount(): Int = files.size
    override fun onBindViewHolder(holder: fileHolder, position: Int) = holder.bind(files[position])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fileHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileChooserListItemBinding.inflate(inflater)
        val windowManager = parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = windowManager.defaultDisplay.width
        val wrapContent = RecyclerView.LayoutParams.WRAP_CONTENT
        binding.root.layoutParams = RecyclerView.LayoutParams(width, wrapContent)
        return FileChooserViewHolder(binding, onClickListener, imageController)
    }

    fun update(newFiles: List<Archivo>) {
        val mainDiff = MainDiff(files, newFiles)
        val calculateDiff = DiffUtil.calculateDiff(mainDiff)
        files = newFiles
        calculateDiff.dispatchUpdatesTo(this)
    }
}


class FileChooserViewHolder(
    private val binding: FileChooserListItemBinding,
    listener: FileChooserAdapter.OnClickListener?,
    private val imageController: ImageController,
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener { listener?.onItemClick(adapterPosition) }
    }

    fun bind(file: Archivo) {
        binding.fileChooserTvHeading.text = file.name
        if (!file.isDirectory) {
            imageController.setImage(binding.fileChooserTitleImage, file)
        } else {
            if (file.parentFile?.canRead() != true) {
                binding.fileChooserTitleImage.setImageResource(R.drawable.sidebar_sdcard)
            } else {
                binding.fileChooserTitleImage.setImageResource(R.drawable.folder)
            }
        }
    }

}
