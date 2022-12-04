package com.example.crystalfiles.model.filemanipulation

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R

class OneStyleAlertDialog(context: Context) {



    private val alertDialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
            .setCancelable(true)
            .create().apply {
                val backgroundDrawable =
                    ContextCompat.getDrawable(context, R.drawable.bg_dialog_view)
                window?.setBackgroundDrawable(backgroundDrawable)
            }
    }

    private val oneStyleDialogView: OneStyleDialogView by lazy { OneStyleDialogView(context) }

    fun set(
        title: String? = null,
        message: String? = null,
        negativeButtonText: String? = null,
        negativeButtonListener: ButtonListener? = null,
        positiveButtonText: String? = null,
        positiveButtonListener: ButtonListener? = null
    ): OneStyleAlertDialog {
        oneStyleDialogView.set(
            title,
            message,
            negativeButtonText,
            negativeButtonListener,
            positiveButtonText,
            positiveButtonListener
        )

        alertDialog.setView(oneStyleDialogView)

        return this
    }

    fun show() {
        alertDialog.show()
    }

    fun dismiss() {
        alertDialog.dismiss()
    }

    // Here will be the custom view as private inner class
}