package com.hallen.rfilemanager.ui.view.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.RenameStyleDialogBinding
import java.io.File

enum class StyleDialog {
    NEW_FOLDER, NEW_FILE, DELETE_FILE, RENAME, COMPRESS, DECOMPRESS
}

class StyleDialogBuilder(
    val title: String,
    val renameText: String,
    val editState: Boolean = false,
    val password: Boolean = false,
) {
}

interface DialogBuilder {
    fun setStyle(style: StyleDialog): DialogBuilder
    fun setDialogListener(listener: DialogListener): DialogBuilder
    fun build(): DialogBuilder
    fun show()
    fun dismiss()
    fun getText() = ""
    fun getPassword() = ""
    fun setPlaceholder(text: String): DialogBuilder = this
}

interface DialogListener {
    fun onCancel(dialog: DialogBuilder) = dialog.dismiss()
    fun onAccept(dialog: DialogBuilder) {}
}

class RenameDialog(context: Context) : ConstraintLayout(context), DialogBuilder {
    private var style: StyleDialog? = null
    private var dialogListener: DialogListener? = null
    private var alertDialog: AlertDialog? = null
    private val binding: RenameStyleDialogBinding =
        RenameStyleDialogBinding.inflate(LayoutInflater.from(context), this)

    private fun getDialogBuilder(): StyleDialogBuilder {
        return when (style) {
            StyleDialog.NEW_FILE -> {
                StyleDialogBuilder("Nuevo Archivo", "Archivo", editState = true)
            }

            StyleDialog.NEW_FOLDER -> {
                StyleDialogBuilder("Nueva Carpeta", "Carpeta", editState = true)
            }

            StyleDialog.RENAME -> {
                StyleDialogBuilder("Renombrar", "", editState = true)
            }

            StyleDialog.COMPRESS -> {
                StyleDialogBuilder("Comprimir", "", editState = true, password = true)
            }

            StyleDialog.DECOMPRESS -> {
                StyleDialogBuilder("Descomprimir", "", editState = true, password = true)
            }

            else -> StyleDialogBuilder("Nueva Carpeta", "Carpeta")
        }
    }

    private fun setVisibility(builder: StyleDialogBuilder) {
        binding.renameStyleEt.isVisible = builder.editState
        val text = placeholder.ifBlank { builder.renameText }
        binding.renameStyleEt.setText(text)
        binding.passwordEditText.isVisible = builder.password
    }

    private fun setText(builder: StyleDialogBuilder) {
        binding.renameStyleDialogTv.text = builder.title
        binding.renameStyleNegativeBtn.text = context.resources.getText(R.string.cancelar)
        binding.renameStylePositiveBtn.text = context.resources.getText(R.string.aceptar)
    }

    private var placeholder: String = ""
    override fun setPlaceholder(text: String): DialogBuilder {
        placeholder = text
        return this
    }

    private fun setListeners() {
        binding.renameStyleNegativeBtn.setOnClickListener { dialogListener?.onCancel(this) }
        binding.renameStylePositiveBtn.setOnClickListener { dialogListener?.onAccept(this) }
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

    override fun build(): RenameDialog {
        val builder = getDialogBuilder()
        setVisibility(builder)
        setText(builder)
        setListeners()
        setAlertDialog()
        return this
    }

    private fun showKeyboard(editText: EditText) {
        val text = getText()
        val file = File(text)
        editText.requestFocus()
        editText.setSelection(0, file.nameWithoutExtension.length)

        val window = alertDialog?.window ?: return
        binding.renameStyleEt.requestFocus()
        val insetsController = WindowCompat.getInsetsController(window, binding.renameStyleEt)
        insetsController.show(WindowInsetsCompat.Type.ime())

    }

    override fun show() {
        alertDialog?.show()
        if (style == StyleDialog.RENAME) {
            showKeyboard(binding.renameStyleEt)
        }
    }

    override fun dismiss() {
        alertDialog?.dismiss()
    }


    override fun setStyle(style: StyleDialog): DialogBuilder {
        this.style = style
        return this
    }

    override fun setDialogListener(listener: DialogListener): DialogBuilder {
        this.dialogListener = listener
        return this
    }

    fun onDismiss(function: () -> Unit) = alertDialog?.setOnDismissListener { function() }

    override fun getText(): String = binding.renameStyleEt.text.toString()
    override fun getPassword(): String = binding.passwordEditText.text.toString()
}