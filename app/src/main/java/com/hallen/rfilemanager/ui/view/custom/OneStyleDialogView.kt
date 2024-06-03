package com.hallen.rfilemanager.ui.view.custom

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.CompoundOneStyleDialogBinding
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.dialogs.StyleDialog

class ConditionalDialog constructor(
    context: Context,
) : ConstraintLayout(context), DialogBuilder {

    private val binding: CompoundOneStyleDialogBinding
    private var alertDialog: AlertDialog? = null
    private var style: StyleDialog? = null
    private var dialogListener: DialogListener? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = CompoundOneStyleDialogBinding.inflate(inflater, this)
    }

    override fun setStyle(style: StyleDialog): DialogBuilder {
        this.style = style
        val text = when (style) {
            StyleDialog.DELETE_FILE -> "Eliminar"
            else -> null
        }
        binding.oneStyleTitleMessageTv.text = text
        return this
    }

    override fun setDialogListener(listener: DialogListener): DialogBuilder {
        this.dialogListener = listener
        return this
    }

    override fun build(): DialogBuilder {
        binding.oneStyleNegativeBtn.setOnClickListener { dialogListener?.onCancel(this) }
        binding.oneStylePositiveBtn.setOnClickListener { dialogListener?.onAccept(this) }
        setAlertDialog()
        return this
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

    override fun show() {
        alertDialog?.show()
    }

    override fun dismiss() {
        alertDialog?.dismiss()
    }

    fun setText(message: String): DialogBuilder {
        binding.oneStyleTitleMessageTv.text = message
        return this
    }
}

private val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()