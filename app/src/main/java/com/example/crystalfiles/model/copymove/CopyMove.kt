package com.example.crystalfiles.model.copymove

import android.content.Context
import android.widget.Toast
import androidx.work.*
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.view.MainActivity

class CopyMove(private val context: Context, private val readStorage: ReadStorage) {
    private var arrayOfSrc: Array<String?> = arrayOf()
    private var copyState = 0

    private fun copyMove(arraySrc: Array<String?>, dest: String, move: Boolean = false){
        val src = arraySrc[copyState]
        val data = Data.Builder()
        data.apply {
            putBoolean(FileCopyWorker.FileParams.KEY_MOVE, move)
            putString(FileCopyWorker.FileParams.KEY_SOURCE, src)
            putString(FileCopyWorker.FileParams.KEY_DEST, dest)
        }
        val fileCopyWorker = OneTimeWorkRequestBuilder<FileCopyWorker>().setInputData(data.build()).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork("oneFileCopyWork_${System.currentTimeMillis()}", ExistingWorkPolicy.KEEP, fileCopyWorker)

        workManager.getWorkInfoByIdLiveData(fileCopyWorker.id)
            .observe(context as MainActivity){ info->
                info?.let {
                    when(it.state){
                        WorkInfo.State.SUCCEEDED -> {
                            if (actualPath.absolutePath == dest){
                                readStorage.readStorage(actualPath)
                                if (copyState < arrayOfSrc.size - 1){
                                    copyState += 1
                                    copyMove(arrayOfSrc, dest, move)
                                }
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            Toast.makeText(context, "A ocurrido un error", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
        }
    }

    fun move(arraySrc: Array<String?>, dest: String){
        arrayOfSrc = arraySrc
        copyState = 0
        if (arrayOfSrc[0] != null) copyMove(arrayOfSrc, dest, true)
    }

    fun copy(arraySrc: Array<String?>, dest: String){
        arrayOfSrc = arraySrc
        copyState = 0
        if (arrayOfSrc[0] != null) copyMove(arrayOfSrc, dest)
    }
}