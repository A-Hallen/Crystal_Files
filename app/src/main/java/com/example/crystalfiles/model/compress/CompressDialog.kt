package com.example.crystalfiles.model.compress

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.example.crystalfiles.model.Global
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.filemanipulation.RenameStyleDialog
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.view.MainActivity
import com.example.crystalfiles.view.recyclerview.PictureFolderAdapter
import java.io.File


class CompressDialog(private val readStorage: ReadStorage) {

    fun show(context: Context, list: Array<String?>){
        val thisActualPath = actualPath
        val firstFile = File(list[0]!!)
        val i: Int = firstFile.name.lastIndexOf('.')
        val name = if (i != -1) firstFile.name.substring(0, i) + ".zip" else firstFile.name + ".zip"

        val dialog = RenameStyleDialog(context).apply {
            set(
                title = "Comprimir",
                password = true,
                negativeButtonListener = {  dismiss()   },
                positiveButtonListener = {
                    val data = Data.Builder()
                    data.apply {
                        putStringArray(FileCompressWorker.Lists.KEY_LIST, list)
                        putString(FileCompressWorker.Lists.KEY_NAME, getText())
                        putString(FileCompressWorker.Lists.KEY_PASSWORD, getPassword())
                    }
                    val fileCopyWorker = OneTimeWorkRequestBuilder<FileCompressWorker>().setInputData(data.build()).build()
                    val workManager = WorkManager.getInstance(context)
                    workManager.enqueueUniqueWork("oneCompressCopyWork_${System.currentTimeMillis()}", ExistingWorkPolicy.KEEP, fileCopyWorker)

                    workManager.getWorkInfoByIdLiveData(fileCopyWorker.id)
                        .observe(context as MainActivity){ info->
                            info?.let {
                                when(it.state){
                                    WorkInfo.State.SUCCEEDED -> {
                                        Toast.makeText(context, "Compresión exitosa", Toast.LENGTH_SHORT).show()
                                        if (thisActualPath == actualPath)   readStorage.readStorage(actualPath)
                                    }
                                    WorkInfo.State.FAILED -> {
                                        Toast.makeText(context, "A ocurrido un error en la compresión", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {}
                                }
                            }
                        }

                    readStorage.readStorage(actualPath)
                    dismiss()
                },
                textViewState = false,
                editState = true,
                message = null,
                renameText = name
            )
        }
        dialog.show()
    }

}
