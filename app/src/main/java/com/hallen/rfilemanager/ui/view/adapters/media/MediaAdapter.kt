package com.hallen.rfilemanager.ui.view.adapters.media

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ItemMediaBinding
import com.hallen.rfilemanager.databinding.ItemMediaOtherBinding
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
    private var itemsSize: Float? = null
    fun setItemSize(newSize: Float) {
        itemsSize = newSize
    }

    fun setListeners(listeners: AdapterListener?) {
        this.listeners = listeners
    }

    override fun getItemCount(): Int = files.size

    fun getSelectionText(): Pair<String, String> =
        checkedItems.size.toString() to files.size.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (type == Mode.MEDIA_VIDEO || type == Mode.MEDIA_IMAGE) {
            val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)
            MultiMediaViewHolder(binding)
        } else {
            val binding = ItemMediaOtherBinding.inflate(layoutInflater, parent, false)
            OtherMediaViewHolder(binding)
        }
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

    abstract inner class MediaViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        abstract var galeryCheck: CheckBox
        abstract var imageGalery: ShapeableImageView
        abstract var galeryText: TextView
        abstract var root: ConstraintLayout
        abstract var checkBoxContainer: View

        fun init() {

            root.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                if (!isEditMode) {
                    listeners?.onClick(adapterPosition)
                    return@setOnClickListener
                }
                galeryCheck.toggle()
                if (galeryCheck.isChecked) {
                    checkedItems.add(adapterPosition)
                } else {
                    checkedItems.remove(adapterPosition)
                }
                notifyItemChanged(adapterPosition)
                listeners?.onCheck(adapterPosition)
            }

            checkBoxContainer.setOnClickListener {
                galeryCheck.toggle()
                if (galeryCheck.isChecked) {
                    checkedItems.add(adapterPosition)
                } else {
                    checkedItems.remove(adapterPosition)
                }
                notifyItemChanged(adapterPosition)
                listeners?.onCheck(adapterPosition)
            }

            root.setOnLongClickListener {
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
            setParams(root)
            val text = if (file.extension.equals("apk", ignoreCase = true)) {
                getApkName(file, root.context)
            } else file.displayName
            galeryText.text = text
            galeryCheck.isChecked = checkedItems.contains(adapterPosition)
            checkBoxContainer.isVisible = isEditMode
            val thumbnailFile = File(file.thumbnail)
            imageController.setImage(imageGalery, thumbnailFile)
        }


        // Obtener el nombre de la apk
        private fun getApkName(file: File, context: Context): CharSequence {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageArchiveInfo(file.absolutePath, 0)
            val appInfo = packageInfo?.applicationInfo
            val label = appInfo?.loadLabel(packageManager) ?: file.nameWithoutExtension
            return label
        }

        open fun setParams(itemView: View) {
            val listItemRelativeLayout: RelativeLayout =
                itemView.findViewById(R.id.list_item_relative_layout)
            val params = listItemRelativeLayout.layoutParams
            val n = (((itemsSize ?: 1f) * 200)).toInt()
            params.width = n; params.height = n
            listItemRelativeLayout.layoutParams = params
        }

    }

    inner class MultiMediaViewHolder(
        binding: ItemMediaBinding
    ) : MediaViewHolder(binding.root) {
        override var galeryCheck: CheckBox = binding.galeryCheck
        override var imageGalery: ShapeableImageView = binding.imageGalery
        override var galeryText: TextView = binding.galeryText
        override var root: ConstraintLayout = binding.root
        override var checkBoxContainer: View = galeryCheck

        init {
            super.init()
        }

        override fun setParams(itemView: View) {}
    }

    inner class OtherMediaViewHolder(
        binding: ItemMediaOtherBinding
    ) : MediaViewHolder(binding.root) {
        override var galeryCheck: CheckBox = binding.galeryCheck
        override var imageGalery: ShapeableImageView = binding.imageGalery
        override var galeryText: TextView = binding.galeryText
        override var root: ConstraintLayout = binding.root
        override var checkBoxContainer: View = binding.llContainerGrid

        init {
            super.init()
        }
    }

}