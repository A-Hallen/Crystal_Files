package com.hallen.rfilemanager.ui.view.custom.colorchooser

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ColorChooserDialogBinding
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.dialogs.StyleDialog

class ColorChooserAlertDialog(context: Context) : ConstraintLayout(context), DialogBuilder {
    private var listener: DialogListener? = null
    private var color: String? = null
    private var alertDialog: AlertDialog? = null
    private val binding: ColorChooserDialogBinding

    init {
        binding = ColorChooserDialogBinding.inflate(LayoutInflater.from(context), this)
    }

    override fun setStyle(style: StyleDialog): DialogBuilder = this
    override fun setDialogListener(listener: DialogListener): DialogBuilder {
        this.listener = listener
        return this
    }

    private fun setListeners() {
        binding.renameStyleNegativeBtn.setOnClickListener { listener?.onCancel(this) }
        binding.renameStylePositiveBtn.setOnClickListener { listener?.onAccept(this) }
    }

    fun setColor(color: String?): ColorChooserAlertDialog {
        this.color = color
        return this
    }

    override fun build(): DialogBuilder {
        binding.colorChooser.setColor(color)
        setListeners()
        setAlertDialog()
        return this
    }

    override fun getText(): String = binding.colorChooser.getColor()

    override fun dismiss() {
        alertDialog?.dismiss()
    }

    override fun show() {
        alertDialog?.show()
    }

    private fun setAlertDialog() {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_dialog_view)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(true)
        val dialog = alertDialogBuilder.create()
        dialog.window?.setBackgroundDrawable(backgroundDrawable)
        alertDialog = dialog
        alertDialog?.setView(this)
    }
}