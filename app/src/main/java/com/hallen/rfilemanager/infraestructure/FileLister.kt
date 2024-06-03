package com.hallen.rfilemanager.infraestructure

import java.io.File
import javax.inject.Inject

class FileLister @Inject constructor() {

    suspend fun listFile(path: String) {
        val file = File(path)
        listFile(file)
    }

    suspend fun listFile(file: File): List<File>? {
        if (!file.canRead() || !file.exists()) return null
        val files = file.listFiles() ?: return null
        files.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })

        val directories = files.filter { it.isDirectory }
        val onlyFiles = files.filter { !it.isDirectory }
        return directories.plus(onlyFiles)
    }
}