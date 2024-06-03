package com.hallen.rfilemanager.infraestructure

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import java.io.File

class ExampleClass(
    private val file: File,
    private val context: Context,
    private val mime: String,
    private val prefs: Prefs,
) :
    MediaScannerConnection.MediaScannerConnectionClient {
    private var conn: MediaScannerConnection? = null
    fun notifiSystemWidthImage() {
        conn = MediaScannerConnection(context, this)
        conn!!.connect()
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        try {
            if (uri != null) {
                val arrayNames = prefs.getDefaultApp(mime)
                val packageName: String = arrayNames[0]
                val activityName: String = arrayNames[1]
                val name = ComponentName(packageName, activityName)
                val intento = Intent(Intent.ACTION_VIEW, uri)
                intento.setDataAndType(uri, mime)
                intento.addCategory(Intent.CATEGORY_LAUNCHER)
                intento.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                intento.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intento.component = name
                println(uri.toString())
                try {
                    context.startActivity(intento)
                } catch (e: ActivityNotFoundException) {
                }
            }
        } finally {
            conn?.disconnect()
            conn = null
        }
    }

    override fun onMediaScannerConnected() {
        conn?.scanFile(file.absolutePath, mime)
    }
}