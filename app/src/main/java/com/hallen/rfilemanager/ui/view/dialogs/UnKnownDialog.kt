package com.hallen.rfilemanager.ui.view.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.DialogUnknownInflationBinding

class UnKnownDialog(private val context: Context) : ConstraintLayout(context), DialogBuilder {
    private var listener: UnKnowListener? = null
    private var alertDialog: AlertDialog? = null
    private val binding: DialogUnknownInflationBinding

    interface UnKnowListener : DialogListener {
        override fun onAccept(dialog: DialogBuilder) {}
        fun onItemSelected(mime: String)
    }

    init {
        val inflater = LayoutInflater.from(context)
        binding = DialogUnknownInflationBinding.inflate(inflater, this, true)
    }

    override fun setStyle(style: StyleDialog): DialogBuilder = this

    fun setDialogListener(listener: UnKnowListener): DialogBuilder {
        this.listener = listener
        return this
    }

    override fun setDialogListener(listener: DialogListener): DialogBuilder = this
    override fun build(): DialogBuilder {
        setupListView()
        setAlertDialog()
        return this
    }

    private fun setupListView() {
        val array: Array<String> = arrayOf("Texto", "Audio", "Video", "Imagen", "Otros")
        binding.unknownListView.adapter = ArrayAdapter(context, R.layout.simple_list_item, array)
        binding.unknownListView.setOnItemClickListener { parent, _, position, _ ->
            val mime = when (parent.getItemIdAtPosition(position).toInt()) {
                0 -> "text/*"
                1 -> "audio/*"
                2 -> "video/*"
                3 -> "image/*"
                else -> "*/*"
            }
            listener?.onItemSelected(mime)
            dismiss()
        }
    }

    override fun show() {
        alertDialog?.show()
    }

    override fun dismiss() {
        alertDialog?.dismiss()
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