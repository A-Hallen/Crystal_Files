package com.example.crystalfiles.model.list_files

import android.content.Context
import java.io.File

class ListDrives(private val context: Context) {

    /**
     * Returns all available external SD-Card roots in the system.
     *
     * @return paths to all available external SD-Card roots in the system.
     */
    fun getStorageDirectories(): Array<String?>{
        val results: MutableList<String> = ArrayList()
        val externalDirs: Array<File> = context.applicationContext.getExternalFilesDirs(null)
        for (file in externalDirs) {
            val path: String = file.path.split("/Android")[0]
            results.add(path)
        }
        return results.toTypedArray()
    }
}