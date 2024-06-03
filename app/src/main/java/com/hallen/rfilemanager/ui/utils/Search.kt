package com.hallen.rfilemanager.ui.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class Search @Inject constructor(@ApplicationContext val context: Context) {
    private var thread: Thread? = null
    private var stop: Boolean = false

    fun showSearch(text: String, callback: (String) -> Unit) {
        if (text.isBlank()) return
        stop = true
        thread = object : Thread() {
            override fun run() {
                getNames(text, callback)
            }
        }
        (thread as Thread).start()
    }


    fun getNames(text: String, callback: (String) -> Unit) {
        val whereClause = MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ('%" + text + "%')"
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA
        )
        val cursor: Cursor =
            context.contentResolver.query(uri, projection, whereClause, null, null)!!
        stop = false

        if (cursor.moveToFirst()) {
            do {
                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths = ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)
                callback(paths)
            } while (cursor.moveToNext() && !stop)
        }
        cursor.close()
    }
}