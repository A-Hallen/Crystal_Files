package com.example.crystalfiles.model.list_files

import android.content.Context
import android.os.Environment
import com.example.crystalfiles.model.prefs.SharedPrefs
import java.io.File

class ListFiles(private val context: Context, private val path_name: String) {
    // retorna una lista de archivos con algunos atributos
    val path: File = File(path_name)

    fun getPath(): String {
        val favLocation = SharedPrefs.prefs.getRootLocation()
        val lastLocation = SharedPrefs.prefs.getRootLocation()
        return if (favLocation != ""){
            favLocation
        } else if (lastLocation != ""){
            lastLocation
        } else {
            context.getExternalFilesDir(null)!!.absolutePath
        }
    }
}