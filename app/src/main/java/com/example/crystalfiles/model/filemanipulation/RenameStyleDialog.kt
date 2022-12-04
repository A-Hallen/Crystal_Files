package com.example.crystalfiles.model.filemanipulation

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R

class RenameStyleDialog(context: Context) {



    private val alertDialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
            .setCancelable(true)
            .create().apply {
                val backgroundDrawable =
                    ContextCompat.getDrawable(context, R.drawable.bg_dialog_view)
                window?.setBackgroundDrawable(backgroundDrawable)
            }
    }

    private val renameStyleDialog: RenameStyleClass by lazy { RenameStyleClass(context) }
    private val textViewText = renameStyleDialog.findViewById<EditText>(R.id.rename_style_et)
    private val passwordText = renameStyleDialog.findViewById<EditText>(R.id.password_edit_text)
    fun set(
        title: String? = null,
        negativeButtonListener: ButtonListener? = null,
        positiveButtonListener: ButtonListener? = null,
        textViewState: Boolean = false,
        editState: Boolean = false,
        message: String? = null,
        password: Boolean = false,
        renameText:String?
    ): RenameStyleDialog {
        renameStyleDialog.set(
            title,
            negativeButtonListener,
            positiveButtonListener,
            textViewState,
            editState,
            message,
            password,
            renameText
        )
        alertDialog.setView(renameStyleDialog)

        return this
    }

    fun show() {
        alertDialog.show()
    }

    fun dismiss() {
        alertDialog.dismiss()
    }
    fun getText():String{
        return textViewText.text.toString()
    }
    fun getPassword():String{
        return passwordText.text.toString()
    }

    // Here will be the custom view as private inner class
}

class RenameStyleClass @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.rename_style_dialog, this, true)

    }

    fun set(
        title: String? = null,
        negativeButtonListener: ButtonListener? = null,
        positiveButtonListener: ButtonListener? = null,
        textViewState: Boolean = false,
        editState: Boolean = false,
        message: String? = null,
        password: Boolean = false,
        renameText:String?
    ) {
        title?.let {
            val renameStyleDialog:TextView = findViewById(R.id.rename_style_dialog_tv)
            renameStyleDialog.text = it
        }
        editState.let {
            val editText = findViewById<EditText>(R.id.rename_style_et)
            if (!it) editText.visibility = View.INVISIBLE else editText.setText(renameText)
        }
        password.let {
            if (it){
                val passwordEditText = findViewById<EditText>(R.id.password_edit_text)
                passwordEditText.visibility = View.VISIBLE
            }
        }

        negativeButtonListener?.let {
            val negativeBttn: Button = findViewById(R.id.rename_style_negative_btn)
            negativeBttn.text = context.resources.getText(R.string.cancelar)
            negativeBttn.setOnClickListener { negativeButtonListener.invoke() }
        }
        positiveButtonListener?.let {
            val positiveBttn: Button = findViewById(R.id.rename_style_positive_btn)
            positiveBttn.text = context.resources.getText(R.string.aceptar)
            positiveBttn.setOnClickListener { positiveButtonListener.invoke() }
        }

    }
}