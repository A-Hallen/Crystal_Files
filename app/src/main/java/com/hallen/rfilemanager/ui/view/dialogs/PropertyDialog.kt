package com.hallen.rfilemanager.ui.view.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.DialogPropiedadesInflationBinding

class PropertyDialog(context: Context) : ConstraintLayout(context) {
    private val binding: DialogPropiedadesInflationBinding
    private var alertDialog: AlertDialog? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = DialogPropiedadesInflationBinding.inflate(inflater, this, true)
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

    fun build(properties: List<String>): PropertyDialog {
        val adapter = ArrayAdapter(context, R.layout.simple_list_item, properties)
        binding.propiedadesListView.adapter = adapter
        setAlertDialog()
        return this
    }

    fun show() {
        alertDialog?.show()
    }

    fun dismiss() {
        alertDialog?.dismiss()
    }
}