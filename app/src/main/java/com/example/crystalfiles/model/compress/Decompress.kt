package com.example.crystalfiles.model.compress

import android.content.Context
import android.widget.Toast
import androidx.work.*
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.filemanipulation.RenameStyleDialog
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.view.MainActivity
import net.lingala.zip4j.ZipFile
import java.io.File

class Decompress(private val context: Context,
                 private val readStorage: ReadStorage,
                 private val path: String
) {
    private var name: String = ""
    private var firstFile: File = File(path)
    private val thisActualPath = actualPath


    init {
        val i: Int = firstFile.name.lastIndexOf('.')
        name = if (i != -1) firstFile.name.substring(0, i) else firstFile.name
        val zipFile =  ZipFile(firstFile)
        if (zipFile.isValidZipFile){
            decompressZip(zipFile)
        } else {
            Toast.makeText(context, "El archivo ${firstFile.name} no es un archivo .zip válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decompressZip(zipFile: ZipFile) {
        var fileParentPath = actualPath.absolutePath + "/" + name
        var fileParent = File(fileParentPath)
        var x = 0
        while (fileParent.exists()){
            fileParentPath = actualPath.absolutePath + "/" + name + x.toString()
            fileParent = File(fileParentPath)
            x += 1
        }

        if (zipFile.isEncrypted){
            decompressEncryptedZip(fileParent)
        } else {
            decompressNormalZip(fileParent)
        }
    }

    private fun decompressNormalZip(fileParent: File) {
        val dialog = RenameStyleDialog(context).apply {
            set(
                title = "Descomprimir",
                negativeButtonListener = {  dismiss()   },
                positiveButtonListener = {
                    val firstFilePath = firstFile.absolutePath
                    val data = Data.Builder()
                    data.apply {
                        putString(FileDecompressWorker.Lists.KEY_PATH, firstFilePath)
                        putString(FileDecompressWorker.Lists.KEY_PASSWORD, "")
                        putBoolean(FileDecompressWorker.Lists.KEY_IS_ENCRYPTED, false)
                        putString(FileDecompressWorker.Lists.KEY_PARENT, fileParent.absolutePath)
                    }
                    val fileDecompressWorker = OneTimeWorkRequestBuilder<FileDecompressWorker>().setInputData(data.build()).build()
                    val workManager = WorkManager.getInstance(context)
                    workManager.enqueueUniqueWork("oneDecompressWork_${System.currentTimeMillis()}", ExistingWorkPolicy.KEEP, fileDecompressWorker)

                    workManager.getWorkInfoByIdLiveData(fileDecompressWorker.id)
                        .observe(context as MainActivity){ info->
                            info?.let {
                                when(it.state){
                                    WorkInfo.State.SUCCEEDED -> {
                                        Toast.makeText(context, "Decompresión exitosa", Toast.LENGTH_SHORT).show()
                                        if (thisActualPath == actualPath)   readStorage.readStorage(actualPath)
                                    }
                                    WorkInfo.State.FAILED -> {
                                        Toast.makeText(context, "A ocurrido un error durante la decompresión", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {}
                                }
                            }
                        }

                    if (thisActualPath == actualPath)   readStorage.readStorage(actualPath)
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

    private fun decompressEncryptedZip(fileParent: File) {
        val dialog = RenameStyleDialog(context).apply {
            set(
                title = "Descomprimir",
                password = true,
                negativeButtonListener = {  dismiss()   },
                positiveButtonListener = {
                    val firstFilePath = firstFile.absolutePath
                    val data = Data.Builder()
                    data.apply {
                        putString(FileDecompressWorker.Lists.KEY_PATH, firstFilePath)
                        putString(FileDecompressWorker.Lists.KEY_PASSWORD, getPassword())
                        putBoolean(FileDecompressWorker.Lists.KEY_IS_ENCRYPTED, true)
                        putString(FileDecompressWorker.Lists.KEY_PARENT, fileParent.absolutePath)
                    }
                    val fileDecompressWorker = OneTimeWorkRequestBuilder<FileDecompressWorker>().setInputData(data.build()).build()
                    val workManager = WorkManager.getInstance(context)
                    workManager.enqueueUniqueWork("oneDecompressWork_${System.currentTimeMillis()}", ExistingWorkPolicy.KEEP, fileDecompressWorker)

                    workManager.getWorkInfoByIdLiveData(fileDecompressWorker.id)
                        .observe(context as MainActivity){ info->
                            info?.let {
                                when(it.state){
                                    WorkInfo.State.SUCCEEDED -> {
                                        Toast.makeText(context, "Decompresión exitosa", Toast.LENGTH_SHORT).show()
                                        if (thisActualPath == actualPath)   readStorage.readStorage(actualPath)
                                    }
                                    WorkInfo.State.FAILED -> {
                                        Toast.makeText(context, "A ocurrido un error durante la decompresión", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {}
                                }
                            }
                        }

                    if (thisActualPath == actualPath)   readStorage.readStorage(actualPath)
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