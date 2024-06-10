package com.hallen.rfilemanager.ui.view.adapters.main

import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.ui.view.adapters.ThemeColor

class MainViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val llContainer: LinearLayout = itemView.findViewById(R.id.llContainer_grid)
    private val cbSelect: CheckBox = itemView.findViewById(R.id.cbSelect_grid)
    private val layoutBackground: ConstraintLayout =
        itemView.findViewById(R.id.layout_recicler_grid)

    private var listeners: AdapterListener? = null
    private var itemsSize: Float? = null
    private var imageController: ImageController? = null
    private var colorTheme: ThemeColor? = null

    fun setListener(listeners: AdapterListener?): MainViewHolder {
        this.listeners = listeners
        return this
    }

    fun setColorScheme(colorThemeColor: ThemeColor?): MainViewHolder {
        this.colorTheme = colorThemeColor
        return this
    }

    fun setItemsSize(size: Float?): MainViewHolder {
        itemsSize = size
        return this
    }

    fun setImageController(imageController: ImageController): MainViewHolder {
        this.imageController = imageController
        return this
    }


    init {
        itemView.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listeners?.onClick(adapterPosition)
        }

        itemView.setOnLongClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION)
                return@setOnLongClickListener listeners?.onLongClick(adapterPosition) ?: false
            true
        }

        llContainer.setOnClickListener {
            listeners?.onCheck(adapterPosition)
            cbSelect.toggle()
            setLayoutBackground()
        }
    }


    fun bind(file: Archivo) {

        //definitions
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading_grid)
        val titleImage: ShapeableImageView = itemView.findViewById(R.id.title_image_grid)
        val titleImage2: ShapeableImageView = itemView.findViewById(R.id.title_image_grid2)

        //usage
        setCheckBoxContainer(file)
        cbSelect.isChecked = file.isChecked ?: false
        setLayoutBackground(file)
        setParams(itemView)
        tvHeading.text = file.name

        titleImage2.isVisible = file.isDirectory

        if (file.isDirectory) {
            imageController?.setFolderImage(titleImage, titleImage2, colorTheme)
        } else imageController?.setImage(titleImage, file)
    }

    private fun setCheckBoxContainer(file: Archivo) {
        val visibility = if (file.isChecked != null) View.VISIBLE else View.INVISIBLE
        llContainer.visibility = visibility
    }

    private fun setLayoutBackground(file: Archivo) = setLayoutBackground(file.isChecked == true)
    private fun setLayoutBackground(isChecked: Boolean = true) {
        val layoutBackgroundColor = if (isChecked) R.color.glass else R.color.transparente
        layoutBackground.setBackgroundColor(ContextCompat.getColor(context, layoutBackgroundColor))
    }

    private fun setParams(itemView: View) {
        val listItemRelativeLayout: RelativeLayout =
            itemView.findViewById(R.id.list_item_relative_layout)
        val params = listItemRelativeLayout.layoutParams
        val n = (((itemsSize ?: 1f) * 200)).toInt()
        params.width = n; params.height = n
        listItemRelativeLayout.layoutParams = params
    }

}