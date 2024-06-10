package com.hallen.rfilemanager.ui.view.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.AnalisisDialogBinding
import com.hallen.rfilemanager.databinding.FileChooserListItemBinding
import com.hallen.rfilemanager.model.Storage

class StartAnalysisDialog(context: Context) :
    ConstraintLayout(context), DialogBuilder {
    private val binding = AnalisisDialogBinding.inflate(LayoutInflater.from(context), this, true)
    private var drives: List<Storage> = emptyList()
    private lateinit var alertDialog: AlertDialog
    private var dialogListener: DialogListener? = null

    private fun setAlertDialog() {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_dialog_view)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(true)
        val dialog = alertDialogBuilder.create()
        dialog.window?.setBackgroundDrawable(backgroundDrawable)
        alertDialog = dialog
        alertDialog.setView(this)
    }

    override fun setStyle(style: StyleDialog): DialogBuilder = this
    override fun setDialogListener(listener: DialogListener): StartAnalysisDialog {
        this.dialogListener = listener
        return this
    }

    fun setDrives(drives: List<Storage>): DialogBuilder {
        this.drives = drives
        return this
    }

    override fun getText(): String = selectedStorage?.absolutePath ?: ""
    private var selectedStorage: Storage? = null
    override fun build(): DialogBuilder {
        val driveNames = drives.map { it.description }
        val driveAdapter = DriveAdapter(context, driveNames) { position ->
            alertDialog.dismiss()
            selectedStorage = drives.getOrNull(position)
            dialogListener?.onAccept(this)
        }
        binding.analisisDialogLv.adapter = driveAdapter
        setAlertDialog()
        return this
    }

    override fun show() = alertDialog.show()
    override fun dismiss() = alertDialog.dismiss()

    inner class DriveAdapter(
        context: Context,
        private val array: List<String>,
        private val function: (pos: Int) -> Unit,
    ) : ArrayAdapter<String>(context, R.layout.file_chooser_list_item, array) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val binding = FileChooserListItemBinding.inflate(inflater, parent, false)
            binding.fileChooserTitleImage.setImageResource(R.drawable.sidebar_sdcard)
            binding.fileChooserTvHeading.text = array[position]
            binding.root.setOnClickListener { function(position) }
            return binding.root
        }

    }
}
