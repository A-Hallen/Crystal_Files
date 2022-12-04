package com.example.crystalfiles.filechooser

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.Global.Companion.fcActualPath
import com.example.crystalfiles.model.Global.Companion.fcArrayList
import com.example.crystalfiles.model.list_files.LoadThumb
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import com.example.crystalfiles.view.recyclerview.FCdata
import java.io.File

class StartFileChooser(private val context: Context,
                       private var settings: String = "",
                       private var settingsView: TextView? = null){
    private lateinit var adapter: FileChooserAdapter
    private lateinit var dialog: FileChooserDialog

    private fun setListeners(){
        adapter.setOnItemClicKListener(object : FileChooserAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                try {
                    val path = fcArrayList[position].path
                    fcListAll(path.absolutePath)
                } catch (e: IndexOutOfBoundsException) {
                    return
                }
                //ReadStorage(context).readStorage(Global.newArrayList[position].path)
            }
        })
    }

    fun firstList() {
        fcArrayList.clear()
        fcActualPath = ""
        for ( n in drives){
            n ?: return
            val storageFile = File(n)
            fcArrayList.add(FCdata(true, storageFile.name, storageFile.absoluteFile, storage = true))
        }
        adapter = FileChooserAdapter()
        dialog = FileChooserDialog(context, this).apply {
            when(settings){
                "background" -> set(adapter, "background")
                "default_window" -> set(adapter, "default_window", settingsView)
                else -> set(adapter)
            }
        }
        dialog.show()
        setListeners()
    }
    fun fcDrives(){
        fcArrayList.clear()
        for ( n in drives){
            n ?: return
            val storageFile = File(n)
            fcArrayList.add(FCdata(true, storageFile.name, storageFile.absoluteFile, storage = true))
        }
        adapter = FileChooserAdapter()
        dialog.recyclerView.adapter = adapter
        fcActualPath = ""
        setListeners()
    }

    fun fcListAll(path_: String) {
        val recyclerView: RecyclerView = dialog.recyclerView
        val actualEditText: EditText = dialog.actualEditText

        val path = File(path_)
        if(path.isFile){
            if (settings == "background"){
                (settingsView as TextView).text = path.name
                if (GetMimeFile(path).getmime().split("/")[0] == "image"){
                    dialog.dismiss()
                    prefs.setBgLocation(path.absolutePath)
                    prefs.setDefaultBackground(false)
                }
            } else return
            return
        }


        fcArrayList.clear()
        val files = path.listFiles()
        if (files == null){
            Toast.makeText(context,"No tienes permiso para acceder a este directorio", Toast.LENGTH_SHORT).show()
            return
        }
        files.sortWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )

        for (pathFiles in files){
            if(pathFiles.isDirectory && !pathFiles.isHidden) fcArrayList.add(FCdata(true, pathFiles.name.toString(), pathFiles))
        }


        for (pathFolders in files){
            if(!pathFolders.isDirectory && !pathFolders.isHidden){
                    when {
                        GetMimeFile(pathFolders).getmime().split("/")[0] == "image" -> {
                            val nombre: String = pathFolders.name.toString()
                            val fCdata = FCdata(false, nombre, pathFolders, ContextCompat.getDrawable(context, R.drawable.file), true)
                            fcArrayList.add(fCdata)
                        }
                        GetMimeFile(pathFolders).getmime() == "application/vnd.android.package-archive" ->{
                            val nombre:String = pathFolders.name.toString()
                            val loadThumb = LoadThumb()
                            val drawable: Drawable = loadThumb.getApkIcon(context, pathFolders)
                            val fCdata = FCdata(false, nombre, pathFolders, drawable, false)
                            fcArrayList.add(fCdata)
                        }
                        GetMimeFile(pathFolders).getmime().split("/")[0] == "video" -> {
                            val nombre: String = pathFolders.name.toString()
                            val fCdata = FCdata(false, nombre, pathFolders, ContextCompat.getDrawable(context, R.drawable.file),true)
                            fcArrayList.add(fCdata)
                        }
                        else -> {
                            val icon: Drawable = GetMimeFile(pathFolders).getImageFromExtension(context)
                            val nombre: String = pathFolders.name.toString()
                            val fCdata = FCdata(false, nombre, pathFolders, icon, false)
                            fcArrayList.add(fCdata)
                        }
                    }
            }
        }

        adapter = FileChooserAdapter()
        recyclerView.adapter = adapter
        fcActualPath = path_
        actualEditText.setText(fcActualPath)
        setListeners()
    }
}