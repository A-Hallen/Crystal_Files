package com.example.crystalfiles.model.list_files

import android.content.Context
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import java.io.File

/**
 * first function triggered when we start the app
 */
fun startReading(context: Context) {
    val readSorage = ReadStorage(context)
    val path: String = if (prefs.getRootLocation() != ""){
        prefs.getRootLocation()
    } else if (prefs.getLastLocation() != ""){
        prefs.getLastLocation()
    } else {
        drives[0]!!
    }

    readSorage.readStorage(File(path))
}