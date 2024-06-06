package com.hallen.rfilemanager.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Search @Inject constructor(@ApplicationContext val context: Context) {
    private var searchJob: Job? = null

    fun showSearch(text: String, callback: (String) -> Unit) {
        if (text.isBlank()) return

        searchJob?.cancel() // Cancel any previous search

        searchJob = CoroutineScope(Dispatchers.Main).launch {
            val results = withContext(Dispatchers.IO) {
                searchFiles(text)
            }
            results.forEach { callback(it) }
        }
    }

    private fun searchFiles(text: String): List<String> {
        val results = mutableListOf<String>()
        val whereClause = MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?"
        val selectionArgs = arrayOf("%$text%")
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA
        )

        context.contentResolver.query(
            uri,
            projection,
            whereClause,
            selectionArgs,
            null
        )?.use { cursor ->
            val pathsColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (cursor.moveToNext()) {
                results.add(cursor.getString(pathsColumn))
            }
        }

        return results
    }
}