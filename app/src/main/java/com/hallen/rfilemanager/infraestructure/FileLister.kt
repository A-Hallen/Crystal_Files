package com.hallen.rfilemanager.infraestructure

import java.io.File
import javax.inject.Inject

class FileLister @Inject constructor() {
    private var showHiddenFiles: Boolean = false

    fun setHiddenFiles(showHiddenFiles: Boolean) {
        this.showHiddenFiles = showHiddenFiles
    }

    suspend fun listFile(file: File): List<File>? {
        if (!file.canRead() || !file.exists()) return null

        val files = if (showHiddenFiles) {
            file.listFiles() ?: return null
        } else {
            file.listFiles { file1, name -> !file1.isHidden && !name.startsWith(".") }
        }
        files.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })

        val directories = files.filter { it.isDirectory }
        val onlyFiles = files.filter { !it.isDirectory }
        return directories.plus(onlyFiles)
    }
}