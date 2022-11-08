package com.example.crystalfiles.model.list_files

import android.os.Environment
import java.io.File

class ListFiles(private val path_name: String) {
    // retorna una lista de archivos con algunos atributos
    val path: File = File(path_name)

    fun check(path: File): Boolean{
        return path.exists() and path.canRead()
    }

    private fun listExternalStorage(){
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state){
            ////
        }
    }


    fun getListFolder(): Array<out File>? {
        return path.listFiles()
    }

    fun getListFiles(path:File){
        val list = path.listFiles()
    }
}