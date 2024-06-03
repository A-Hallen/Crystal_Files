package com.hallen.rfilemanager.infraestructure.fileactions.copy

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hallen.rfilemanager.model.Clipboard
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

typealias Params = FileCopyWorker.FileParams

class Copy @Inject constructor(@ApplicationContext val context: Context) {

    operator fun invoke(clipboard: Clipboard) {
        val source = clipboard.source ?: return
        val destiny = clipboard.destiny ?: return

        when (clipboard.action) {
            Clipboard.Action.COPY -> copy(source, destiny)
            Clipboard.Action.MOVE -> move(source, destiny)
            else -> {}
        }
    }

    fun pasteFromClipboard(clipboard: Clipboard) {

    }

    private fun move(source: List<String>, destiny: String) {
        moveCopy(source.toTypedArray(), destiny, true)
    }

    private fun copy(source: List<String>, destiny: String) {
        moveCopy(source.toTypedArray(), destiny, false)
    }

    private fun moveCopy(source: Array<String>, destiny: String, move: Boolean) {
        val data = Data.Builder().putBoolean(FileCopyWorker.FileParams.KEY_MOVE, move)
            .putStringArray(FileCopyWorker.FileParams.KEY_SOURCE, source)
            .putString(FileCopyWorker.FileParams.KEY_DEST, destiny).build()
        val fileCopyWorker = OneTimeWorkRequestBuilder<FileCopyWorker>().setInputData(data).build()
        val workManager = WorkManager.getInstance(context)
        val id = "oneFileCopyWork_${System.currentTimeMillis()}"
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, fileCopyWorker)

        /*workManager.getWorkInfoByIdLiveData(fileCopyWorker.id)
            .observe(context as MainActivity){ info ->
                when(info.state){
                    SUCCEEDED -> TODO()
                    FAILED -> TODO()
                    else -> {}
                }
            }*/
    }
}