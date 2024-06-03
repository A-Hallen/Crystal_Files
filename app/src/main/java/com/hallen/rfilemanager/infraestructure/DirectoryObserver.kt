package com.hallen.rfilemanager.infraestructure

import android.os.Build
import android.os.FileObserver
import androidx.annotation.RequiresApi
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
class DirectoryObserver(private val file: File) : FileObserver(file) {

    private var listener: AvailableSpaceChangeListener? = null

    fun setListener(listener: AvailableSpaceChangeListener) {
        this.listener = listener
    }

    interface AvailableSpaceChangeListener {
        fun onAvailableSpaceChange(availableSpace: Long, path: String?)
    }

    override fun onEvent(event: Int, path: String?) {
        if (listener == null) return
        if (event == MODIFY || event == CREATE || event == DELETE || event == MOVED_FROM || event == MOVED_TO) {
            val availableSpace = file.usableSpace
            listener?.onAvailableSpaceChange(availableSpace, path)
        }
    }
}