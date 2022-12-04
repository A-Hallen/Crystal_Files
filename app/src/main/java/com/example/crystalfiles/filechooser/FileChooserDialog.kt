package com.example.crystalfiles.filechooser

import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R

class FileChooserDialog(context: Context, startFileChooser: StartFileChooser) {
    private val alertDialog: AlertDialog by lazy {
        AlertDialog.Builder(context).setCancelable(true).
                create().apply {
            window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_transparent))
        }
    }
    private val onClickItem: CustomConstraintLayout by lazy { CustomConstraintLayout(
        context,
        startFileChooser,
        alertDialog
    ) }
    lateinit var recyclerView: RecyclerView
    lateinit var actualEditText: EditText

    fun set(arrayAdapter: FileChooserAdapter, settings: String = "", settIngsView: View? = null): FileChooserDialog {
        onClickItem.set(arrayAdapter, settings, settIngsView)
        recyclerView = onClickItem.recyclerView
        actualEditText = onClickItem.actualEditText
        alertDialog.setView(onClickItem)
        return this
    }
    fun show(){
        alertDialog.show()
    }
    fun dismiss(){
        alertDialog.dismiss()
    }
}


