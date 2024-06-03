package com.hallen.rfilemanager.infraestructure

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import androidx.lifecycle.MutableLiveData
import com.hallen.rfilemanager.model.Storage
import java.io.File
import javax.inject.Inject

class Storages @Inject constructor(private val context: Context) {
    var drives = MutableLiveData<List<Storage>>()

    init {
        updateStorages()
    }

    fun updateStorages() {
        //val storages = getAvailableStorages()
        val storages: List<Storage> = getAvailableStorages()
        drives.value = storages
    }


    /**
     * Returns all available external SD-Card roots in the system.
     *
     * @return paths to all available external SD-Card roots in the system.
     */
    private fun getStorageDirectories(): MutableList<Storage> {
        val results: MutableList<Storage> = ArrayList()
        val externalDirs: Array<File?> = context.applicationContext.getExternalFilesDirs(null)
        for (file in externalDirs) {
            file ?: continue
            val path: String = file.path.split("/Android")[0]
            val storage = Storage(path = path, description = file.name)
            results.add(storage)
        }
        return results
    }

    private fun getAvailableStorages(): List<Storage> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return getStorageDirectories()
        }

        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolume = storageManager.storageVolumes

        return storageVolume.mapNotNull { volume ->
            if (volume.state != "mounted") return@mapNotNull null
            val isRemovable = volume.isRemovable
            val isEmulated = volume.isEmulated

            Storage(
                path = volume.directory?.absolutePath ?: "",
                description = volume.getDescription(context),
                isEmulated = isEmulated,
                isRemovable = isRemovable,
                isPrimary = volume.isPrimary,
                state = volume.state,
            )
        }
    }
}

