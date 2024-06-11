package com.hallen.rfilemanager.ui.view.adapters.analysis

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ListItemAnalisisBinding
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.ui.view.adapters.ThemeColor
import com.hallen.rfilemanager.ui.view.adapters.main.AdapterListener
import com.hallen.rfilemanager.ui.viewmodels.AnalysisFile
import java.text.DecimalFormat

class AnalysisViewHolder(private val binding: ListItemAnalisisBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val context = itemView.context
    private var isInEditMode = false

    private var listeners: AdapterListener? = null
    private var itemsSize: Float? = null
    private var imageController: ImageController? = null
    private var colorTheme: ThemeColor? = null

    fun setListener(listeners: AdapterListener?): AnalysisViewHolder {
        this.listeners = listeners
        return this
    }

    fun setColorScheme(colorThemeColor: ThemeColor?): AnalysisViewHolder {
        this.colorTheme = colorThemeColor
        return this
    }

    fun setItemsSize(size: Float?): AnalysisViewHolder {
        itemsSize = size
        return this
    }

    fun setImageController(imageController: ImageController): AnalysisViewHolder {
        this.imageController = imageController
        return this
    }


    init {
        itemView.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listeners?.onClick(adapterPosition)
            }
        }


        itemView.setOnLongClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                return@setOnLongClickListener listeners?.onLongClick(adapterPosition) ?: false
            }
            false
        }

        binding.llContainerGrid.setOnClickListener {
            listeners?.onCheck(adapterPosition)
            binding.cbSelectGrid.toggle()
            setLayoutBackground()
        }
    }


    fun bind(file: AnalysisFile) {
        //usage
        setCheckBoxContainer(file)
        binding.cbSelectGrid.isChecked = file.isChecked ?: false
        setLayoutBackground(file)
        setParams()

        binding.llContainerGrid.isVisible = file.isChecked != null
        binding.tvHeadingGrid.text = file.name
        binding.titleImageGrid2.isVisible = file.isDirectory
        binding.analisisProgressBar.progress = file.percent.toInt()
        val percentText = if (file.percent > 0) "${String.format("%.2f", file.percent)} %" else ""
        binding.analisisTextView.text = percentText
        binding.analisisProgressBar.progressTintList =
            ColorStateList.valueOf(Color.parseColor(colorTheme?.darkColor))
        binding.sizeAnalisisTextView.text = bytesToHuman(file.size)

        if (file.isDirectory) {
            imageController?.setFolderImage(
                binding.titleImageGrid,
                binding.titleImageGrid2,
                colorTheme
            )
        } else imageController?.setImage(binding.titleImageGrid, file)
    }

    private fun setCheckBoxContainer(file: Archivo) {
        val visibility = if (file.isChecked != null) View.VISIBLE else View.INVISIBLE
        binding.llContainerGrid.visibility = visibility
    }

    private fun setLayoutBackground(file: Archivo) = setLayoutBackground(file.isChecked == true)
    private fun setLayoutBackground(isChecked: Boolean = true) {
        val layoutBackgroundColor = if (isChecked) R.color.glass else R.color.transparente
        binding.layoutReciclerGrid.setBackgroundColor(
            ContextCompat.getColor(
                context,
                layoutBackgroundColor
            )
        )
    }

    private fun setParams() {
        val params = binding.listItemRelativeLayout.layoutParams
        val n = (((itemsSize ?: 1f) * 200)).toInt()
        params.width = n; params.height = n
        binding.listItemRelativeLayout.layoutParams = params
    }

    private fun bytesToHuman(size: Long): String {
        val Kb = (1 * 1024).toLong()
        val Mb = Kb * 1024
        val Gb = Mb * 1024
        val Tb = Gb * 1024
        val Pb = Tb * 1024
        val Eb = Pb * 1024
        if (size < Kb) return floatForm(size.toDouble()).toString() + " byte"
        if (size in Kb until Mb) return floatForm(size.toDouble() / Kb).toString() + " Kb"
        if (size in Mb until Gb) return floatForm(size.toDouble() / Mb).toString() + " Mb"
        if (size in Gb until Tb) return floatForm(size.toDouble() / Gb).toString() + " Gb"
        if (size in Tb until Pb) return floatForm(size.toDouble() / Tb).toString() + " Tb"
        if (size in Pb until Eb) return floatForm(size.toDouble() / Pb).toString() + " Pb"
        return if (size >= Eb) floatForm(size.toDouble() / Eb).toString() + " Eb" else "???"
    }

    private fun floatForm(d: Double): String? = DecimalFormat("#.##").format(d)

}