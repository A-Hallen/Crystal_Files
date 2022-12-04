package com.example.crystalfiles.view.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global
import com.example.crystalfiles.model.list_files.fileFun
import java.io.File

class DialogUnknown @JvmOverloads constructor(
    context: Context,
    private val dialog:AlertDialog,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val newRecyclerView: RecyclerView = (context as Activity).findViewById(R.id.recyclerView)

    init {
       LayoutInflater.from(context).inflate(R.layout.dialog_unknown_inflation, this, true)

    }

    fun set(file:File) {
        file.let{
            val listView = findViewById<ListView>(R.id.unknown_listView)
            val array:Array<String> = arrayOf("Texto", "Audio", "Video", "Imagen", "Otros")
            listView.adapter = ArrayAdapter(context, R.layout.simple_list_item, array)
            listView.setOnItemClickListener { parent, _, position, _ ->
                when(parent.getItemIdAtPosition(position).toInt()){
                    0 -> {
                        fileFun(file, context, false, "text/*")
                    }
                    1 -> {
                        fileFun(file, context, false, "audio/*")
                    }
                    2 -> {
                        fileFun(file, context, false, "video/*")
                    }
                    3 -> {
                        fileFun(file, context, false, "image/*")
                    }
                    4 -> {
                        fileFun(file, context, false, "*/*")
                    }
                }
                dialog.dismiss()

            }

        }
    }
}

class AlertDialogPropiedades(context: Context, private val file: File, private val array: Array<String>) {
    private val alertPropiedades: AlertDialog by lazy {
        AlertDialog.Builder(context).setCancelable(true).create().apply {
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.dialog_transparent)
            window?.setBackgroundDrawable(backgroundDrawable)
        }
    }
    private val dialogPropiedades: DialogPropiedades by lazy { DialogPropiedades(context) }

    fun set(): AlertDialogPropiedades {
        dialogPropiedades.set(file, array)
        alertPropiedades.setView(dialogPropiedades)
        return this@AlertDialogPropiedades
    }
    fun show(){
        alertPropiedades.show()
    }
}
class DialogPropiedades @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
       LayoutInflater.from(context).inflate(R.layout.dialog_propiedades_inflation, this, true)
    }

    fun set(file:File, array: Array<String>) {
        file.let{
            val listView = findViewById<ListView>(R.id.propiedades_listView)
            listView.adapter = ArrayAdapter(context, R.layout.simple_list_item, array)

        }
    }
}
