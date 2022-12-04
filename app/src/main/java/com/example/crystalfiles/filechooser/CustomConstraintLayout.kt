package com.example.crystalfiles.filechooser

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global.Companion.fcActualPath
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import com.example.crystalfiles.view.leftpanel.NavDrawer
import java.io.File

class  CustomConstraintLayout constructor(context: Context, private val startFileChooser: StartFileChooser,private val dialog: AlertDialog
):
ConstraintLayout(context){
    lateinit var recyclerView: RecyclerView
    lateinit var actualEditText: EditText
    init {
        LayoutInflater.from(context).inflate(R.layout.file_chooser_dialog, this, true)
    }
    fun set(arrayAdapter: FileChooserAdapter, settings: String = "", settIngsView: View?){
        arrayAdapter.let {
            recyclerView = findViewById(R.id.rv_file_chooser_dialog)
            actualEditText = findViewById(R.id.et_file_chooser_dialog)
            val backImageView = findViewById<ImageView>(R.id.file_chooser_back)
            backImageView.setOnClickListener {
                val parent = File(fcActualPath).parent
                if (parent != null && File(parent).canRead()){
                    startFileChooser.fcListAll(parent)
                } else {
                    startFileChooser.fcDrives()
                    actualEditText.setText(context.getString(R.string.storage_))
                }
            }
            val btnCancelar: AppCompatButton = findViewById(R.id.file_chooser_negative_btn)
            btnCancelar.setOnClickListener{
                dialog.dismiss()
            }
            val btnAceptar: AppCompatButton = findViewById(R.id.file_chooser_positive_btn)
            btnAceptar.setOnClickListener {
                when(settings){
                    "background" -> {}
                    "default_window" -> {
                        if (settIngsView != null) (settIngsView as TextView).text = File(fcActualPath).absolutePath
                        prefs.saveRootLocation(fcActualPath)
                    }
                    else -> {
                        prefs.saveFavLocation(fcActualPath)
                        NavDrawer(context)
                    }
                }
                dialog.dismiss()
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = arrayAdapter
        }

    }
}

