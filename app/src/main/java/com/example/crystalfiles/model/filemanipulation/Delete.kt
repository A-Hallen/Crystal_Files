package com.example.crystalfiles.model.filemanipulation

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.Global.Companion.galeryScrollPosition
import com.example.crystalfiles.model.Global.Companion.imageFolderArray
import com.example.crystalfiles.model.Global.Companion.mode
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.model.list_files.Selection
import com.example.crystalfiles.view.recyclerview.PictureFolderAdapter
import com.skydoves.progressview.ProgressView
import java.io.File

class Delete(private val context: Context) {
    private var foldersDeleted = 0

    private fun deleteAll(file: File, total: Int){
        val listFiles = file.listFiles() ?: return
        for (subFile in listFiles){
            foldersDeleted++
            if (subFile.isDirectory){
                deleteAll(subFile, total)
                deleteFile(subFile)
            } else {
                deleteFile(subFile)
            }
        }
    }
    private fun deleteFile(file: File){
        try {
            file.delete()
        } catch (e: Exception){
            e.printStackTrace()
            return
        }
    }
    private fun countFolders(file: File):Int{
        var numsDirs = 0
        if (!file.isDirectory){
            return 0
        }
        if (!file.exists()){
            Log.i("ERROR", "El archivo solicitado no existe")
        }
        repeat(file.walkTopDown().count()) {
            numsDirs++
        }
        return numsDirs
    }

    fun deleteFun(arrayFile: Array<String?>,
                  progressView: ProgressView,
                  newRecyclerView: RecyclerView,
                  readStorage: ReadStorage,
                  selection: Selection) {

        val message:String = if (arrayFile.size == 1){
            "Seguro que deseas eliminar: ${File(arrayFile[0]!!).name}?"
        } else {  "Seguro que deseas eliminar estos ${arrayFile.size} elementos?"  }

        val dialog = OneStyleAlertDialog(context ).apply {
            set(
                title = "Eliminar",
                message = message,
                negativeButtonText = "Cancelar",
                positiveButtonText = "OK",
                positiveButtonListener = {
                    dismiss()
                    for (path in arrayFile){
                        val file: File
                        if (path != null){
                            file = File(path)
                        } else return@set
                        if (!file.exists() && !file.canWrite() && !file.canRead()) return@set
                        val total = countFolders(file)
                        if (file.isFile) deleteFile(file) else {
                            deleteAll(file, total)
                            deleteFile(file)
                        }
                    }

                    if (progressView.visibility == View.VISIBLE) {
                        progressView.visibility = View.INVISIBLE
                    }
                    val mensaje =
                        if (arrayFile.size == 1) "El archivo ${File(arrayFile[0]!!).name} a sido eliminado" else {
                            "Se han eliminado los archivos"
                        }
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    galeryScrollPosition = newRecyclerView.layoutManager?.onSaveInstanceState()!!
                    if (mode) {
                        newRecyclerView.adapter = PictureFolderAdapter(
                            imageFolderArray,
                            context,
                            selection,
                            selection
                        )
                    } else {
                        readStorage.readStorage(actualPath)
                    }
                    (newRecyclerView.layoutManager as GridLayoutManager).onRestoreInstanceState(galeryScrollPosition)
                },
                negativeButtonListener = {
                    dismiss()
                }
            )
        }
        dialog.show()


    }

}